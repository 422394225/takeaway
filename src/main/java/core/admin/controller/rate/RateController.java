package core.admin.controller.rate;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import core.admin.service.rate.RateService;
import core.admin.service.rate.impl.RateServiceImpl;
import core.vo.DTParams;

/**
 * 
 * Description:
 * 
 * @author hjk
 * @date 2017年1月30日下午2:40:27
 */
public class RateController extends Controller {

	private Log log = Log.getLog(RateController.class);
	private RateService rateService = new RateServiceImpl();

	public void index() {
		render("list.html");
	}

	public void getData() {
		DTParams params = new DTParams(getParaMap());
		Page<Record> rate = rateService.getDTPage(params, "rate.list");
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", rate.getTotalRow());
		List<Record> list = rate.getList();
		result.put("data", list);
		result.put("recordsFiltered", rate.getTotalRow());
		renderJson(result);
	}

}