/**
 * Created At 2017年4月10日下午7:57:19 
 * 
 */

package core.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import core.admin.service.menu.MenuService;
import core.admin.service.menu.impl.MenuServiceImpl;
import core.model.Admin;
import core.model.Menu;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月10日下午7:57:19
 */

public class PowerInterceptor extends Controller implements Interceptor {

	@Override
	public void intercept(Invocation ai) {
		Controller controller = ai.getController();
		String ck = ai.getControllerKey();
		String method = ai.getMethodName();
		String requestUrl = ck + "/" + method;
		if (method.contains("get") && method.contains("Data")) {
			ai.invoke();
			return;
		}
		Admin admin = controller.getSessionAttr("administrator");
		if (admin == null) {
			controller.redirect("/login");
			return;
		}
		List<Menu> menus = CacheKit.get("menuCache", admin.get("ID") + "menus");
		if (menus == null) {
			MenuService menuService = new MenuServiceImpl();
			CacheKit.put("menuCache", admin.get("ID") + "menus", menuService.getMenuByPower(admin));
			menus = CacheKit.get("menuCache", admin.get("ID") + "menus");
		}
		for (Menu menu : menus) {
			String url = menu.getStr("URL");
			if (StringUtils.isNotEmpty(url)) {
				if (url.contains(ck) && !"/".equals(url)) {
					ai.invoke();
					return;
				}
			} else {
				continue;
			}
		}
		controller.redirect("/error?type=power");
	}

}
