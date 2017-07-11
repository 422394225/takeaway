
package core.admin.service.food.impl;

import core.admin.service.base.impl.DataTableServiceImpl;
import core.admin.service.food.FoodService;
import core.model.Food;
import core.model.Shop;

public class FoodServiceImpl extends DataTableServiceImpl implements FoodService {

	@Override
	public Food findById(String id) {
		String sql = "SELECT * FROM T_FOOD WHERE ID = ?";
		Food food = Food.dao.findFirst(sql, id);
		return food;
	}

	@Override
	public boolean checkShopName(String shopName) {
		Shop shop = Shop.dao.findFirst("select * from t_shop where name=?", shopName);
		return shop != null;
	}

	@Override
	public boolean checkFoodName(String shopName) {
		Food food = Food.dao.findFirst("select * from t_food where name=?", shopName);
		return food != null;
	}

}
