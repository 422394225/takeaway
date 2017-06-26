package core.validate;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;
import com.jfinal.upload.UploadFile;
import com.jfinal.validate.Validator;

import org.apache.commons.lang3.StringUtils;

import core.admin.service.admin.AdminService;
import core.admin.service.admin.impl.AdminServiceImpl;

public class AdminValidate extends Validator {
	private AdminService adminService = new AdminServiceImpl();

	@Override
	protected void validate(Controller c) {
		try {
			UploadFile file = c.getFile("avatar", "admin");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (StringUtils.isEmpty(c.getPara("id"))) {//编辑时邮箱和密码非必填
			validateRequired("username", "msg", "用户名不能为空");
			validateRequired("password", "msg", "请输入密码");
		}
		validateRequired("email", "msg", "请输入您的邮箱");
		//validateRequired("roleId", "msg", "角色不能为空");
		validateEmail("email", "msg", "请输入正确的邮箱格式");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
