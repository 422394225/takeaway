
package core.admin.service.food;

import core.admin.service.base.DataTableService;
import core.model.Food;

public interface FoodService extends DataTableService {
	public Food findById(String id);

	public boolean checkShopName(String shopName);

	public boolean checkFoodName(String shopName);
}
