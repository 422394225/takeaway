/**
 * Created At 2017年2月7日上午12:10:36 
 * 
 */

package core.admin.service.admin;

import core.admin.service.base.DataTableService;
import core.model.Admin;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年2月7日上午12:10:36
 */

public interface AdminService extends DataTableService {
	Admin findByUsername(String username);

	Admin findByEmail(String email);
}
