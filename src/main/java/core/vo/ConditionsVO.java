/**
 * Created At 2017年4月15日上午1:39:53 
 * 
 */

package core.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月15日上午1:39:53
 */

public class ConditionsVO {
	private Map<String, Object> conditions = new HashMap<>();
	private Map<String, Object> whereCond = new HashMap<>();
	private Map<String, Object> limitCond = new HashMap<>();
	private Map<String, Object> orderbyCond = new HashMap<>();

	public ConditionsVO() {
		Map<String, Object> map = new HashMap<>();
		map.put("whereCond", whereCond);
		map.put("limitCond", limitCond);
		map.put("orderbyCond", orderbyCond);
		conditions.put("conditions", map);
	}

	public Map<String, Object> getConditions() {
		return conditions;
	}

	public void setConditions(Map<String, Object> conditions) {
		this.conditions = conditions;
	}

	public Map<String, Object> getWhereCond() {
		return whereCond;
	}

	public void setWhereCond(Map<String, Object> whereCond) {
		this.whereCond = whereCond;
	}

	public Map<String, Object> getLimitCond() {
		return limitCond;
	}

	public void setLimitCond(Map<String, Object> limitCond) {
		this.limitCond = limitCond;
	}

	public Map<String, Object> getOrderbyCond() {
		return orderbyCond;
	}

	public void setOrderbyCond(Map<String, Object> orderbyCond) {
		this.orderbyCond = orderbyCond;
	}

}
