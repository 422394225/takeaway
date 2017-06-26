/**
 * Created At 2017年1月30日下午2:29:33 
 * 
 */

package core.admin.controller.log;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import core.admin.service.log.impl.LogServiceImpl;
import core.vo.DTParams;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:29:33
 */

public class LogController extends Controller {
	private Log log = Log.getLog(LogController.class);
	private LogServiceImpl logServiceImpl = new LogServiceImpl();
	public void index() {
		render("list.html");
	}

	public void getData() {
		DTParams params = new DTParams(getParaMap());
		Page<Record> logs = logServiceImpl.getDTPage(params, "log.list");
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", logs.getTotalRow());
		List<Record> list = logs.getList();
		result.put("data", list);
		result.put("recordsFiltered", logs.getTotalRow());
		renderJson(result);
	}
}
