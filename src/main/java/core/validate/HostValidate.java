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
public class HostValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		String id = c.getPara("id");
		try {
			UploadFile file = c.getFile("logo", "/host");//如果传文件要先获取文件
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (id == null) {
			validateRequired("name", "msg", "请输入举办方名称");
			validateRequired("supervisor", "msg", "请输入负责人");
			validateRequired("phone", "msg", "请输入手机号码");
			validatePhone("phone", "msg", "请输入正确的手机/电话号码");
			validateRequired("address", "msg", "请输入联系地址");
		} else {

		}
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
