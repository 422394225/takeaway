package core.admin.controller.delivery;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import core.admin.service.delivery.DeliveryService;
import core.admin.service.delivery.impl.DeliveryServiceImpl;
import core.model.Delivery;
import core.utils.MD5Util;
import core.vo.DTParams;
import core.vo.JSONError;
import core.vo.JSONSuccess;

/**
 * 
 * Description:
 * 
 * @author hjk
 * @date 2017年1月30日下午2:40:27
 */
public class DeliveryController extends Controller {

	private Log log = Log.getLog(DeliveryController.class);
	private DeliveryService deliveryService = new DeliveryServiceImpl();
	private final static String ENCRIPT_KEY = PropKit.get("encrypt_key");

	public void index() {
		render("list.html");
	}

	public void getData() {
		DTParams params = new DTParams(getParaMap());
		Page<Record> foods = deliveryService.getDTPage(params, "delivery.list");
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", foods.getTotalRow());
		List<Record> list = foods.getList();
		result.put("data", list);
		result.put("recordsFiltered", foods.getTotalRow());
		renderJson(result);
	}

	public void placeSingle() {
		Delivery delivery = Delivery.dao.findById(getPara("deliveryId"));
		setAttr("LONGITUDE", delivery.get("NOW_LONGITUDE"));
		setAttr("LATIDUTE", delivery.get("NOW_LATIDUTE"));
		setAttr("deliveryId", getPara("deliveryId"));
		setAttr("amapKey", PropKit.get("amap.key"));
		render("placeSingle.html");

	}

	public void getPlaceDot() {
		String deliveryIdString = getPara("deliveryId");
		if (deliveryIdString != null) {
			String[] deliveryIds = deliveryIdString.split(",");
			JSONArray result = new JSONArray();
			for (String deliveryId : deliveryIds) {
				Delivery delivery = Delivery.dao.findById(deliveryId);
				if (delivery.getInt("STATE") == 1) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("NAME", delivery.get("NAME"));
					JSONArray place = new JSONArray();
					place.add(delivery.get("NOW_LONGITUDE"));
					place.add(delivery.get("NOW_LATIDUTE"));
					jsonObject.put("PLACE", place);
					result.add(jsonObject);
				}
			}
			renderJson(result);
		} else {
			JSONArray result = new JSONArray();
			List<Delivery> deliverys = Delivery.dao.find("select * from t_delivery where state='1'");
			for (Delivery delivery : deliverys) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("NAME", delivery.get("NAME"));
				JSONArray place = new JSONArray();
				place.add(delivery.get("NOW_LONGITUDE"));
				place.add(delivery.get("NOW_LATIDUTE"));
				jsonObject.put("PLACE", place);
				result.add(jsonObject);
			}
			renderJson(result);
		}
	}

	public void allDeliveryPlace() {
		setAttr("amapKey", PropKit.get("amap.key"));
		List<Delivery> deliverys = Delivery.dao.find("select * from t_delivery where state='1'");
		double LONGITUDE = 0;
		double LATIDUTE = 0;
		for (Delivery delivery : deliverys) {
			LONGITUDE += delivery.getDouble("NOW_LONGITUDE");
			LATIDUTE += delivery.getDouble("NOW_LATIDUTE");
		}
		LONGITUDE /= deliverys.size();
		LATIDUTE /= deliverys.size();
		setAttr("LONGITUDE", LONGITUDE);
		setAttr("LATIDUTE", LATIDUTE);
		render("allDeliveryPlace.html");
	}

	public void edit() {
		String deliveryId = getPara("deliveryId");
		Record record = Db.findFirst(
				"select a.*,b.`name` as CITY from t_delivery a left join region_code b on a.pccode=b.`code` where a.id=?",
				deliveryId);
		setAttr("deliveryId", record.get("ID"));
		setAttr("USERNAME", record.getStr("USERNAME"));
		setAttr("NAME", record.getStr("NAME"));
		setAttr("TEL", record.getStr("TEL"));
		System.out.println(record.getStr("CITY"));
		setAttr("AREA", record.getStr("CITY"));
		// List<Record> records = Db.find("select * from t_food_type where
		// SHOP_ID=? and deleted='0'",
		// record.get("SHOP_ID") + "");
		// JSONArray jsonArray = new JSONArray();
		// for (Record record2 : records) {
		// JSONObject jsonObject = new JSONObject();
		// jsonObject.put("ID", record2.get("ID"));
		// jsonObject.put("NAME", record2.get("NAME"));
		// jsonArray.add(jsonObject);
		// }
		// setAttr("typeData", jsonArray.toJSONString());
		render("edit.html");
	}

	public void saveEdit() {
		String deliveryId = getPara("deliveryId");
		String NEWPASSWORD = getPara("NEWPASSWORD");
		String NEWPASSWORD1 = getPara("NEWPASSWORD1");
		String NAME = getPara("NAME");
		String TEL = getPara("TEL");
		String area = getPara("area");
		if (!NEWPASSWORD.equals(NEWPASSWORD1)) {
			renderJson(new JSONError("输两次相同的密码都能出错(⊙﹏⊙)b"));
			return;
		}
		Record record = Db.findFirst("select `code` from region_code where name=?", area);
		if (record == null) {
			renderJson(new JSONError("这个世界上没有这个地方！！"));
			return;
		}
		Delivery delivery = new Delivery();
		delivery.set("ID", deliveryId);
		delivery.set("PASSWORD", MD5Util.encrypt(NEWPASSWORD + ENCRIPT_KEY));
		delivery.set("NAME", NAME);
		delivery.set("TEL", TEL);
		String pccode = "" + record.getInt("code");
		delivery.set("PCCODE", pccode);
		delivery.set("PCODE", pccode.subSequence(0, 2) + "0000");
		delivery.update();
		renderJson(new JSONSuccess("保存成功O(∩_∩)O~"));
	}

	public void getArea() {
		String keyWord = getPara("keyWord");
		List<Record> records = Db.find("select * from region_code where NAME like '%" + keyWord + "%'");
		JSONArray array = new JSONArray();
		int i = 0;
		for (Record record : records) {
			JSONObject object = new JSONObject();
			object.put("ename", record.getStr("NAME"));
			array.add(object);
			if (++i == 10)
				break;
		}
		System.out.println(array);
		renderJson(array);
	}

	public void lizhi() {
		try {
			String deliveryId = getPara("deliveryId");
			Db.update("update t_delivery set state=-1 where id=?", deliveryId);
		} catch (Exception e) {
			renderText(e.getMessage());
			return;
		}
		renderText("成功");
	}

	public void huifu() {
		try {
			String deliveryId = getPara("deliveryId");
			Db.update("update t_delivery set state=0 where id=?", deliveryId);
		} catch (Exception e) {
			renderText(e.getMessage());
			return;
		}
		renderText("成功");
	}

	public void save() {
		try {
			String userName = getPara("USERNAME");
			Record record = Db.findFirst("select * from t_delivery where username=?", userName);
			if (record != null) {
				renderJson(new JSONError("用户名不能使用啦！"));
				return;
			}
			String password = getPara("PASSWORD");
			String name = getPara("NAME");
			String tel = getPara("TEL");
			String area = getPara("area");
			record = Db.findFirst("select `code` from region_code where name=?", area);
			if (record == null) {
				renderJson(new JSONError("这个世界上没有这个地方！！"));
				return;
			}
			String pccode = "" + record.getInt("code");
			Delivery delivery = new Delivery();
			delivery.set("USERNAME", userName);
			delivery.set("PASSWORD", MD5Util.encrypt(password + ENCRIPT_KEY));
			delivery.set("NAME", name);
			delivery.set("TEL", tel);
			delivery.set("PCCODE", pccode);
			delivery.set("PCODE", pccode.subSequence(0, 2) + "0000");
			delivery.save();
			renderJson(new JSONSuccess("添加成功"));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONError(e.getMessage()));

		}
	}

	//
	// public void saveEdit() {
	// try {
	// Food food = new Food();
	// try {
	// UploadFile file = getFile("IMG");
	// if (file != null) {
	// String localFilePath = file.getUploadPath() + File.separator +
	// file.getFileName();
	// String path = QiniuUtils.upload(localFilePath);
	// String crop = getPara("crop");// 截图参数
	// food.set("IMG", path + (StringUtils.isNotEmpty(crop) ? crop : ""));
	// }
	// } catch (Exception e) {
	// renderJson(new JSONError("图片错误"));
	// return;
	// }
	// String shopName = getPara("shopName");
	// Shop shop = deliveryService.findByShopName(shopName);
	// if (shop == null) {
	// renderJson(new JSONError("店铺不存在"));
	// return;
	// }
	// String foodId = getPara("foodId");
	// String name = getPara("NAME");
	// String price = getPara("price");
	// String UNIT = getPara("UNIT");
	// String DESCRIPTION = getPara("DESCRIPTION");
	// Boolean USE_STOCK = getParaToBoolean("USE_STOCK");
	// String STOCK = getPara("STOCK");
	// String TYPE = getPara("TYPE");
	// String ORIGN_PRICE = getPara("ORIGN_PRICE");
	// food.set("ID", foodId);
	// food.set("SHOP_ID", shop.get("ID"));
	// food.set("NAME", name);
	// food.set("PRICE", price);
	// food.set("TYPE_ID", TYPE);
	// food.set("ORIGN_PRICE", ORIGN_PRICE == null || ORIGN_PRICE.equals("") ? 0
	// : Double.valueOf(ORIGN_PRICE));
	// food.set("UNIT", UNIT);
	// food.set("DESCRIPTION", DESCRIPTION);
	// USE_STOCK = USE_STOCK == null ? false : USE_STOCK;
	// food.set("USE_STOCK", USE_STOCK ? 1 : 0);
	// if (USE_STOCK != null && USE_STOCK)
	// food.set("STOCK", STOCK);
	// else
	// food.set("STOCK", 999999);
	// food.set("STATE", 1);
	// food.update();
	// renderJson(new JSONSuccess("添加成功"));
	// } catch (Exception e) {
	// e.printStackTrace();
	// renderJson(new JSONError("1"));
	//
	// }
	// }
	//
	// public void getHistroyData() {
	// DTParams params = new DTParams(getParaMap());
	// Map<String, Object> limitCondPara = new HashMap<>();
	// limitCondPara.put("name", "='" + getPara("GOOD_NAME") + "'");
	// limitCondPara.put("shop_id", "=" + getPara("SHOP_ID"));
	// Map<String, Map<String, Object>> allParap = new HashMap<>();
	// allParap.put("limitCond", limitCondPara);
	// Page<Record> foods = deliveryService.getDTPage(params, "food.histroy",
	// allParap);
	// JSONObject result = new JSONObject();
	// result.put("draw", params.getDraw());
	// result.put("recordsTotal", foods.getTotalRow());
	// List<Record> list = foods.getList();
	// result.put("data", list);
	// result.put("recordsFiltered", foods.getTotalRow());
	// renderJson(result);
	// }
	//
	public void checkUserName() {
		String userName = getPara("userName");
		JSONObject resultObject = new JSONObject();
		Record record = Db.findFirst("select * from t_delivery where USERNAME=?", userName);
		if (record == null) {
			resultObject.put("mess", "可以使用O(∩_∩)O~~");
		} else
			resultObject.put("mess", "已经被注册了哦~");
		renderJson(resultObject);
	}

	//
	// public void addFoodType() {
	// String shopName = getPara("shopName");
	// String type = getPara("type");
	// Shop shop = deliveryService.findByShopName(shopName);
	// if (shop == null) {
	// renderJson("没找到商家啊~");
	// return;
	// }
	// if (deliveryService.existFoodType(shop.get("ID"), type)) {
	// renderJson(new JSONError("已存在分类╮(╯▽╰)╭"));
	// return;
	// }
	// FoodType foodType = new FoodType();
	// foodType.set("NAME", type);
	// foodType.set("SHOP_ID", shop.get("ID"));
	// foodType.save();
	// renderJson(new JSONSuccess("添加成功(｡◕ˇ∀ˇ◕)"));
	// }
	//
	// public void checkFoodName() {
	// String shopName = getPara("foodName");
	// if (deliveryService.checkFoodName(shopName))
	// renderText("已存在该商品");
	// else
	// renderText("可以使用的商品名");
	// }
	//
	public void add() {
		render("add.html");
	}
	//
	// public void offShelf() {
	// try {
	// String foodId = getPara("foodId");
	// Food food = deliveryService.findById(foodId);
	// food.set("STATE", 0);
	// food.update();
	// renderText("success");
	// } catch (Exception e) {
	// log.info("error in offShelf");
	// e.printStackTrace();
	// renderText("error");
	// }
	// }
}