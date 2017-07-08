package core.common.routes;

import com.jfinal.config.Routes;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;

import core.admin.controller.admin.AdminController;
import core.admin.controller.customer.CustomerController;
import core.admin.controller.feedback.FeedbackController;
import core.admin.controller.food.FoodController;
import core.admin.controller.index.IndexController;
import core.admin.controller.log.LogController;
import core.admin.controller.menu.MenuController;
import core.admin.controller.role.RoleController;
import core.admin.controller.shop.type.ShopTypeController;
import core.admin.controller.tools.ToolsController;
import core.admin.controller.user.UserController;
import core.interceptor.AdminLoginInterceptor;
import core.interceptor.PowerInterceptor;

public class AdminRoutes extends Routes {

	@Override
	public void config() {
		setBaseViewPath("WEB-INF/admin/");
		addInterceptor(new AdminLoginInterceptor());
		addInterceptor(new PowerInterceptor());
		addInterceptor(new SessionInViewInterceptor());
		add("/", IndexController.class, "/");
		add("admin", AdminController.class);
		add("feedback", FeedbackController.class);
		add("log", LogController.class);
		add("role", RoleController.class);
		add("tools", ToolsController.class);
		add("user", UserController.class);
		add("customer", CustomerController.class);
		add("menu", MenuController.class);
		add("food", FoodController.class);
		add("shopType", ShopTypeController.class);
	}

}
