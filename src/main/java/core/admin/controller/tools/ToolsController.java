/**
 * Created At 2017年4月9日下午1:55:53 
 * 
 */

package core.admin.controller.tools;

import java.awt.Desktop;
import java.io.File;
import java.net.URLDecoder;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

import core.interceptor.WxApiConfigInterceptor;
import core.model.Order;
import core.utils.SMSUtils;
import core.utils.WeiXinUtils;
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

	public void reboot() {
		String filePath = URLDecoder.decode(getClass().getClassLoader().getResource("reboot.bat").getPath());
		File rebootFile = new File(filePath);
		if (!rebootFile.exists()) {
			renderJson(new JSONError("重启脚本损坏"));
			return;
		}
		try {
			Desktop.getDesktop().open(rebootFile);
			renderJson(new JSONSuccess("重启成功，请等待后刷新"));
		} catch (Exception e) {
			renderJson(new JSONError("调起重启失败"));
		}
	}

	@Before(WxApiConfigInterceptor.class)
	public void sendMess() {
		Order order = Order.dao.findById(107);
		WeiXinUtils.sendOrderVideoAndMess(order);
		renderJson(new JSONSuccess("发送成功"));
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
