package core.validate;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;
import com.jfinal.validate.Validator;

public class DiningValidate extends Validator {

	@Override
	protected void validate(Controller c) {
		validateRequired("name", "msg", "餐厅名不能为空");
		validateRequired("address", "msg", "餐厅地点不能为空");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
