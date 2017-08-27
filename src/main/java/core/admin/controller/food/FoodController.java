package core.admin.controller.food;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

import core.admin.service.food.FoodService;
import core.admin.service.food.impl.FoodServiceImpl;
import core.common.utils.QiniuUtils;
import core.model.Food;
import core.model.FoodType;
import core.model.Shop;
import core.vo.DTParams;
import core.vo.JSONError;
import core.vo.JSONSuccess;

/**
 * 
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:40:27
 */
public class FoodController extends Controller {

	private Log log = Log.getLog(FoodController.class);
	private FoodService foodService = new FoodServiceImpl();

	public void index() {
		render("list.html");
	}

	public void getData() {
		DTParams params = new DTParams(getParaMap());
		Page<Record> foods = foodService.getDTPage(params, "food.list");
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", foods.getTotalRow());
		List<Record> list = foods.getList();
		result.put("data", list);
		result.put("recordsFiltered", foods.getTotalRow());
		renderJson(result);
	}

	public void edit() {
		String foodId = getPara("foodId");
		Record record = Db.findFirst(
				"select a.*,b.name as SHOP_NAME from t_food a left join t_shop b on a.shop_id=b.id where a.id=?",
				foodId);
		setAttr("foodId", record.get("ID"));
		setAttr("shopName", record.get("SHOP_NAME"));
		setAttr("NAME", record.get("NAME"));
		setAttr("ORIGN_PRICE", record.get("ORIGN_PRICE"));
		setAttr("IMG", record.get("IMG") == null ? "" : record.get("IMG"));
		setAttr("price", record.get("PRICE"));
		setAttr("nowType", record.get("TYPE_ID"));
		setAttr("UNIT", record.get("UNIT"));
		setAttr("DESCRIPTION", record.get("DESCRIPTION"));
		setAttr("USE_STOCK", record.get("USE_STOCK"));
		setAttr("STOCK", record.get("STOCK"));
		setAttr("SALE_LIMIT", record.get("SALE_LIMIT"));
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

	public void histroy() {
		setAttr("GOOD_NAME", getPara("GOOD_NAME"));
		setAttr("SHOP_ID", getPara("SHOP_ID"));
		render("histroy.html");
	}

	public void save() {
		getFile();
		try {
			Food food = new Food();
			try {
				UploadFile file = getFile("IMG");
				if (file != null) {
					String localFilePath = file.getUploadPath() + File.separator + file.getFileName();
					String path = QiniuUtils.upload(localFilePath);
					String crop = getPara("crop");// 截图参数
					food.set("IMG", path + (StringUtils.isNotEmpty(crop) ? crop : ""));
				}
			} catch (Exception e) {
				renderJson(new JSONError("图片错误"));
				return;
			}
			String shopName = getPara("shopName");
			Shop shop = foodService.findByShopName(shopName);
			if (shop == null) {
				renderJson(new JSONError("店铺不存在"));
				return;
			}
			String name = getPara("NAME");
			Food food1 = foodService.findByName(shop.getInt("ID"), name);
			if (food1 != null) {
				renderJson(new JSONError("商品已存在"));
				return;
			}
			String price = getPara("price");
			String UNIT = getPara("UNIT");
			String DESCRIPTION = getPara("DESCRIPTION");
			if (DESCRIPTION.length() > 50) {
				renderJson(new JSONError("描述不能超过50字哦~"));
				return;
			}
			Boolean USE_STOCK = getParaToBoolean("USE_STOCK");
			String STOCK = getPara("STOCK");
			String TYPE = getPara("TYPE");
			String ORIGN_PRICE = getPara("ORIGN_PRICE");
			String SALE_LIMIT = getPara("SALE_LIMIT");
			food.set("SHOP_ID", shop.get("ID"));
			food.set("NAME", name);
			food.set("PRICE", price);
			food.set("TYPE_ID", TYPE);
			food.set("ORIGN_PRICE", ORIGN_PRICE == null || ORIGN_PRICE.equals("") ? 0 : Double.valueOf(ORIGN_PRICE));
			food.set("UNIT", UNIT);
			food.set("SALE_LIMIT", SALE_LIMIT == null ? 0 : Integer.valueOf(SALE_LIMIT));
			food.set("DESCRIPTION", DESCRIPTION);
			USE_STOCK = USE_STOCK == null ? false : USE_STOCK;
			food.set("USE_STOCK", USE_STOCK ? 1 : 0);
			if (USE_STOCK != null && USE_STOCK)
				food.set("STOCK", STOCK);
			else
				food.set("STOCK", 999999);
			food.set("STATE", 1);
			food.save();
			renderJson(new JSONSuccess("添加成功"));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONError("添加失败"));

		}
	}

	public void saveEdit() {
		getFile();
		try {
			String foodId = getPara("foodId");
			Food food = Food.dao.findById(foodId);
			String imgPath = null;
			try {
				UploadFile file = getFile("IMG");
				if (file != null) {
					String localFilePath = file.getUploadPath() + File.separator + file.getFileName();
					String path = QiniuUtils.upload(localFilePath);
					String crop = getPara("crop");// 截图参数
					imgPath = path + (StringUtils.isNotEmpty(crop) ? crop : "");
				} else
					imgPath = food.getStr("IMG");
			} catch (Exception e) {
				renderJson(new JSONError("图片错误"));
				return;
			}
			String shopName = getPara("shopName");
			Shop shop = foodService.findByShopName(shopName);
			if (shop == null) {
				renderJson(new JSONError("店铺不存在"));
				return;
			}
			String name = getPara("NAME");
			String price = getPara("price");
			String UNIT = getPara("UNIT");
			String DESCRIPTION = getPara("DESCRIPTION");
			if (DESCRIPTION.length() > 50) {
				renderJson(new JSONError("描述不能超过50字哦~"));
				return;
			}
			Boolean USE_STOCK = getParaToBoolean("USE_STOCK");
			String STOCK = getPara("STOCK");
			String TYPE = getPara("TYPE");
			String SALE_LIMIT = getPara("SALE_LIMIT");
			String ORIGN_PRICE = getPara("ORIGN_PRICE");
			food.delete();
			food.set("IMG", imgPath);
			food.set("ID", null);
			food.set("SHOP_ID", shop.get("ID"));
			food.set("NAME", name);
			food.set("PRICE", price);
			food.set("TYPE_ID", TYPE);
			food.set("SALE_LIMIT", SALE_LIMIT == null ? 0 : Integer.valueOf(SALE_LIMIT));
			food.set("ORIGN_PRICE", ORIGN_PRICE == null || ORIGN_PRICE.equals("") ? 0 : Double.valueOf(ORIGN_PRICE));
			food.set("UNIT", UNIT);
			food.set("DESCRIPTION", DESCRIPTION);
			food.set("CREATE_TIME", null);
			USE_STOCK = USE_STOCK == null ? false : USE_STOCK;
			food.set("USE_STOCK", USE_STOCK ? 1 : 0);
			if (USE_STOCK != null && USE_STOCK)
				food.set("STOCK", STOCK);
			else
				food.set("STOCK", 999999);
			food.set("STATE", 1);
			food.save();
			renderJson(new JSONSuccess("修改成功"));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONError("修改失败"));

		}
	}

	public void getHistroyData() {
		DTParams params = new DTParams(getParaMap());
		Map<String, Object> limitCondPara = new HashMap<>();
		limitCondPara.put("name", "='" + getPara("GOOD_NAME") + "'");
		limitCondPara.put("shop_id", "=" + getPara("SHOP_ID"));
		Map<String, Map<String, Object>> allParap = new HashMap<>();
		allParap.put("limitCond", limitCondPara);
		Page<Record> foods = foodService.getDTPage(params, "food.histroy", allParap);
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", foods.getTotalRow());
		List<Record> list = foods.getList();
		result.put("data", list);
		result.put("recordsFiltered", foods.getTotalRow());
		renderJson(result);
	}

	public void checkShopName() {
		String shopName = getPara("shopName");
		JSONObject resultObject = new JSONObject();
		JSONArray typeArray = new JSONArray();
		if (foodService.findByShopName(shopName) != null) {
			resultObject.put("mess", "存在商家");
			List<FoodType> foodTypes = foodService.findFoodTypeByShopName(shopName);
			for (FoodType foodType : foodTypes) {
				JSONObject typeObject = new JSONObject();
				typeObject.put("id", foodType.get("ID"));
				typeObject.put("name", foodType.get("NAME"));
				typeArray.add(typeObject);
			}
		} else
			resultObject.put("mess", "不存在商家");
		resultObject.put("type", typeArray);
		renderJson(resultObject);
	}

	public void addFoodType() {
		String shopName = getPara("shopName");
		String type = getPara("type");
		Shop shop = foodService.findByShopName(shopName);
		if (shop == null) {
			renderJson("没找到商家啊~");
			return;
		}
		if (foodService.existFoodType(shop.get("ID"), type)) {
			renderJson(new JSONError("已存在分类╮(╯▽╰)╭"));
			return;
		}
		FoodType foodType = new FoodType();
		foodType.set("NAME", type);
		foodType.set("SHOP_ID", shop.get("ID"));
		foodType.save();
		renderJson(new JSONSuccess("添加成功(｡◕ˇ∀ˇ◕)"));
	}

	public void checkFoodName() {
		String shopName = getPara("foodName");
		if (foodService.checkFoodName(shopName))
			renderText("已存在该商品");
		else
			renderText("可以使用的商品名");
	}

	public void add() {
		render("add.html");
	}

	public void offShelf() {
		try {
			String foodId = getPara("foodId");
			Food food = foodService.findById(foodId);
			food.set("STATE", 0);
			food.update();
			renderText("success");
		} catch (Exception e) {
			log.info("error in offShelf");
			e.printStackTrace();
			renderText("error");
		}
	}

	public void onShelf() {
		try {
			String foodId = getPara("foodId");
			Food food = foodService.findById(foodId);
			food.set("STATE", 1);
			food.update();
			renderText("success");
		} catch (Exception e) {
			log.info("error in offShelf");
			e.printStackTrace();
			renderText("error");
		}
	}

	public void getShopName() {
		String keyWord = getPara("keyWord");
		List<Record> records = Db.find("select `NAME` from t_shop where NAME like '%" + keyWord + "%'");
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
}