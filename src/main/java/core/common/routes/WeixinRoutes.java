package core.common.routes;

import com.jfinal.config.Routes;

import core.interceptor.AllowOriginInterceptor;
import core.weixin.controller.JSSDKController;
import core.weixin.controller.ShareController;
import core.weixin.controller.WeiXinOauthController;
import core.weixin.controller.WeixinApiController;
import core.weixin.controller.WeixinMsgController;
import core.weixin.controller.feedback.FeedbackController;
import core.weixin.controller.user.UserController;

public class WeixinRoutes extends Routes {

	@Override
	public void config() {
		addInterceptor(new AllowOriginInterceptor());
		// 微信
		add("/msg", WeixinMsgController.class);
		add("/api", WeixinApiController.class);
		add("/oauth", WeiXinOauthController.class);
		add("/api/v1/jssdk", JSSDKController.class);

		// api接口
		add("/api/v1/user", UserController.class);

		add("/api/v1/share", ShareController.class);
		add("/api/v1/feedback", FeedbackController.class);

	}
}
