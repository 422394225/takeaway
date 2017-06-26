/**
 * Created At 2017年2月17日上午2:20:00 
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
 * @date 2017年2月17日上午2:20:00
 */

public class SaveResetValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		validateRequired("newPassword", "msg", "请输入您的密码");
		validateRequired("password", "msg", "请输入您的密码");
		validateEqualField("newPassword", "password", "msg", "两次输入不一致");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
