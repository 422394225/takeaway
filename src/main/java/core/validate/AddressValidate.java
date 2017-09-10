/**
 * Created At 2017年7月20日下午9:27:21 
 * 
 */

package core.validate;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;
import core.validate.base.ShortCircuitValidate;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月20日下午9:27:21
 */

public class AddressValidate extends ShortCircuitValidate {

	@Override
	protected void validate(Controller c) {
		validateRequired("openId", "msg", "未获取到用户信息");
		validateRequired("realName", "msg", "请输入收货人姓名");
		validateRequired("sex", "msg", "请选择性别");
		validateRequired("address", "msg", "请输入收货地址");
		validateRequired("latitude", "msg", "请从地图上选择一个坐标,将用于定位");
		validateRequired("longitude", "msg", "请从地图上选择一个坐标,将用于定位");
		validateRequired("addressCode", "msg", "请从地图上选择一个坐标,将用于定位");
		validateRequired("phone", "msg", "请输入联系电话");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}
}
