/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.order;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.ehcache.CacheKit;
import core.model.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.jfinal.MsgInterceptor;
import com.jfinal.weixin.sdk.utils.HttpUtils;

import core.utils.WeiXinUtils;
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

	public void payList() throws UnsupportedEncodingException {
		UserAddress userAddress = UserAddress.dao.findFirst(Db.getSql(""));
		render("payList.html");
	}

	public void ajaxPreOrder(){
		JSONObject request = JSONObject.parseObject(HttpKit.readData(getRequest()));
		String openId = request.getString("openId");
		JSONObject result = new JSONObject();
		Order order = new Order();
		order.set("USER_ID",openId);
		if(StringUtils.isNotEmpty(openId)){
			CacheKit.remove("preOrder",openId);//清空缓存
			JSONObject foodInfo = request.getJSONObject("foods");
			if(foodInfo.size()>0){
				double totalPrice = 0;
				Integer shopId = null;
				JSONArray tmpArray = new JSONArray();
				List<Food> foods = new ArrayList<>();
				for(String key:foodInfo.keySet()){
					Integer num = foodInfo.getIntValue(key);
					if(num!=null && num>0){
						Food food = Food.dao.findById(key);
						if(food!=null){
							if(order.get("SHOP_ID")==null){//初始化shopID
								order.set("SHOP_ID",shopId=food.get("SHOP_ID"));
							}

							double price = food.getDouble("PRICE");
							totalPrice+=(price*num);

							foods.add(food);
						}else{
							renderJson(new JSONError("订单中含有已下架/删除的商品，请重新选购"));
						}

						JSONObject tmpJson = new JSONObject();
						tmpJson.put("id",key);
						tmpJson.put("num",num);
						tmpArray.add(tmpJson);
					}
				}
				Shop shop = Shop.dao.findById(shopId);
				if(shop!=null){
					totalPrice += shop.getDouble("DELIVERY_PRICE");
					order.set("TOTAL_PRICE",totalPrice);
					//优惠计算
					if (shop.getDouble("DELIVERY_OFF_THRESHOLD") != null && shop.getDouble("DELIVERY_OFF_THRESHOLD") != 0
							&& totalPrice > shop.getDouble("DELIVERY_OFF_THRESHOLD")) {
						totalPrice -= shop.getDouble("DELIVERY_OFF");
					}
					if (shop.getDouble("REDUCTION_THRESHOLD") != null && shop.getDouble("REDUCTION_THRESHOLD") != 0
							&& totalPrice > shop.getDouble("REDUCTION_THRESHOLD")) {
						totalPrice -= shop.getDouble("REDUCTION");
					}
					order.set("PAY_PRICE", totalPrice);
				}else{
					renderJson(new JSONError("您选的商品所在的店铺不存在哦~"));
				}
				order.set("FOODS",tmpArray.toJSONString());
				CacheKit.put("preOrder",openId,order);//更新缓存
				result.put("SHOP",shop);
				result.put("PRE_ORDER",JSONObject.parseObject(order.toJson()));
				result.put("FOODS",foods);
				renderJson(new JSONSuccess(result));
			}else{
				renderJson(new JSONError("您还没有选择任何美食哦~"));
			}
		}else{
			renderJson(new JSONError("没有获取到openId"));
		}
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
	public void pay() {
		/** 返回的json */
		JSONObject result = new JSONObject();
		String openId = getPara("openId");
		String address = getPara("address");
		String phone = getPara("phone");
		String name = getPara("name");
		/** 更新订单 **/
		Order order = CacheKit.get("preOrder",openId);
		order.set("USER_ADDRESS", address);
		order.set("USER_TEL", phone);
		order.set("USER_NAME", name);
		order.update();
		result.put("orderid", order.getInt("ID"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		java.util.Map<String, String> keyMap = new HashMap<>();
		keyMap.put("appid", PropKit.get("appId"));
		keyMap.put("mch_id", PropKit.get("mch_id"));
		keyMap.put("device_info", "WEB");
		keyMap.put("nonce_str", WeiXinUtils.getNonceNorString());
		keyMap.put("sign_type", "MD5");
		keyMap.put("body", "订单支付");
		keyMap.put("out_trade_no", order.getInt("ID") + "");
		keyMap.put("fee_type", "CNY");
		keyMap.put("total_fee", order.getInt("PAY_PRICE")*100+"");
		keyMap.put("spbill_create_ip", PropKit.get("spbill_create_ip"));
		keyMap.put("notify_url", PropKit.get("server.address") + "/wx/orderRecieve/orderPayed");
		keyMap.put("trade_type", "JSAPI");
		keyMap.put("openid", openId);
		keyMap.put("sign", WeiXinUtils.getSign(keyMap));
		String paraXml = new String(WeiXinUtils.callMapToXML(keyMap));
		System.out.println(paraXml);
		String urlResult = HttpUtils.post(WEIXIN_PRE_PAY_URL, paraXml);

		Document doc = Jsoup.parse(urlResult);
		if ("SUCCESS".equals(doc.getElementsByTag("result_code").text())) {
			long timeStamp = new Date().getTime() / 1000;
			String nonceNor = WeiXinUtils.getNonceNorString();
			String prepayId = doc.getElementsByTag("prepay_id").text();
			String appId = PropKit.get("appId");
			String package1 = "prepay_id=" + doc.getElementsByTag("prepay_id").text();
			String signType = "MD5";
			order.set("PREPAY_ID", prepayId);
			order.update();
			result.put("appId", appId);
			result.put("timeStamp", timeStamp + "");
			result.put("nonceStr", nonceNor);
			result.put("package1", package1);
			result.put("signType", signType);
			Map<String, String> map = new HashMap<>();
			map.put("appId", appId);
			map.put("timeStamp", timeStamp + "");
			map.put("nonceStr", nonceNor);
			map.put("package", package1);
			map.put("signType", signType);
			result.put("paySign", WeiXinUtils.getSign(map));
			renderJson(new JSONSuccess(result));
		} else {
			System.out.println(urlResult);
			renderJson(new JSONError(doc.getElementsByTag("return_msg").text()));
		}
	}

	// public void orderPayed() {
	// Map<String, String[]> paraMap = getParaMap();
	// for (Entry<String, String[]> entry : paraMap.entrySet()) {
	// System.out.println(entry.getKey());
	// for (String string : entry.getValue()) {
	// System.out.println("\t" + string);
	// }
	// }
	// render("orderPayed.html");
	// }

	// public void recieveCreateOrder() {
	//
	// }

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
