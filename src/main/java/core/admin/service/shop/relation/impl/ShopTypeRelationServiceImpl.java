/**
 * Created At 2017年7月8日下午11:27:53 
 * 
 */

package core.admin.service.shop.relation.impl;

import com.jfinal.plugin.activerecord.Db;

import core.admin.service.base.impl.DataTableServiceImpl;
import core.admin.service.shop.relation.ShopTypeRelationService;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月8日下午11:27:53
 */

public class ShopTypeRelationServiceImpl extends DataTableServiceImpl implements ShopTypeRelationService {
	public void deleteAll(String shopId) {
		String sql = "DELETE FROM t_shop_type_relation WHERE SHOPID=?";
		Db.update(sql, shopId);
	}
}
