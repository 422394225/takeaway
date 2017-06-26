/**
 * Created At 2017年2月27日上午1:42:02 
 * 
 */

package core.admin.service.base.impl;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.Map;

import core.admin.service.base.DataTableService;
import core.db.SqlFactory;
import core.vo.DTParams;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年2月27日上午1:42:02
 */

public class DataTableServiceImpl implements DataTableService {

	public Page<Record> getDTPage(DTParams params, String sqlTemplate) {
		return getDTPage(params, sqlTemplate, new HashMap<String, Map<String, Object>>());
	}

	public Page<Record> getDTPage(DTParams params, String sqlTemplate, Map<String, Map<String, Object>> queryParams) {
		int start = params.getStart();
		int length = params.getLength();
		int pageNumber;
		int pageSize;
		if (length == -1) {
			pageSize = Integer.MAX_VALUE;
			pageNumber = 1;
		} else {
			pageSize = length;
			pageNumber = start / length + 1;
		}
		Map<String, String> map = SqlFactory.createSql(sqlTemplate, params, queryParams);
		Page<Record> competitions = Db.paginate(pageNumber, pageSize, map.get("select"), map.get("from"));
		return competitions;
	}
}
