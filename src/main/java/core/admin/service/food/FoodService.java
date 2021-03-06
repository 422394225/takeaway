
package core.admin.service.food;

import java.util.List;

import core.admin.service.base.DataTableService;
import core.model.Food;
import core.model.FoodType;
import core.model.Shop;

public interface FoodService extends DataTableService {
	public Food findById(String id);

	public FoodType findFoodType(String shopId, String type);

	public Shop findByShopName(String shopName);

	public boolean checkFoodName(String shopName);

	public Food findByName(Integer shopId, String name);

	public List<FoodType> findFoodTypeByShopName(String shopName);

	public boolean existFoodType(int shopId, String type);

	public Shop findShopById(int id);

}
