package core.temple;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;

public abstract class TempleModel {
	static final Log log = Log.getLog(TempleModel.class);

	public String touser;
	public String template_id;
	public String url;
	public String topcolor = "#FF0000";

	public abstract JSONObject getData();

	public String toJson() {
		template_id = PropKit.get(this.getClass().getName().replace("Temple", "").replace("core.", ""));
		JSONObject object = new JSONObject();
		object.put("touser", touser);
		object.put("template_id", template_id);
		if (url != null)
			object.put("url", url);
		object.put("topcolor", topcolor);
		object.put("data", getData());
		return object.toString();
	};

	protected JSONObject getDataObject(String value) {
		return getDataObject(value, "#173177");
	}

	protected JSONObject getDataObject(String value, String color) {
		JSONObject object = new JSONObject();
		object.put("value", value);
		object.put("color", color);
		return object;
	}

	public boolean send() {
		ApiResult apiResult = TemplateMsgApi.send(toJson());
		if (apiResult.getErrorCode() == 0) {
			return true;
		} else {
			log.error("发送模板消息失败(" + apiResult.getErrorCode() + ")：" + apiResult.getErrorMsg());
			return false;
		}
	}

	public String sendAndBackMsg() {
		ApiResult apiResult = TemplateMsgApi.send(toJson());
		if (apiResult.getErrorCode() == 0) {
			return "发送成功！";
		} else {
			log.error("发送模板消息失败(" + apiResult.getErrorCode() + ")：" + apiResult.getErrorMsg());
			return apiResult.getErrorMsg();
		}
	}
}
