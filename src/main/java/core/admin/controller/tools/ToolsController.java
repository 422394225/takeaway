/**
 * Created At 2017年4月9日下午1:55:53 
 * 
 */

package core.admin.controller.tools;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

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
public class ToolsController extends Controller {

	public void index() {
		render("index.html");
	}

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

	public void getLocation() {
		setAttr("key", PropKit.get("amap.key"));
		render("map.html");
	}

}
