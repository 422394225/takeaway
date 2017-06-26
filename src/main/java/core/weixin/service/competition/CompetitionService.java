/**
 * Created At 2017年4月5日下午3:46:05 
 * 
 */

package core.weixin.service.competition;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月5日下午3:46:05
 */

public interface CompetitionService {
	Page<Record> listCompetitions(Integer pageNumber, Integer pageSize);
}
