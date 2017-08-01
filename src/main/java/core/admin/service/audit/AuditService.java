/**
 * Created At 2017年7月30日下午2:17:46 
 * 
 */

package core.admin.service.audit;

import java.util.List;

import core.model.Audit;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月30日下午2:17:46
 */

public interface AuditService {
	List<Audit> getByShopId(String shopId);
}
