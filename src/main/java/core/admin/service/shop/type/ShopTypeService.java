/**
 * Created At 2017年7月8日下午11:28:18 
 * 
 */

package core.admin.service.shop.type;

import core.admin.service.base.DataTableService;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月8日下午11:28:18
 */

public interface ShopTypeService extends DataTableService {
	boolean hasShop(String typeId);
}
