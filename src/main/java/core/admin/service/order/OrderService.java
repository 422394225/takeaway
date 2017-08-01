
package core.admin.service.order;

import com.jfinal.plugin.activerecord.Record;

import core.admin.service.base.DataTableService;

public interface OrderService extends DataTableService {

	Record getDetail(String orderId);

	Record getSimpleFoodHistroy(int foodId);
}
