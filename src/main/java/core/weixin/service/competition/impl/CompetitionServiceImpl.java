/**
 * Created At 2017年4月5日下午3:46:34 
 * 
 */

package core.weixin.service.competition.impl;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.Map;

import core.db.SqlFactory;
import core.vo.ConditionsVO;
import core.weixin.service.competition.CompetitionService;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月5日下午3:46:34
 */

public class CompetitionServiceImpl implements CompetitionService {
	public Page<Record> listCompetitions(Integer pageNumber, Integer pageSize) {
		ConditionsVO conditionsVO = new ConditionsVO();
		Map<String, Object> orderby = new HashMap<>();
		orderby.put("CREATE_TIME", "DESC");
		conditionsVO.getOrderbyCond().putAll(orderby);
		Map<String, String> map = SqlFactory.separate(Db.getSql("competition.list", conditionsVO.getConditions()));
		Page<Record> competitions = Db.paginate(pageNumber, pageSize, map.get("select"), map.get("from"));
		return competitions;
	}

}
