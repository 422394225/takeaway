package core.validate;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.render.JsonRender;
import com.jfinal.validate.Validator;

public class ModPassValidate extends Validator {
	@Override
	protected void validate(Controller c) {
		String id = c.getPara("id");
		String password = c.getPara("password");
		String newpass = c.getPara("newpass");
		String confirmPass = c.getPara("confirm");
		String pString = Db.queryStr("select PASSWORD from t_shop where ID=?", id);
		validateRequired("password", "msg", "原密码不能为空");
		validateRequired("newpass", "msg", "新密码不能为空");
		validateRequired("confirm", "msg", "重复密码不能为空");
		if (password.equals(pString)) {
			addError("msg", "原密码错误");
		}
		if (!newpass.equals(confirmPass)) {
			addError("msg", "两次密码不一致");
		}

	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
