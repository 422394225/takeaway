/**
 * Created At 2017年7月8日下午11:27:53 
 * 
 */

package core.admin.service.shop.type.impl;

import core.admin.service.base.impl.DataTableServiceImpl;
import core.admin.service.shop.type.ShopTypeService;
import core.model.ShopTypeRelation;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月8日下午11:27:53
 */

public class ShopTypeServiceImpl extends DataTableServiceImpl implements ShopTypeService {
	public boolean hasShop(String typeId) {
		String sql = "SELECT ID FROM t_shop_type_relation WHERE TYPEID =?";
		ShopTypeRelation shopTypeRelation = ShopTypeRelation.dao.findFirst(sql, typeId);
		if (shopTypeRelation == null) {
			return false;
		}
		return true;
	}
}
