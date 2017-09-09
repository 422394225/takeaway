package core.admin.controller.food;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import core.admin.service.foodType.FoodTypeService;
import core.admin.service.foodType.impl.FoodTypeServiceImpl;
import core.model.Food;
import core.model.FoodType;
import core.model.Shop;
import core.vo.DTParams;
import core.vo.JSONError;
import core.vo.JSONSuccess;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:40:27
 */
public class FoodTypeController extends Controller {

	private Log log = Log.getLog(FoodTypeController.class);
	private FoodTypeService foodTypeService = new FoodTypeServiceImpl();

	public void index() {
		render("list.html");
	}

	public void getData() {
		DTParams params = new DTParams(getParaMap());
		Page<Record> foods = foodTypeService.getDTPage(params, "foodType.list");
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", foods.getTotalRow());
		List<Record> list = foods.getList();
		result.put("data", list);
		result.put("recordsFiltered", foods.getTotalRow());
		renderJson(result);
	}

	public void edit() {
		String id = getPara("id");
		FoodType foodType = FoodType.dao.findById(id);
		setAttr("foodType", foodType);
		render("add.html");
	}

	public void save() {
		try {
			FoodType foodType = new FoodType();
			foodType.set("SHOP_ID", getPara("shopId"));
			foodType.set("NAME", getPara("name"));
			Integer orderNum = getParaToInt("orderNum");
			foodType.set("ORDER_NUM", orderNum);
			String id = getPara("id");
			if (StringUtils.isNotEmpty(id)) {
				foodType.set("ID", id);
				foodType.update();
			} else {
				foodType.save();
			}
			renderJson(new JSONSuccess("保存成功(｡◕ˇ∀ˇ◕)"));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONError("添加失败╮(╯▽╰)╭"));

		}
	}

	public void delete() {
		try {
			//删除分类
			int typeId = getParaToInt("foodTypeId");
			FoodType foodType = FoodType.dao.findById(typeId);
			foodType.set("DELETED", 1);
			foodType.update();
			//分类转移
			int dstTypeId = -1;
			try {
				dstTypeId = getParaToInt("dstTypeId");
			}catch (Exception e){}
			if(dstTypeId!=-1){
				Db.update("update t_food set TYPE_ID=? WHERE TYPE_ID=?", dstTypeId, typeId);
			}
			renderText("success");
		} catch (Exception e) {
			log.info("error in delete");
			e.printStackTrace();
			renderText("error");
		}
	}

	public void add() {
		render("add.html");
	}

	public void ajaxShopList() {
		List<Shop> shops = Shop.dao.find(Db.getSql("foodType.shopList"));
		renderJson(new JSONSuccess(shops));
	}

	public void getTransferType() {
		String shopName = getPara("shopName");
		int typeId = getParaToInt("typeId");
		JSONObject resultObject = new JSONObject();
		JSONArray typeArray = new JSONArray();
		if (foodTypeService.findByShopName(shopName) != null) {
			resultObject.put("mess", "存在商家");
			Food food = Food.dao.findFirst(Db.getSql("foodType.exitsFood"),typeId);
			if(food!=null){
				List<FoodType> foodTypes = foodTypeService.findFoodTypeByShopName(shopName, typeId);
				for (FoodType foodType : foodTypes) {
					JSONObject typeObject = new JSONObject();
					typeObject.put("id", foodType.get("ID"));
					typeObject.put("name", foodType.get("NAME"));
					typeArray.add(typeObject);
				}
			}
		} else{
			resultObject.put("mess", "不存在商家");
		}
		resultObject.put("type", typeArray);
		renderJson(resultObject);
	}

}