/**
 * Created At 2017年4月15日下午3:32:46 
 * 
 */

package core.validate;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.render.JsonRender;

import core.validate.base.ShortCircuitValidate;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月15日下午3:32:46
 */

public class ApplyWXValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		JSONObject jsonObject = JSONObject.parseObject(HttpKit.readData(c.getRequest()));
		validatePostRequired(jsonObject.getString("name"), "msg", "请输入您的姓名");
		validatePostRequired(jsonObject.getString("sex"), "msg", "请选择您的性别");
		validatePostRequired(jsonObject.getString("age"), "msg", "请输入您的年龄");
		validatePostRequired(jsonObject.getString("idNum"), "msg", "请输入您的身份证号");
		validatePostRequired(jsonObject.getString("phone"), "msg", "请输入您的手机号");
		validatePostRequired(jsonObject.getString("code"), "msg", "请输入您的验证码");
		validatePostRequired(jsonObject.getString("role"), "msg", "请输入您的职务");
		validatePostRequired(jsonObject.getString("school"), "msg", "请输入您的学校");
		validatePostRequired(jsonObject.getString("academy"), "msg", "请输入您的学院");
		validatePostRequired(jsonObject.getString("major"), "msg", "请输入您的专业");
		validatePostRequired(jsonObject.getString("needHotel"), "msg", "请选择是否需要酒店");
		validatePostRequired(jsonObject.getString("needInvoice"), "msg", "请选择是否需要发票");
		c.setAttr("validate", jsonObject);
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
