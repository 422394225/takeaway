package core.admin.controller.rate;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import core.admin.service.foodType.FoodTypeService;
import core.admin.service.foodType.impl.FoodTypeServiceImpl;
import core.vo.DTParams;

import java.util.List;

/**
 * 
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:40:27
 */
public class RateController extends Controller {

	private Log log = Log.getLog(RateController.class);
	private FoodTypeService foodTypeService = new FoodTypeServiceImpl();

	public void index() {
		render("list.html");
	}

	public void getData() {
		DTParams params = new DTParams(getParaMap());
		Page<Record> foods = foodTypeService.getDTPage(params, "rate.list");
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", foods.getTotalRow());
		List<Record> list = foods.getList();
		result.put("data", list);
		result.put("recordsFiltered", foods.getTotalRow());
		renderJson(result);
	}

	/*public void edit() {
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

	public void save() {
		try {
			FoodType foodType;
			String shopName = getPara("shopName");
			Shop shop = foodTypeService.findByShopName(shopName);
			if (shop == null) {
				renderJson(new JSONError("没找到商家啊~"));
				return;
			}
			String name = getPara("NAME");
			foodType = foodTypeService.findFoodType(shop.get("ID"), name);
			if (foodType != null) {
				if (foodType.getInt("DELETED") == 0)
					renderJson(new JSONError("已存在分类╮(╯▽╰)╭"));
				else {
					foodType.set("DELETED", "0");
					foodType.update();
					renderJson(new JSONSuccess("添加成功(｡◕ˇ∀ˇ◕)"));
				}
				return;
			}
			foodType = new FoodType();
			foodType.set("SHOP_ID", shop.get("ID"));
			foodType.set("NAME", name);
			foodType.save();
			renderJson(new JSONSuccess("添加成功(｡◕ˇ∀ˇ◕)"));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONError("添加失败╮(╯▽╰)╭"));

		}
	}

	public void checkShopName() {
		String shopName = getPara("shopName");
		JSONObject resultObject = new JSONObject();
		JSONArray typeArray = new JSONArray();
		if (foodTypeService.findByShopName(shopName) != null) {
			resultObject.put("mess", "存在商家");
		} else
			resultObject.put("mess", "不存在商家");
		resultObject.put("type", typeArray);
		renderJson(resultObject);
	}

	public void add() {
		render("add.html");
	}

	public void getTransferType() {
		String shopName = getPara("shopName");
		int typeId = getParaToInt("typeId");
		JSONObject resultObject = new JSONObject();
		JSONArray typeArray = new JSONArray();
		if (foodTypeService.findByShopName(shopName) != null) {
			resultObject.put("mess", "存在商家");
			List<FoodType> foodTypes = foodTypeService.findFoodTypeByShopName(shopName, typeId);
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

	public void delete() {
		try {
			int typeId = getParaToInt("foodTypeId");
			int dstTypeId = getParaToInt("dstTypeId");
			FoodType foodType = FoodType.dao.findById(typeId);
			foodType.set("DELETED", 1);
			Db.update("update t_food set TYPE_ID=? WHERE TYPE_ID=?", dstTypeId, typeId);
			foodType.update();
			renderText("success");
		} catch (Exception e) {
			log.info("error in delete");
			e.printStackTrace();
			renderText("error");
		}
	}

	public void rename() {
		try {
			int typeId = getParaToInt("foodTypeId");
			String newName = getPara("name");
			Db.update("update t_food_type set name=? where id=?", newName, typeId);
			renderText("success");
		} catch (Exception e) {
			log.info("error in rename");
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
	}*/
}