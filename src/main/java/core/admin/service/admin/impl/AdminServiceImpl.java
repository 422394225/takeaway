/**
 * Created At 2017年2月7日上午12:10:56 
 * 
 */

package core.admin.service.admin.impl;

import core.admin.service.admin.AdminService;
import core.admin.service.base.impl.DataTableServiceImpl;
import core.model.Admin;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年2月7日上午12:10:56
 */

public class AdminServiceImpl extends DataTableServiceImpl implements AdminService {

	public Admin findByUsername(String username) {
		String sql = "select * from t_admin where USERNAME = ? AND ENABLE = 1";
		Admin admin = Admin.dao.findFirst(sql, username);
		return admin;
	}

	public Admin findByEmail(String email) {
		String sql = "select * from t_admin where EMAIL = ? AND ENABLE = 1";
		Admin admin = Admin.dao.findFirst(sql, email);
		return admin;
	}
}
