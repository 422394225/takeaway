package core.validate;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;

import core.validate.base.ShortCircuitValidate;

/**
 * 
 * Description:邮箱校验器
 * 
 * @author weifaguo
 * @date 2017年2月15日下午10:01:37
 */
public class EmailValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		validateRequired("email", "msg", "请输入您的邮箱");
		validateEmail("email", "msg", "请输入正确的邮箱格式");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
