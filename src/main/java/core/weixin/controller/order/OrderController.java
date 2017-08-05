/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.order;

import java.text.DecimalFormat;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.jfinal.MsgInterceptor;

import core.model.Shop;
import core.model.User;
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
	private static final double EARTH_RADIUS = 6378137;
	private Log log = Log.getLog(OrderController.class);
	private String[] getType = { "" };

	@Before(MsgInterceptor.class)
	public void index() {
		String openid = getAttrForStr("openid");
		User user = User.dao.findById(openid);
		setAttr("user", user);
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
	public void getShopList() {
		String openid = getPara("openid");
		setAttr("openid", openid);
		String orderType = getPara("orderType");
		String typeId = getPara("typeId");
		String userLongitude = getPara("userLongitude");
		String userLatidute = getPara("userLatidute");
		String pccode = getPara("pccode");
		Integer page = getParaToInt("page");
		if (page == null || page <= 0)
			page = 1;
		Integer pageCount = getParaToInt("pageCount");
		if (pageCount == null || pageCount <= 0 || pageCount >= 20)
			pageCount = 20;
		String orderBy = "";
		if ("distance".equals(orderType))
			orderBy = " (LONGITUDE-" + userLongitude + ")* (LONGITUDE-" + userLongitude + ")+ (LATIDUTE-" + userLatidute
					+ ")* (LATIDUTE-" + userLatidute + ") ASC ";
		else if ("rate".equals(orderType))
			orderBy = " RATE_AVG DESC ";
		else
			orderBy = " RAND() ";
		if (typeId == null)
			typeId = "";
		else
			typeId = " AND ID IN (SELECT SHOPID FROM T_SHOP_TYPE_RELATION WHERE TYPEID='" + typeId + "' ";
		if (pccode == null)
			pccode = "";
		else
			pccode = " AND PCCODE='" + pccode + "' ";
		DecimalFormat df = new java.text.DecimalFormat("0.00");
		List<Record> shops = Db.find("SELECT * FROM T_SHOP WHERE " + " STATE=2  " + pccode + typeId + " ORDER BY "
				+ orderBy + " LIMIT " + (pageCount * (page - 1)) + "," + pageCount);
		for (Record record : shops) {
			record.set("distance", df.format((getDistance(Double.valueOf(userLongitude), Double.valueOf(userLatidute),
					record.getDouble("LONGITUDE"), record.getDouble("LATIDUTE")))) + "km");
		}
		renderJson(new JSONSuccess(shops));
	}

	/**
	 * return : {error:"",data{ shop(店铺信息 object):"" ,typeList(分类
	 * object){ID:"",NAME:"",foods(商品array)[]} } }
	 */
	public void getShopInfo() {
		String openid = getPara("openid");
		setAttr("openid", openid);
		String shopId = getPara("shopId");
		if (shopId == null) {
			renderJson(new JSONError("没有shopid"));
			return;
		}
		JSONObject result = new JSONObject();
		Shop shop = Shop.dao.findById(shopId);
		result.put("shop", shop);
		List<Record> foods = Db.find("SELECT * FROM T_FOOD WHERE SHOP_ID=? AND STATE=1", shopId);
		List<Record> foodTypes = Db.find("SELECT `ID`,`NAME` FROM T_FOOD_TYPE WHERE `DELETED`=0 AND `SHOP_ID`=?",
				shopId);
		for (Record foodType : foodTypes) {
			Integer foodTypeId = foodType.getInt("ID");
			JSONArray foodArray = new JSONArray();
			for (Record food : foods)
				if (foodTypeId.equals(food.getInt("TYPE_ID")))
					foodArray.add(food);
			foodType.set("foods", foodArray);
		}
		result.put("typeList", foodTypes);
		renderJson(new JSONSuccess(result));
	}

	public void interShop() {
		String openid = getPara("openid");
		setAttr("openid", openid);
		String shopId = getPara("shopId");
		Shop shop = Shop.dao.findById(shopId);
		setAttr("shop", shop);
		List<Record> foods = Db.find("SELECT * FROM T_FOOD WHERE SHOP_ID=?", shopId);
		setAttr("foods", foods);
		render("interShop.html");
	}

	/**
	 * 转化为弧度(rad)
	 */
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 基于googleMap中的算法得到两经纬度之间的距离,计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下
	 * 
	 * @param lon1
	 *            第一点的精度
	 * @param lat1
	 *            第一点的纬度
	 * @param lon2
	 *            第二点的精度
	 * @param lat3
	 *            第二点的纬度
	 * @return 返回的距离，单位km
	 */
	public static double getDistance(double lon1, double lat1, double lon2, double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lon1) - rad(lon2);
		double s = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS / 1000;
		// s = Math.round(s * 10000) / 10000;
		return s;
	}
}
