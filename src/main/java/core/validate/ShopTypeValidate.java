package core.validate;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;
import com.jfinal.upload.UploadFile;

import org.apache.commons.lang3.StringUtils;

import core.validate.base.ShortCircuitValidate;

public class ShopTypeValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		UploadFile file = null;
		try {
			file = c.getFile("icon", "shopType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (file == null && (StringUtils.isEmpty(c.getPara("id")) || StringUtils.isEmpty(c.getPara("defaultImg")))) {
			addError("msg", "请上传商家分类图标");
		}
		validateRequired("name", "msg", "请输入商家分类名称");
		validateRequired("deleted", "msg", "请选择是否启用");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
