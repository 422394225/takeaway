
package core.admin.service.foodType;

import java.util.List;

import core.admin.service.base.DataTableService;
import core.model.Food;
import core.model.FoodType;
import core.model.Shop;

public interface FoodTypeService extends DataTableService {
	public Food findById(String id);

	public FoodType findFoodType(Integer shopId, String type);

	public Shop findByShopName(String shopName);

	public boolean checkFoodName(String shopName);

	public Food findByName(Integer shopId, String name);

	public List<FoodType> findFoodTypeByShopName(String shopName);

	public List<FoodType> findFoodTypeByShopName(String shopName, int expId);

	public List<FoodType> findFoodTypeByShopId(int shopId, int expId);

	public boolean existFoodType(int shopId, String type);

	public Shop findShopById(int id);
}
