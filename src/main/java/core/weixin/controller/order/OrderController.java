/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.jfinal.MsgInterceptor;
import com.jfinal.weixin.sdk.utils.HttpUtils;

import core.model.Food;
import core.model.Order;
import core.model.Shop;
import core.model.User;
import core.utils.MD5Util;
import core.vo.JSONError;
import core.vo.JSONSuccess;
import core.weixin.controller.WeixinMsgController;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月24日下午10:03:40
 */

public class OrderController extends WeixinMsgController {
	private static final String WEIXIN_PRE_PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	private Log log = Log.getLog(OrderController.class);
	private String[] getType = { "" };

	@Before(MsgInterceptor.class)
	public void index() {
		String openid = getAttr("openId");
		User user = User.dao.findById(openid);
		setAttr("user", user);
		render("index.html");
	}

	//
	// public void testPay() {
	// String prepay_id = getPara("orderId");
	// setAttr("timeStamp", new Date().getTime() / 1000);
	// String appId = "wx7d654593ab6ec6a4";
	// String packageString = null;
	// String signType = "MD5";
	// Random random = new Random();
	// StringBuilder builder = new StringBuilder();
	// for (int i = 0; i < 16; i++)
	// builder.append((char) (random.nextInt(26) + 'A'));
	// String nonce_str = builder.toString();
	// System.out.println("nonce_str " + nonce_str);
	// try {
	// packageString = URLEncoder.encode("prepay_id=" + prepay_id, "UTF-8");
	// } catch (UnsupportedEncodingException e) {
	// }
	// String sign = "appId=" + PropKit.get("appId") + "&nonce_str=" + nonce_str
	// + "&package=" + packageString
	// + "&signType=" + signType + "&key=1";
	// sign = MD5Util.encrypt(sign).toUpperCase();
	// System.out.println(sign);
	// setAttr("appId", appId);
	// setAttr("package", packageString);
	// setAttr("signType", signType);
	// setAttr("nonce_str", nonce_str);
	// render("testPay.html");
	// }
	/**
	 * 参数：<br>
	 * userid<br>
	 * shopid<br>
	 * userAddress<br>
	 * userTel<br>
	 * userName<br>
	 * 返回值:<br>
	 * 成功时：<br>
	 * orderid<br>
	 * prepay_id<br>
	 * 
	 */
	public void createOrder() {
		/** 返回的json */
		JSONObject result = new JSONObject();
		String userid = getPara("userid");
		setAttr("userid", userid);
		Integer shopid = getParaToInt("shopid");
		String userAddress = getPara("userAddress");
		String userTel = getPara("userTel");
		String userName = getPara("userName");
		Shop shop = Shop.dao.findById(shopid);
		JSONArray foodArray = JSONArray.parseArray(getPara("foods"));
		/** 计算价格 */
		double totalPrice = 0;
		for (Object object : foodArray) {
			JSONObject foodObject = (JSONObject) object;
			Food food = Food.dao.findById(foodObject.get("id"));
			totalPrice += food.getDouble("PRICE") * foodObject.getIntValue("num");
		}
		double tempTotalPrice = totalPrice;
		if (shop.getDouble("DELIVERY_OFF_THRESHOLD") != null && shop.getDouble("DELIVERY_OFF_THRESHOLD") != 0
				&& tempTotalPrice > shop.getDouble("DELIVERY_OFF_THRESHOLD")) {
			totalPrice -= shop.getDouble("DELIVERY_OFF");
		}
		if (shop.getDouble("REDUCTION_THRESHOLD") != null && shop.getDouble("REDUCTION_THRESHOLD") != 0
				&& tempTotalPrice > shop.getDouble("REDUCTION_THRESHOLD")) {
			totalPrice -= shop.getDouble("REDUCTION");
		}
		totalPrice += shop.getDouble("DELIVERY_PRICE");
		/** 新建订单 **/
		Order order = new Order();
		order.set("SHOP_ID", shopid);
		order.set("USER_ID", userid);
		order.set("FOODS", getPara("foods"));
		order.set("ORDER_STATE", 1);
		order.set("PAY_STATE", 0);
		order.set("TOTAL_PRICE", tempTotalPrice);
		order.set("PAY_PRICE", totalPrice);
		order.set("CREATE_TIME", new Date());
		order.set("USER_ADDRESS", userAddress);
		order.set("USER_TEL", userTel);
		order.set("USER_NAME", userName);
		order.save();
		result.put("orderid", order.getInt("ID"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		java.util.Map<String, String> keyMap = new HashMap<>();
		keyMap.put("appid", PropKit.get("appId"));
		keyMap.put("mch_id", PropKit.get("mch_id"));
		keyMap.put("device_info", "WEB");
		StringBuilder nonceNorBuilder = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 16; i++)
			nonceNorBuilder.append((char) (random.nextInt(26) + 'A'));
		keyMap.put("nonce_str", nonceNorBuilder.toString());
		keyMap.put("sign_type", "MD5");
		keyMap.put("body", "订单支付");
		keyMap.put("out_trade_no", order.getInt("ID") + "");
		keyMap.put("fee_type", "CNY");
		keyMap.put("total_fee", ((int) (totalPrice * 100)) + "");
		keyMap.put("spbill_create_ip", PropKit.get("spbill_create_ip"));
		keyMap.put("notify_url", PropKit.get("server.address") + "/wx/orderRecieve/orderPayed");
		keyMap.put("trade_type", "JSAPI");
		keyMap.put("openid", userid);
		String sign = getSign(keyMap);
		keyMap.put("sign", sign);
		String urlResult = HttpUtils.post(WEIXIN_PRE_PAY_URL, new String(callMapToXML(keyMap)));

		Document doc = Jsoup.parse(urlResult);
		if ("SUCCESS".equals(doc.getElementsByTag("result_code").text())) {
			long timeStamp = new Date().getTime() / 1000;
			nonceNorBuilder = new StringBuilder();
			random = new Random();
			for (int i = 0; i < 16; i++)
				nonceNorBuilder.append((char) (random.nextInt(26) + 'A'));
			String prepayId = doc.getElementsByTag("prepay_id").text();
			String appId = PropKit.get("appId");
			String package1 = "prepay_id=" + doc.getElementsByTag("prepay_id").text();
			String signType = "MD5";
			order.set("PREPAY_ID", prepayId);
			order.update();
			result.put("appId", appId);
			result.put("timeStamp", timeStamp + "");
			result.put("nonceStr", nonceNorBuilder.toString());
			result.put("package1", package1);
			result.put("signType", signType);
			Map<String, String> map = new HashMap<>();
			map.put("appId", appId);
			map.put("timeStamp", timeStamp + "");
			map.put("nonceStr", nonceNorBuilder.toString());
			map.put("package", package1);
			map.put("signType", signType);
			result.put("paySign", getSign(map));
			renderJson(new JSONSuccess(result));
		} else {
			renderJson(new JSONError(doc.getElementsByTag("return_msg").text()));
		}
	}

	public void orderPayed() {
		Map<String, String[]> paraMap = getParaMap();
		for (Entry<String, String[]> entry : paraMap.entrySet()) {
			System.out.println(entry.getKey());
			for (String string : entry.getValue()) {
				System.out.println("\t" + string);
			}
		}
		render("orderPayed.html");
	}

	public String getSign(Map<String, String> keyMap) {
		List<Entry<String, String>> list = new ArrayList<Entry<String, String>>(keyMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
				return (o1.getKey().compareTo(o2.getKey()));
			}
		});
		StringBuilder paraBuilder = new StringBuilder();
		for (Entry<String, String> entry : list) {
			paraBuilder.append(entry.getKey());
			paraBuilder.append("=");
			paraBuilder.append(entry.getValue());
			paraBuilder.append("&");
		}
		paraBuilder.append("key");
		paraBuilder.append("=");
		paraBuilder.append(PropKit.get("mch_key"));
		// System.out.println(paraBuilder.toString());
		return MD5Util.encrypt(paraBuilder.toString()).toUpperCase();
	}

	public static byte[] callMapToXML(Map map) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		mapToXMLTest2(map, sb);
		sb.append("</xml>");
		try {
			return sb.toString().getBytes("UTF-8");
		} catch (Exception e) {
		}
		return null;
	}

	private static void mapToXMLTest2(Map map, StringBuffer sb) {
		Set set = map.keySet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = map.get(key);
			if (null == value)
				value = "";
			if (value.getClass().getName().equals("java.util.ArrayList")) {
				ArrayList list = (ArrayList) map.get(key);
				sb.append("<" + key + ">");
				for (int i = 0; i < list.size(); i++) {
					HashMap hm = (HashMap) list.get(i);
					mapToXMLTest2(hm, sb);
				}
				sb.append("</" + key + ">");

			} else {
				if (value instanceof HashMap) {
					sb.append("<" + key + ">");
					mapToXMLTest2((HashMap) value, sb);
					sb.append("</" + key + ">");
				} else {
					sb.append("<" + key + ">" + value + "</" + key + ">");
				}

			}

		}
	}

	public void recieveCreateOrder() {

	}

	/**
	 * orderType:distance 按距离排序 rate:好评数排序 没有参数：随机排序<br>
	 * typeId: shopType表中的id 默认:所有<br>
	 * userLongitude: 用户经度 <br>
	 * userLatidute: 用户纬度 <br>
	 * page:当前页 从1开始 默认: 1<br>
	 * pageCount: 每页数量 默认: 20<br>
	 * pccode:地区编码
	 * 
	 */
	// weifg 已移植到shop里
	/*
	 * public void getShopList() { String openid = getPara("openid");
	 * setAttr("openid", openid); String orderType = getPara("orderType");
	 * String typeId = getPara("typeId"); String userLongitude = getPara("lng");
	 * String userLatidute = getPara("lat"); String pccode = getPara("pccode");
	 * Integer page = getParaToInt("page"); if (page == null || page <= 0) page
	 * = 1; Integer pageCount = getParaToInt("pageCount"); if (pageCount == null
	 * || pageCount <= 0 || pageCount >= 20) pageCount = 20; String orderBy =
	 * ""; orderBy = " (LONGITUDE-" + userLongitude + ")* (LONGITUDE-" +
	 * userLongitude + ")+ (LATITUDE-" + userLatidute + ")* (LATITUDE-" +
	 * userLatidute + ") ASC "; if (typeId == null) typeId = ""; else typeId =
	 * " AND ID IN (SELECT SHOPID FROM T_SHOP_TYPE_RELATION WHERE TYPEID='" +
	 * typeId + "' "; if (pccode == null) pccode = ""; else pccode =
	 * " AND PCCODE='" + pccode + "' "; DecimalFormat df = new
	 * java.text.DecimalFormat("0.00"); String sql =
	 * "SELECT * FROM T_SHOP WHERE " + " STATE=2  " + pccode + typeId +
	 * " ORDER BY " + orderBy + " LIMIT " + (pageCount * (page - 1)) + "," +
	 * pageCount; List<Record> shops = Db.find(sql); for (Record record : shops)
	 * { record.set("distance",
	 * df.format((LocationUtils.getDistance(Double.valueOf(userLongitude),
	 * Double.valueOf(userLatidute), record.getDouble("LONGITUDE"),
	 * record.getDouble("LATITUDE")))) + "km"); } renderJson(new
	 * JSONSuccess(shops)); }
	 */

	/**
	 * return : {error:"",data{ shop(店铺信息 object):"" ,typeList(分类
	 * object){ID:"",NAME:"",foods(商品array)[]} } }
	 */
	// weifg 已有
	/*
	 * public void getShopInfo() { String openid = getPara("openid");
	 * setAttr("openid", openid); String shopId = getPara("shopId"); if (shopId
	 * == null) { renderJson(new JSONError("没有shopid")); return; } JSONObject
	 * result = new JSONObject(); Shop shop = Shop.dao.findById(shopId);
	 * result.put("shop", shop); List<Record> foods = Db.find(
	 * "SELECT * FROM T_FOOD WHERE SHOP_ID=? AND STATE=1", shopId); List<Record>
	 * foodTypes = Db. find(
	 * "SELECT `ID`,`NAME` FROM T_FOOD_TYPE WHERE `DELETED`=0 AND `SHOP_ID`=?" ,
	 * shopId); for (Record foodType : foodTypes) { Integer foodTypeId =
	 * foodType.getInt("ID"); JSONArray foodArray = new JSONArray(); for (Record
	 * food : foods) if (foodTypeId.equals(food.getInt("TYPE_ID")))
	 * foodArray.add(food); foodType.set("foods", foodArray); }
	 * result.put("typeList", foodTypes); renderJson(new JSONSuccess(result)); }
	 */

	// weifg 已有
	/*
	 * public void interShop() { String openid = getPara("openid");
	 * setAttr("openid", openid); String shopId = getPara("shopId"); Shop shop =
	 * Shop.dao.findById(shopId); setAttr("shop", shop); List<Record> foods =
	 * Db.find("SELECT * FROM T_FOOD WHERE SHOP_ID=?", shopId); setAttr("foods",
	 * foods); render("interShop.html"); }
	 */

}
