package core.common.routes;

import com.jfinal.config.Routes;
import core.weixin.controller.ShareController;
import core.weixin.controller.WeiXinOauthController;
import core.weixin.controller.WeixinApiController;
import core.weixin.controller.WeixinMsgController;
import core.weixin.controller.delivery.DeliveryApiController;
import core.weixin.controller.delivery.DeliveryController;
import core.weixin.controller.feedback.FeedbackController;
import core.weixin.controller.food.FoodController;
import core.weixin.controller.index.IndexController;
import core.weixin.controller.order.OrderController;
import core.weixin.controller.shop.ShopController;
import core.weixin.controller.tools.ToolsController;
import core.weixin.controller.user.UserController;

public class WeixinRoutes extends Routes {

	@Override
	public void config() {
		setBaseViewPath("WEB-INF/weixin/");
		// 微信
		add("/msg", WeixinMsgController.class);
		add("/api", WeixinApiController.class);
		add("/oauth", WeiXinOauthController.class);

		// controller路由
		add("/wx", IndexController.class, "/");
		add("/wx/shop", ShopController.class, "/shop");
		add("/wx/tools", ToolsController.class, "/tools");
		add("/wx/order", OrderController.class, "/order");
		add("/wx/delivery", DeliveryController.class, "/delivery");
		add("/wx/food", FoodController.class, "/food");
		// api接口
		add("/api/v1/user", UserController.class);
		add("/api/v1/share", ShareController.class);
		add("/api/v1/feedback", FeedbackController.class);
		add("/api/v1/delivery", DeliveryApiController.class);

	}
}
