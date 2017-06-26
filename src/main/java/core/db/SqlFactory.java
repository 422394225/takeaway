/**
 * Created At 2017年2月27日上午12:57:55 
 * 
 */

package core.db;

import com.jfinal.kit.JMap;
import com.jfinal.plugin.activerecord.Db;

import java.util.HashMap;
import java.util.Map;

import core.vo.DTParams;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年2月27日上午12:57:55
 */

public class SqlFactory {

	public static Map<String, String> createSql(String sqlTemplate, DTParams params) {
		return createSql(sqlTemplate, params, null);
	}

	public static Map<String, String> createSql(String sqlTemplate, DTParams params,
			Map<String, Map<String, Object>> queryParams) {
		Map<String, Map<String, Object>> cond = ConditionsFactory.getConditions(params);
		if (queryParams != null && queryParams.size() > 0) {
			cond.putAll(queryParams);
		}
		String sql = Db.getSql(sqlTemplate, JMap.create("conditions", cond));
		return separate(sql);
	}

	public static Map<String, String> separate(String sql) {
		Map<String, String> result = new HashMap<>();
		try {
			String selectSql = sql.substring(0, sql.indexOf("FROM"));
			String fromSql = sql.substring(sql.indexOf("FROM"));
			result.put("select", selectSql);
			result.put("from", fromSql);
		} catch (Exception e) {
			return result;
		}
		return result;
	}
}
