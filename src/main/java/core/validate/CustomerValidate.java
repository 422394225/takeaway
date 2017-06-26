package core.validate;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;
import com.jfinal.upload.UploadFile;

import core.validate.base.ShortCircuitValidate;

/**
 * 登录校验器
 * @author Javen
 * 2016年4月2日
 */
public class CustomerValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		String id = c.getPara("id");
		try {
			UploadFile file = c.getFile("logo", "/customer");//如果传文件要先获取文件
		} catch (Exception e) {
			e.printStackTrace();
		}
		validateRequired("nickname", "msg", "请输入昵称");
		validateRequired("password", "msg", "请输入多客服密码");
		validateRequired("weixin", "msg", "请输入微信号");
		validateRequired("phone", "msg", "请输入手机号");
		validateRequired("describe", "msg", "请输入描述");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
