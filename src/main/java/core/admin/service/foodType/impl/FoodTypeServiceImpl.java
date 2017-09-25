
package core.admin.service.foodType.impl;

import java.util.List;

import core.admin.service.base.impl.DataTableServiceImpl;
import core.admin.service.foodType.FoodTypeService;
import core.model.Food;
import core.model.FoodType;
import core.model.Shop;

public class FoodTypeServiceImpl extends DataTableServiceImpl implements FoodTypeService {

	@Override
	public Food findById(String id) {
		String sql = "SELECT * FROM T_FOOD WHERE ID = ?";
		Food food = Food.dao.findFirst(sql, id);
		return food;
	}

	@Override
	public Shop findByShopName(String shopName) {
		Shop shop = Shop.dao.findFirst("select * from t_shop where name=?", shopName);
		return shop;
	}

	@Override
	public Shop findShopById(int id) {
		Shop shop = Shop.dao.findFirst("select * from t_shop where ID=?", id);
		return shop;
	}

	@Override
	public boolean checkFoodName(String shopName) {
		Food food = Food.dao.findFirst("select * from t_food where name=?", shopName);
		return food != null;
	}

	@Override
	public Food findByName(Integer shopId, String name) {
		Food food = Food.dao.findFirst("select * from t_food where name=? and SHOP_ID=?", name, shopId);
		return food;
	}

	@Override
	public List<FoodType> findFoodTypeByShopName(String shopName) {
		List<FoodType> foodType = FoodType.dao.find(
				"select a.* from t_food_type a left join t_shop b on a.shop_id = b.id where b.name=? and a.deleted!=1",
				shopName);
		return foodType;
	}

	@Override
	public boolean existFoodType(int shopId, String type) {
		FoodType foodType = FoodType.dao.findFirst("select * from t_food_type where name=? and shop_id=?", type,
				shopId);
		return foodType != null;
	}

	@Override
	public FoodType findFoodType(Integer shopId, String type) {
		FoodType foodType = FoodType.dao.findFirst("select * from t_food_type where name=? and shop_id=?", type,
				shopId);
		return foodType;
	}

	@Override
	public List<FoodType> findFoodTypeByShopName(String shopName, int expId) {
		List<FoodType> foodType = FoodType.dao.find(
				"select a.* from t_food_type a left join t_shop b on a.shop_id = b.id where b.name=? and a.id!=? and a.deleted!=1",
				shopName, expId);
		return foodType;
	}

	@Override
	public List<FoodType> findFoodTypeByShopId(int shopId, int expId) {
		List<FoodType> foodType = FoodType.dao
				.find("select * from t_food_type where shop_id=? and id!=? and deleted!=1", shopId, expId);
		return foodType;
	}

}
