/**
 * Created At 2017年4月15日下午9:15:48 
 * 
 */

package core.admin.service.customer.impl;

import com.jfinal.plugin.activerecord.Db;

import core.admin.service.base.impl.DataTableServiceImpl;
import core.admin.service.customer.CustomerService;
import core.model.Customer;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月15日下午9:15:48
 */

public class CustomerServiceImpl extends DataTableServiceImpl implements CustomerService {

	@Override
	public Customer findByKfAccount(String kfAccount) {
		String sql = "SELECT * FROM t_customer WHERE DUOKEFU = ?";
		Customer customer = Customer.dao.findFirst(sql, kfAccount);
		return customer;
	}

	@Override
	public long countByKfAccount(String kfAccount) {
		String sql = "SELECT count(*) FROM t_customer WHERE DUOKEFU LIKE ?";
		long count = Db.queryLong(sql, kfAccount + "%");
		return count;
	}

}
