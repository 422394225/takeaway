/**
 * Created At 2017年2月27日上午1:41:42 
 * 
 */

package core.admin.service.base;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.Map;

import core.vo.DTParams;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年2月27日上午1:41:42
 */

public interface DataTableService {
	Page<Record> getDTPage(DTParams params, String sqlTemplate);

	Page<Record> getDTPage(DTParams params, String sqlTemplate, Map<String, Map<String, Object>> queryParams);
}
