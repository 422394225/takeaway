/**
 * Created At 2017年7月8日下午11:27:53 
 * 
 */

package core.admin.service.shop.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import core.admin.service.base.impl.DataTableServiceImpl;
import core.admin.service.shop.ShopService;
import core.model.Admin;
import core.model.Audit;
import core.model.Food;
import core.model.Order;
import core.model.Shop;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月8日下午11:27:53
 */

public class ShopServiceImpl extends DataTableServiceImpl implements ShopService {
	public boolean hasOrder(String shopId) {
		String sql = "SELECT ID FROM t_order WHERE SHOP_ID =?";
		Order order = Order.dao.findFirst(sql, shopId);
		if (order == null) {
			return false;
		}
		return true;
	}

	public boolean hasFood(String shopId) {
		String sql = "SELECT ID FROM t_food WHERE SHOP_ID =?";
		Food order = Food.dao.findFirst(sql, shopId);
		if (order == null) {
			return false;
		}
		return true;
	}

	public boolean registerd(String username) {
		String sql = "SELECT ID FROM t_shop WHERE USERNAME =?";
		Shop shop = Shop.dao.findFirst(sql, username);
		if (shop == null) {
			return false;
		}
		return true;
	}

	public void audit(Controller controller, Shop shop, int state) {
		Audit audit = new Audit();
		audit.set("SHOP_ID", shop.get("ID"));
		audit.set("REASON", controller.getPara("reason"));
		Admin admin = controller.getSessionAttr("administrator");
		audit.set("OPERATOR_ID", admin.getStr("ID"));
		audit.set("OPERATOR_NAME", admin.getStr("NAME"));
		audit.set("STATE", state);
		audit.save();
		shop.set("AUDIT_STATE", state);
		shop.update();
	}

	@Override
	public Map<Integer, Double> getFoodPurchasingPrice(int shopId) {
		List<Food> foods = Food.dao.find("SELECT ID,PURCHASING_PRICE FROM T_FOOD WHERE SHOP_ID=?", shopId);
		Map<Integer, Double> resultMap = new HashMap<>();
		for (Food food : foods) {
			resultMap.put(food.getInt("ID"), food.getDouble("PURCHASING_PRICE"));
		}
		foods = Food.dao.find("SELECT ID,PURCHASING_PRICE FROM t_food_histroy WHERE SHOP_ID=?", shopId);
		for (Food food : foods) {
			resultMap.put(food.getInt("ID"), food.getDouble("PURCHASING_PRICE"));
		}
		return resultMap;
	}

	@Override
	public ArrayList<JSONArray> getOrderFoodsMapByShop(int shopId, String limitType) {
		if (!SELECT_PURCHASING_PRICE_SUFFIX.containsKey(limitType)) {
			return null;
		}
		String sql = SELECT_PURCHASING_PRICE_PREFIX + SELECT_PURCHASING_PRICE_SUFFIX.get(limitType);
		List<Record> records = Db.find(sql, shopId);
		ArrayList<JSONArray> resultList = new ArrayList<>();
		for (Record record : records) {
			resultList.add(JSONArray.parseArray(record.getStr("FOODS")));
		}
		return resultList;
	}
}
