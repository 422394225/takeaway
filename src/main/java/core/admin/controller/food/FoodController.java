package core.admin.controller.food;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.h2.util.New;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import core.admin.service.food.FoodService;
import core.admin.service.food.impl.FoodServiceImpl;
import core.model.Food;
import core.vo.DTParams;

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
		renderText("123");
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
		if (foodService.checkShopName(shopName))
			renderText("验证通过");
		else
			renderText("不存在商家");
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