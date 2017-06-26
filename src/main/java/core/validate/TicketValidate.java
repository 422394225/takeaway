package core.validate;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;
import com.jfinal.validate.Validator;

public class TicketValidate extends Validator {

	@Override
	protected void validate(Controller c) {
		validateRequired("type", "msg", "类型不能为空");
		validateRequired("amount", "msg", "数量不能为空");
		validateRequired("validTime", "msg", "请填写过期时间");
		validateRequired("diningId", "msg", "请选择餐厅");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
