package core.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import core.model.Admin;
import core.utils.DateUtils;
import core.utils.SecurityCodeTool;
import core.utils.WebUtils;

/**
 * 
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:43:15
 */
public class AdminLoginInterceptor extends Controller implements Interceptor {

	public void intercept(Invocation ai) {
		Controller controller = ai.getController();
		// controller.setAttr("goeasy.SubscribeKey",
		// PropKit.get("goeasy.SubscribeKey"));
		// controller.setAttr("goeasy.RESTHost",
		// PropKit.get("goeasy.RESTHost"));
		Admin administrator = controller.getSessionAttr("administrator");
		if (administrator == null) {
			String coockie = WebUtils.getCookie(controller.getRequest(), "administrator");
			if (coockie != null) {// 有cookie自动登录
				// 解析cookie
				String origin = "";
				try {
					origin = SecurityCodeTool.decrypt(coockie);
				} catch (Exception e) {
					e.printStackTrace();
					controller.redirect("/login");
					return;
				}
				String[] info = origin.split("@");
				if (DateUtils.isOvertime(Long.parseLong(info[1]), 3600 * 24 * 7L)) {// 过期则重新登录
					controller.redirect("/login");
				} else {
					Admin admin = Admin.dao.findById(info[0]);
					WebUtils.refreshSession(admin, controller);
					ai.invoke();
				}
				return;
			}
			controller.redirect("/login");
		} else {
			ai.invoke();
		}
	}

}
