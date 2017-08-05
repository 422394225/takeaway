/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.order;

import com.jfinal.aop.Before;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.jfinal.MsgInterceptor;

import core.model.User;
import core.weixin.controller.WeixinMsgController;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月24日下午10:03:40
 */

public class OrderController extends WeixinMsgController {
	private Log log = Log.getLog(OrderController.class);

	@Before(MsgInterceptor.class)
	public void index() {
		String openid = getAttrForStr("openid");
		User user = User.dao.findById(openid);
		setAttr("user", user.toJson());
	}

}
