/**
 * Created At 2017年1月30日下午2:29:33 
 * 
 */

package core.admin.controller.feedback;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import core.admin.service.feedback.impl.FeedbackServiceImpl;
import core.vo.DTParams;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:29:33
 */

public class FeedbackController extends Controller {
	private Log log = Log.getLog(FeedbackController.class);
	private FeedbackServiceImpl feedbackServiceImpl = new FeedbackServiceImpl();
	public void index() {
		render("list.html");
	}
	public void getData() {
		DTParams params = new DTParams(getParaMap());
		Page<Record> feedbacks = feedbackServiceImpl.getDTPage(params, "feedback.list");
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", feedbacks.getTotalRow());
		List<Record> list = feedbacks.getList();
		result.put("data", list);
		result.put("recordsFiltered", feedbacks.getTotalRow());
		renderJson(result);
	}
}
