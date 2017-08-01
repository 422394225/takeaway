
package core.admin.service.order.impl;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import core.admin.service.base.impl.DataTableServiceImpl;
import core.admin.service.order.OrderService;

public class OrderServiceImpl extends DataTableServiceImpl implements OrderService {

	@Override
	public Record getDetail(String orderId) {
		String sql = "SELECT o.*, s. NAME AS SHOP_NAME,d.NAME AS DELIVERY_NAME,u.NAME AS USER_ID FROM t_order AS o "
				+ " LEFT JOIN t_shop s ON o.SHOP_ID = s.ID LEFT JOIN t_delivery d ON o.DELIVERY_ID=d.ID"
				+ " LEFT JOIN t_user u ON o.USER_ID = u.ID where o.ID=? ";
		return Db.findFirst(sql, orderId);
	}

	@Override
	public Record getSimpleFoodHistroy(int foodId) {
		String sql = "SELECT A.NAME,A.PRICE,A.TYPE_ID,A.UNIT,B.NAME AS TYPE,A.IMG FROM T_FOOD A LEFT JOIN T_FOOD_TYPE B ON A.TYPE_ID=B.ID WHERE A.ID=?";
		return Db.findFirst(sql, foodId);
	}
}
