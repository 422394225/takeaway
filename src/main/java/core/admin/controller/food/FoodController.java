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

	public void histroy() {
		setAttr("GOOD_NAME", getPara("GOOD_NAME"));
		setAttr("SHOP_ID", getPara("SHOP_ID"));
		render("histroy.html");
	}

	public void save() {
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
			Food food1 = foodService.findByName(name);
			if (food1 != null) {
				renderJson(new JSONError("商品已存在"));
				return;
			}
			String price = getPara("price");
			String UNIT = getPara("UNIT");
			String DESCRIPTION = getPara("DESCRIPTION");
			Boolean USE_STOCK = getParaToBoolean("USE_STOCK");
			String STOCK = getPara("STOCK");
			food.set("SHOP_ID", shop.get("ID"));
			food.set("NAME", name);
			food.set("PRICE", price);
			food.set("UNIT", UNIT);
			food.set("DESCRIPTION", DESCRIPTION);
			food.set("USE_STOCK", USE_STOCK);
			if (USE_STOCK != null && USE_STOCK)
				food.set("STOCK", STOCK);
			else
				food.set("STOCK", 999999);
			food.set("STATE", 1);
			food.save();
			renderJson(new JSONSuccess("添加成功"));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONError("1"));

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
}