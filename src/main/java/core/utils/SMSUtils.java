package core.utils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Javen
 * 2016年4月3日
 */
public class SMSUtils {
	static Log log = Log.getLog(SMSUtils.class);
	private static final Prop prop = PropKit.use("sms.properties");
	private final static String SEND_URL = prop.get("send_url");
	private final static String VLD_URL = prop.get("vld_url");
	private final static String APP_ID = prop.get("app_id");
	private final static String APP_SECRECT = prop.get("app_key");

	public static JSONObject send(String mobile) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json;charset=utf-8");
		headers.put("X-LC-Id", APP_ID);
		headers.put("X-LC-Key", APP_SECRECT);
		JSONObject data = new JSONObject();
		data.put("mobilePhoneNumber", mobile);
		String result = "{msg:发送失败,error:true}";
		try {
			result = HttpKit.post(SEND_URL, data.toJSONString(), headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (JSONObject) JSONObject.parse(result);
	}

	public static JSONObject validate(String mobile, String code) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json;charset=utf-8");
		headers.put("X-LC-Id", APP_ID);
		headers.put("X-LC-Key", APP_SECRECT);
		String result = "{msg:'验证失败!请重新发送！',error:true}";
		try {
			result = HttpKit.post(VLD_URL + code + "?mobilePhoneNumber=" + mobile, "", headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (JSONObject) JSONObject.parse(result);
	}

}
