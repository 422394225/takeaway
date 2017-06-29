/**
 * Created At 2017年4月9日下午1:55:53 
 * 
 */

package core.admin.controller.tools;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;

import core.utils.SMSUtils;
import core.validate.PhoneValidate;
import core.vo.JSONError;
import core.vo.JSONSuccess;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月9日下午1:55:53
 */
@Clear
public class ToolsController extends Controller {

	public void cutter() {
		setAttr("ratio", getPara("ratio"));
		render("cutter.html");
	}

	@Before(PhoneValidate.class)
	public void sendCode() {
		String phone = getPara("phone");
		JSONObject jsonObject = SMSUtils.send(phone);
		if (jsonObject.size() == 0) {
			renderJson(new JSONSuccess("发送成功！"));
		} else {
			renderJson(new JSONError());
		}
	}

}
