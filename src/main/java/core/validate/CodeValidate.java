package core.validate;

import com.jfinal.core.Controller;

import core.utils.SecurityCodeTool;
import core.validate.base.ShortCircuitValidate;

/**
 * 
 * Description:邮箱校验器
 * 
 * @author weifaguo
 * @date 2017年2月15日下午10:01:37
 */
public class CodeValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		validateRequired("code", "msg", "code为空");
		validateRequired("id", "msg", "id为空");
		validateCode("id", "action", "code", "msg");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		//		c.render(new JsonRender().forIE());
		c.redirect("/error?type=code&msg=" + SecurityCodeTool.encrypt(c.getAttr("msg")));
	}

}
