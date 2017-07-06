/**
 * Created At 2017年2月27日上午12:43:24 
 * 
 */

package core.db;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import core.vo.DTParams;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年2月27日上午12:43:24
 */

public class ConditionsFactory {
	public static Map<String, Map<String, Object>> getConditions(DTParams params) {
		Map<String, Map<String, Object>> conditions = new LinkedHashMap<>();
		//		Map<String, Object> whereCond = new HashMap<>();
		Map<String, Object> orderbyCond = new HashMap<>();
		Map<String, Object> limitCond = new HashMap<>();
		int i = 0;
		String data = "";
		while (StringUtils.isNoneEmpty(data = params.getCData(i))) {
			//去掉序号和操作栏
			if (data.equalsIgnoreCase("ingnore")) {
				i++;
				continue;
			}
			if (params.getCSearchable(i)) {
				//				String searchValue = params.getSearchValue();//全局搜索词
				//				String cSearchValue = params.getCSearchValue(i);//
				//				if (StringUtils.isNoneEmpty(cSearchValue)) {
				//					cSearchValue = cSearchValue.trim();
				//					limitCond.put(data, "LIKE BINARY '%" + cSearchValue + "%'");
				//				} else if (StringUtils.isNoneEmpty(searchValue)) {
				//					searchValue = searchValue.trim();
				//					whereCond.put(data, searchValue);
				//				}
				if (params.getOrderable(i)) {
					if (params.getOrderColumn() == i/* && !data.equals("ID")*/) {
						String orderDir = params.getOrderDir();
						if (StringUtils.isNoneEmpty(orderDir)) {
							orderbyCond.put(data, orderDir);
						}
					}
				}
			}
			i++;
		}
		//		conditions.put("whereCond", whereCond);
		JSONArray custom_param = params.getCustomParams();
		if (custom_param.size() > 0) {
			for (int j = 0; j < custom_param.size(); j++) {
				JSONObject param = custom_param.getJSONObject(j);
				for (String key : param.keySet()) {
					limitCond.put(key, "LIKE BINARY '%" + param.getString(key) + "%'");
				}
			}
		}
		conditions.put("orderbyCond", orderbyCond);
		conditions.put("limitCond", limitCond);
		return conditions;
	}
}
