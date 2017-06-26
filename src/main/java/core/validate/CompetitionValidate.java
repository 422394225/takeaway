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
public class CompetitionValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		try {
			UploadFile file = c.getFile("img", "competition");
			UploadFile posterFile = c.getFile("poster", "competition");
		} catch (Exception e) {
			e.printStackTrace();
		}
		validateRequired("title", "msg", "请填写主标题");
		validateRequired("type", "msg", "请选择比赛类别");
		validateRequired("hostId", "msg", "请选择主办方");
		validateRequired("address", "msg", "请填写联系地址");
		if (c.getAttr("limit") != null) {
			validateLong("limit", "msg", "请填写正确的人数限制");
		}
		validateRequired("content", "msg", "请填写比赛介绍");
		validateRequired("rule", "msg", "请填写比赛规则");
		validateRequired("award", "msg", "请填写奖项设置");
		if (c.getAttr("rel") != null) {
			validateUrl("rel", "msg", "请输入正确的url");
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}

}
