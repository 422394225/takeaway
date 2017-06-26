package core.validate;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;

import core.validate.base.ShortCircuitValidate;

/**
 * 登录校验器
 * @author Javen
 * 2016年4月2日
 */
public class LoginValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		validateRequired("username", "msg", "请输入您的账号");
		validateRequired("password", "msg", "请输入您的密码");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
