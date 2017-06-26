package core.weixin.controller.feedback;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;

import core.model.Feedback;
import core.utils.StringTool;
import core.vo.JSONSuccess;

public class FeedbackController extends Controller {

	public void save() {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(HttpKit.readData(getRequest()));
		Feedback feedback = new Feedback();
		feedback.set("ID", StringTool.getUUID());
		feedback.set("QUESTION", jsonObject.get("question"));
		feedback.set("DESCRIBE", jsonObject.get("describe"));
		feedback.set("OPENID", jsonObject.get("openid"));
		feedback.save();
		renderJson(new JSONSuccess());
	}
}
