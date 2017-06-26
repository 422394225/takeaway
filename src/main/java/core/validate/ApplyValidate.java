/**
 * Created At 2017年4月15日下午3:32:46 
 * 
 */

package core.validate;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;

import core.validate.base.ShortCircuitValidate;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月15日下午3:32:46
 */

public class ApplyValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		validateRequired("competitionId", "msg", "请选择比赛");
		validateRequired("name", "msg", "请输入您的姓名");
		validateRequired("sex", "msg", "请选择您的性别");
		validateRequired("idNum", "msg", "请输入您的身份证号");
		validateRequired("phone", "msg", "请输入您的手机号");
		validateRequired("code", "msg", "请输入您的验证码");
		validateRequired("role", "msg", "请输入您的职务");
		validateRequired("school", "msg", "请输入您的学校");
		validateRequired("academy", "msg", "请输入您的学院");
		validateRequired("major", "msg", "请输入您的专业");
		validateRequired("needHotel", "msg", "请选择是否需要酒店");
		validateRequired("needInvoice", "msg", "请选择是否需要发票");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
