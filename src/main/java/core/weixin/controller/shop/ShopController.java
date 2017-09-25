/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.shop;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

import core.admin.service.foodType.FoodTypeService;
import core.admin.service.foodType.impl.FoodTypeServiceImpl;
import core.admin.service.shop.relation.ShopTypeRelationService;
import core.admin.service.shop.relation.impl.ShopTypeRelationServiceImpl;
import core.common.constants.ShopConstants;
import core.common.utils.QiniuUtils;
import core.interceptor.JSSDKInterceptor;
import core.model.Food;
import core.model.FoodType;
import core.model.Shop;
import core.model.ShopType;
import core.model.ShopTypeRelation;
import core.utils.LocationUtils;
import core.utils.MD5Util;
import core.validate.ShopWXValidate;
import core.vo.ConditionsVO;
import core.vo.JSONError;
import core.vo.JSONSuccess;
import core.weixin.api.MediaApi;
import core.weixin.base.BaseController;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月24日下午10:03:40
 */

public class ShopController extends BaseController {
	private Log log = Log.getLog(ShopController.class);
	private ShopTypeRelationService strService = new ShopTypeRelationServiceImpl();
	private String encrypt_key = PropKit.get("encrypt_key");
	private FoodTypeService foodTypeService = new FoodTypeServiceImpl();

	public void editFood() {
		String id = getCookie("shopId");
		Record shop = Db.findFirst(Db.getSql("shop.getById"), id);
		setAttr("shopId", id);
		setAttr("shop", shop);
		setAttr("types", FoodType.dao.find(Db.getSqlPara("foodType.getByShopId", Kv.by("id", id))));
		render("editFoodList.html");
	}

	public void saveFood() {
		String id = getCookie("shopId");
		Record shop = Db.findFirst(Db.getSql("shop.getById"), id);
		setAttr("shopId", id);
		setAttr("shop", shop);

		render("editFoodList.html");
	}

	public void getTransferType() {
		Integer shopid = getCookieToInt("shopId");
		int typeId = getParaToInt("typeId");
		JSONObject resultObject = new JSONObject();
		JSONArray typeArray = new JSONArray();
		if (foodTypeService.findShopById(shopid) != null) {
			resultObject.put("mess", "存在商家");
			Food food = Food.dao.findFirst(Db.getSql("foodType.exitsFood"), typeId);
			if (food != null) {
				List<FoodType> foodTypes = foodTypeService.findFoodTypeByShopId(shopid, typeId);
				for (FoodType foodType : foodTypes) {
					JSONObject typeObject = new JSONObject();
					typeObject.put("id", foodType.get("ID"));
					typeObject.put("name", foodType.get("NAME"));
					typeArray.add(typeObject);
				}
			}
		} else {
			resultObject.put("mess", "不存在商家");
		}
		resultObject.put("type", typeArray);
		renderJson(resultObject);
	}

	public void editSingleFood() {
		String foodId = getPara("foodId");
		if (foodId != null && !"-1".equals(foodId)) {
			Record record = Db.findFirst(
					"select a.*,b.name as SHOP_NAME from t_food a left join t_shop b on a.shop_id=b.id where a.id=?",
					foodId);
			setAttr("foodId", record.get("ID"));
			setAttr("shopName", record.get("SHOP_NAME"));
			setAttr("NAME", record.get("NAME"));
			setAttr("ORIGN_PRICE", record.get("ORIGN_PRICE"));
			setAttr("IMG", record.get("IMG") == null ? "" : record.get("IMG"));
			setAttr("PRICE", record.get("PRICE"));
			setAttr("nowType", record.get("TYPE_ID"));
			setAttr("UNIT", record.get("UNIT"));
			setAttr("DESCRIPTION", record.get("DESCRIPTION"));
			setAttr("PURCHASING_PRICE", record.get("PURCHASING_PRICE"));
			setAttr("USE_STOCK", record.get("USE_STOCK"));
			setAttr("STOCK", record.get("STOCK"));
			setAttr("SALE_LIMIT", record.get("SALE_LIMIT"));
		}
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
		render("foodPopup.html");
	}

	public void deleteType() {
		try {
			// 删除分类
			int typeId = getParaToInt("foodTypeId");
			FoodType foodType = FoodType.dao.findById(typeId);
			foodType.set("DELETED", 1);
			foodType.update();
			// 分类转移
			int dstTypeId = -1;
			try {
				dstTypeId = getParaToInt("dstTypeId");
			} catch (Exception e) {
			}
			if (dstTypeId != -1) {
				Db.update("update t_food set TYPE_ID=? WHERE TYPE_ID=?", dstTypeId, typeId);
			}
			renderJson(new JSONSuccess());
		} catch (Exception e) {
			log.info("error in delete");
			e.printStackTrace();
			renderJson(new JSONError(e.toString()));
		}
	}

	@Before(JSSDKInterceptor.class)
	public void editShop() {
		setAttr("shopId", getCookie("shopId"));
		Map<String, String> params = new HashMap<>();
		params.put("wxUsed", "1");
		setAttr("shopTypes", ShopType.dao.find(Db.getSqlPara("shopType.list", params)));
		Integer shopId = getCookieToInt("shopId");
		if (shopId == null) {
			render("login.html");
			return;
		}
		Shop shop = Shop.dao.findById(shopId);
		List<Record> type = Db.find(
				"SELECT b.ID TYPE_ID,b.NAME TYPE_NAME FROM T_SHOP_TYPE_RELATION a LEFT JOIN T_SHOP_TYPE b ON a.TYPEID=b.ID WHERE a.SHOPID=?",
				shopId);
		StringBuilder typeString = new StringBuilder("");
		StringBuilder typeIds = new StringBuilder("");
		if (type.size() != 0) {
			for (Record record : type) {
				typeString.append(record.getStr("TYPE_NAME"));
				typeString.append(",");
				typeIds.append(record.getInt("TYPE_ID") + "");
				typeIds.append(",");
			}
			typeString.deleteCharAt(typeString.length() - 1);
			typeIds.deleteCharAt(typeIds.length() - 1);
		}
		setAttr("SHOP_TYPE_STRING", typeString.toString());
		System.out.println("SHOP_TYPE_IDS" + typeIds.toString());
		setAttr("SHOP_TYPE_IDS", typeIds.toString());
		setAttr("shop", shop);
		render("edit.html");
	}

	@Override
	public void index() {
		super.index();
		String shopId = getCookie("shopId");
		setAttr("shopId", shopId);
		if (shopId == null) {
			render("login.html");
			return;
		}
		setAttr("shop", Shop.dao.findById(shopId));
		render("info.html");
	}

	public void login() {
		String username = getPara("username");
		String password = getPara("password");
		if (username == null || "".equals(username)) {
			renderJson(new JSONError("请输入用户名"));
			return;
		}
		Record record = Db.findFirst("SELECT * FROM T_SHOP WHERE USERNAME=?", username);
		if (record == null) {
			renderJson(new JSONError("没有这个人哦~"));
			return;
		}
		record = Db.findFirst("SELECT * FROM T_SHOP WHERE USERNAME=? AND PASSWORD=?", username,
				MD5Util.encrypt(password + encrypt_key));
		System.out.println(MD5Util.encrypt(password + encrypt_key));
		if (record == null) {
			renderJson(new JSONError("密码错了哦~"));
			return;
		}
		setCookie(new Cookie("shopId", record.getInt("ID") + ""));
		renderJson(new JSONSuccess());

	}

	public void setState() {
		try {
			Integer shopId = Integer.valueOf(getCookie("shopId"));
			Integer state = Integer.valueOf(getPara("state"));
			Shop shop = Shop.dao.findById(shopId);
			if (state == null) {
				renderJson(new JSONError("参数错误"));
				return;
			}
			shop.set("STATE", state);
			shop.update();
			renderJson(new JSONSuccess());
		} catch (Exception e) {
			renderJson(new JSONError(e.toString()));
		}

	}

	public void commitFoodType() {
		Integer shopId = Integer.valueOf(getCookie("shopId"));
		if (shopId == null) {
			renderJson(new JSONError("请先登录"));
			return;
		}
		Integer typeId = getParaToInt("typeId");
		String typeName = getPara("typeName");
		if (typeName == null || StringUtils.isBlank(typeName)) {
			renderJson(new JSONError("请输入类型名"));
			return;
		}
		Integer orderNum = getParaToInt("orderNum");
		if (orderNum == null)
			orderNum = 99;
		if (typeId == null)
			Db.update("insert into t_food_type(`name`,`order_num`,`SHOP_ID`) values(?,?,?)", typeName, orderNum,
					shopId);
		else
			Db.update("update t_food_type set `name`=?,order_num=? where id=?", typeName, orderNum, typeId);
		renderJson(new JSONSuccess());
	}

	public void getSaleNum() {
		try {
			Integer shopId = Integer.valueOf(getCookie("shopId"));
			Double sale = Db.queryDouble("SELECT SUM(PAY_PRICE) FROM T_ORDER WHERE SHOP_ID=? GROUP BY SHOP_ID", shopId);
			if (sale == null)
				sale = 0.0;
			String string = String.format("%.2f", sale);
			System.out.println(string);
			renderJson(new JSONSuccess(string));
		} catch (Exception e) {
			renderJson(new JSONError(e.toString()));
		}

	}

	/**
	 * 如果要支持多公众账号，只需要在此返回各个公众号对应的 ApiConfig 对象即可 可以通过在请求 url 中挂参数来动态从数据库中获取
	 * ApiConfig 属性值
	 */
	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();

		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));

		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
		return ac;
	}

	@Before(JSSDKInterceptor.class)
	public void add() {
		Map<String, String> params = new HashMap<>();
		params.put("wxUsed", "1");
		setAttr("shopTypes", ShopType.dao.find(Db.getSqlPara("shopType.list", params)));
		render("add.html");
	}

	@Before(ShopWXValidate.class)
	public void save() {
		String id = getPara("id");

		Shop shop = Shop.dao.findById(id);
		if (shop == null) {
			shop = new Shop();
		}
		shop.set("NAME", getPara("name"));
		shop.set("ADMIN_NAME", getPara("adminName"));
		shop.set("DESCRIPTION", getPara("description"));
		shop.set("ADDRESS", getPara("address"));
		shop.set("LATITUDE", getPara("latitude"));
		shop.set("LONGITUDE", getPara("longitude"));
		String addressCode = getPara("addressCode");
		shop.set("PCODE", addressCode.substring(0, 2) + "0000");
		shop.set("PCCODE", addressCode.substring(0, 4) + "00");
		shop.set("PTCODE", addressCode);
		String password = getPara("password");
		shop.set("PASSWORD", MD5Util.encrypt(password, PropKit.get("encrypt_key")));
		shop.set("TEL", getPara("phone"));
		shop.set("QQ", getPara("qq"));
		shop.set("OPEN_TIME", getPara("openTime"));
		shop.set("CLOSE_TIME", getPara("closeTime"));
		shop.set("DELIVERY_THRESHOLD", getPara("deliveryThreshold"));
		shop.set("DELIVERY_PRICE", getPara("deliveryPrice"));
		int on = 0;
		String onStr = getPara("autoOpen");
		if ("on".equals(onStr)) {
			on = 1;
		}
		if (getPara("logo") != null) {
			// 上传素材
			ApiConfigKit.setThreadLocalApiConfig(getApiConfig());
			String logoUrl = QiniuUtils.upload(MediaApi.mediaGet(getPara("logo")));
			shop.set("IMG", logoUrl + "?imageView2/1/w/200/h/200");
		} else {
			if (shop.get("IMG") == null) {
				renderJson(new JSONError("请上传图片"));
				return;
			}
		}
		shop.set("AUTO_OPEN", on);
		if (StringUtils.isNotEmpty(id)) {
			shop.update();
			strService.deleteAll(id);
		} else {
			shop.set("USERNAME", getPara("username"));
			shop.set("AUDIT_STATE", 0);
			shop.set("STATE", -1);
			shop.save();
		}
		String[] temp = getPara("shopTypeId").split(",");
		Integer[] shopTypeId = new Integer[temp.length];
		for (int i = 0; i < temp.length; i++) {
			shopTypeId[i] = Integer.valueOf(temp[i]);
		}
		if (shopTypeId != null) {
			for (Integer typeId : shopTypeId) {
				ShopTypeRelation shopTypeRelation = new ShopTypeRelation();
				shopTypeRelation.set("TYPEID", typeId);
				shopTypeRelation.set("SHOPID", shop.getInt("ID"));
				shopTypeRelation.save();
			}
		}
		renderJson(new JSONSuccess());
	}

	public void getFoodType() {
		String id = getCookie("shopId");
		List<Record> data = Db.find("select ID,NAME FROM T_FOOD_TYPE WHERE DELETED=0 AND SHOP_ID=?", id);
		JSONArray resultArray = new JSONArray();
		for (Record record : data) {
			JSONObject object = new JSONObject();
			object.put("ID", record.get("ID"));
			object.put("NAME", record.get("NAME"));
			resultArray.add(object);
		}
		System.out.println(resultArray);
		renderJson(resultArray);

	}

	public void ajaxShopTypes() {
		Map<String, Object> map = new HashMap<>();
		if (getPara("type") == null) {
			map.put("only", 7);
		}
		List<ShopType> shopTypes = ShopType.dao.find(Db.getSqlPara("shopType_wx.list", map));
		renderJson(new JSONSuccess(shopTypes));
	}

	public void ajaxShops() {
		JSONObject params = JSONObject.parseObject(HttpKit.readData(getRequest()));
		ConditionsVO conditionsVO = new ConditionsVO();
		SqlPara sqlPara = null;

		String flag = params.getString("flag");
		JSONObject loc = null;
		Map<String, Object> filter = new HashMap<>();
		filter.put("wxUsed", "1");
		if (StringUtils.isNotEmpty(flag)) {
			switch (flag) {
			case "hotShop": {
				Map<String, Object> order = new LinkedHashMap<>();
				order.put("SALE_NUM", "DESC");
				order.put("RATE_AVG", "DESC");
				conditionsVO.getOrderbyCond().putAll(order);
				Map<String, Object> limit = new LinkedHashMap<>();
				limit.put("STATE", "!=-2");
				conditionsVO.getLimitCond().putAll(limit);
				filter.putAll(conditionsVO.getConditions());
				sqlPara = Db.getSqlPara("shop.list", filter);
				break;
			}
			case "neighbor": {
				try {
					loc = params.getJSONObject("loc");
				} catch (Exception e) {
					renderJson(new JSONError(ShopConstants.getValue(flag) + "加载失败！"));
					return;
				}
				filter.putAll(loc);
				sqlPara = Db.getSqlPara("shop.groupByDistant", filter);
				break;
			}
			case "collections": {
				String openId = params.getString("openId");
				Record collections = Db.findFirst(Db.getSql("user.getCollections"), openId);
				if (collections != null) {
					String collectionsStr = collections.getStr("COLLECTION");
					if (StringUtils.isNotEmpty(collectionsStr)) {
						String sqlParams = "'" + collectionsStr.replace(",", "','") + "'";
						sqlPara = Db.getSqlPara("shop.inId", Kv.by("inParams", sqlParams));
					}
				}
				break;
			}
			default:
				break;
			}
		}
		if (sqlPara != null) {
			Integer pageNumber = params.getInteger("pageNumber");
			Integer pageSize = params.getInteger("pageSize");
			if (pageNumber != null) {
				if (pageSize == null) {
					pageSize = 10;
				}
				Page<Record> shops = Db.paginate(pageNumber, pageSize, sqlPara);
				if ("neighbor".equals(flag)) {
					DecimalFormat df = new java.text.DecimalFormat("0.00");
					for (Record record : shops.getList()) {
						Double distance = LocationUtils.getDistance(loc.getDouble("lng"), loc.getDouble("lat"),
								record.getDouble("LONGITUDE"), record.getDouble("LATITUDE"));
						record.set("DISTANCE", df.format(distance));
					}
				}
				renderJson(new JSONSuccess(shops));
			} else {
				List<Record> shops = Db.find(sqlPara);
				renderJson(new JSONSuccess(shops));
			}
		} else {
			renderJson(new JSONError("暂时没有找到" + ShopConstants.getValue(flag, "") + "哦"));
		}
	}

	public void ajaxSearchShops() {
		String keyword = getPara("keyword");
		if (StringUtils.isNotEmpty(keyword)) {
			String type = getPara("type");
			List<Record> records = null;
			Map<String, String> map = new HashMap<>();
			map.put("keyword", keyword);
			if ("food".equals(type)) {
				records = Db.find(Db.getSqlPara("food.search", map));
			} else if ("shop".equals(type)) {
				records = Db.find(Db.getSqlPara("shop.search", map));
			}
			renderJson(new JSONSuccess(records));
		} else {
			renderJson(new JSONError("请输入关键词"));
		}
	}

	public void typeDetail() {
		String id = getPara("id");
		if (StringUtils.isNotEmpty(id)) {
			setAttr("typeName", ShopType.dao.findById(id).getStr("NAME"));
			List<Shop> shops = Shop.dao.find(Db.getSql("shop.getByType"), id);
			setAttr("shops", shops);
			render("typeDetail.html");
		} else {
			renderJson(new JSONError("该店铺分类不存在！"));
		}
	}

	public void typeMore() {
		render("typeMore.html");
	}

	public void front() {
		String id = getPara("id");
		Record shop = Db.findFirst(Db.getSql("shop.getById"), id);
		setAttr("shop", shop);
		setAttr("types", FoodType.dao.find(Db.getSqlPara("foodType.getByShopId", Kv.by("id", id))));
		render("front.html");
	}

	public void mapMobile() {
		setAttr("key", PropKit.get("amap.key"));
		setAttr("lng", getPara("lng"));
		setAttr("lat", getPara("lat"));
		render("map-mobile.html");
	}

}
