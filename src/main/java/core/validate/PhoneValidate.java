/**
 * Created At 2017年4月20日下午4:49:08 
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
 * @date 2017年4月20日下午4:49:08
 */

public class PhoneValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		validateRequired("phone", "msg", "请输入手机号码");
		validatePhone("phone", "msg", "请输入正确的手机/电话号码");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
