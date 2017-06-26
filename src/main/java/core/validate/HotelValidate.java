package core.validate;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;
import com.jfinal.validate.Validator;

public class HotelValidate extends Validator {

	@Override
	protected void validate(Controller c) {
		validateRequired("name", "msg", "酒店名不能为空");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
