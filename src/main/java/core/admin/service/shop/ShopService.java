/**
 * Created At 2017年7月8日下午11:28:18 
 * 
 */

package core.admin.service.shop;

import core.admin.service.base.DataTableService;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月8日下午11:28:18
 */

public interface ShopService extends DataTableService {
	boolean hasOrder(String shopId);

	boolean registerd(String username);

	boolean hasFood(String shopId);
}
