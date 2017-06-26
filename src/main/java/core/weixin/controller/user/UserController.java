/**
 * Created At 2017年5月19日上午5:52:47 
 * 
 */

package core.weixin.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

import org.apache.commons.lang3.StringUtils;

import core.model.User;
import core.utils.SMSUtils;
import core.validate.UserWXValidate;
import core.vo.JSONError;
import core.vo.JSONSuccess;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年5月19日上午5:52:47
 */

public class UserController extends Controller {

	public void detail() {
		try {
			String openId = getPara("openId");
			User user = User.dao.findById(openId);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("user", user);
			renderJson(new JSONSuccess(jsonObject));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONError("获取列表失败！"));
		}
	}

	@Before(UserWXValidate.class)
	public void save() {
		JSONObject jsonObject = getAttr("validate");
		User user = new User();
		user.set("NAME", jsonObject.get("name"));
		user.set("SEX", jsonObject.get("sex"));
		user.set("AGE", jsonObject.get("age"));
		String phone = jsonObject.getString("phone");
		user.set("PHONE", jsonObject.get("phone"));
		String code = jsonObject.getString("code");
		JSONObject smsResult = SMSUtils.validate(phone, code);
		if (smsResult.size() > 0 && smsResult.getBoolean("error")) {
			renderJson(new JSONError(smsResult.get("msg").toString()));
			return;
		}
		user.set("ID_NUM", jsonObject.get("idNum"));
		user.set("ROLE", jsonObject.get("role"));
		user.set("SCHOOL", jsonObject.get("school"));
		user.set("ACADEMY", jsonObject.get("academy"));
		user.set("MAJOR", jsonObject.get("major"));
		String openId = (String) jsonObject.get("openId");
		if (StringUtils.isNotEmpty(openId)) {
			user.set("ID", openId);
			user.update();
		} else {
			user.save();
		}
		renderJson(new JSONSuccess("修改成功!", null));
	}
}
