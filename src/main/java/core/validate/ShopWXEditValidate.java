/**
 * Created At 2017年7月20日下午9:27:21 
 * 
 */

package core.validate;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;

import core.admin.service.shop.ShopService;
import core.admin.service.shop.impl.ShopServiceImpl;
import core.validate.base.ShortCircuitValidate;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月20日下午9:27:21
 */

public class ShopWXEditValidate extends ShortCircuitValidate {
	private ShopService service = new ShopServiceImpl();

	@Override
	protected void validate(Controller c) {
		/*
		 * UploadFile file = null; try { file = c.getFile("img", "shop"); }
		 * catch (Exception e) { e.printStackTrace(); } if (file == null &&
		 * (StringUtils.isEmpty(c.getPara("id")) ||
		 * StringUtils.isEmpty(c.getPara("defaultImg")))) { addError("msg",
		 * "请上传商家LOGO"); }
		 */
		validateRequired("name", "msg", "请输入商家名称");
		validateRequired("adminName", "msg", "请输入负责人名称");
		validateRequired("shopTypeId", "msg", "请选择商家分类");
		validateRequired("logo", "msg", "请上传商家logo");
		validateRequired("address", "msg", "请输入商家地址");
		validateRequired("latitude", "msg", "请从地图上选择一个坐标,将用于定位");
		validateRequired("longitude", "msg", "请从地图上选择一个坐标,将用于定位");
		validateRequired("addressCode", "msg", "请从地图上选择一个坐标,将用于定位");
		if (StringUtils.isEmpty(c.getPara("id"))) {
			validateRequired("username", "msg", "请输入商家账号");
			if (service.registerd(c.getPara("username"))) {
				addError("msg", "该账号已注册！");
			}
			validateRequired("password", "msg", "请输入商家密码");
		}
		validateRequired("phone", "msg", "请输入手机号");
		validatePhone("phone", "msg", "请输入正确的手机号");
		validateRequired("deliveryPrice", "msg", "请输入起送价");
		validateDouble("deliveryPrice", "msg", "请输入正确的起送价(数字)");
		if (StringUtils.isNotEmpty(c.getPara("deliveryOffThreshold"))) {
			validateDouble("deliveryOffThreshold", "msg", "请输入正确的配送满减起价(数字)");
		}
		if (StringUtils.isNotEmpty(c.getPara("deliveryOff"))) {
			validateDouble("deliveryOff", "msg", "请输入正确的配送满减额(数字)");
		}
		if (StringUtils.isNotEmpty(c.getPara("redutionThreshold"))) {
			validateDouble("redutionThreshold", "msg", "请输入正确的消费满减起价(数字)");
		}
		if (StringUtils.isNotEmpty(c.getPara("redution"))) {
			validateDouble("redution", "msg", "请输入正确的消费满建额(数字)");
		}
		if (StringUtils.isNotEmpty(c.getPara("giftThreshold"))) {
			validateDouble("giftThreshold", "msg", "请输入正确的赠品起价(数字)");
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("error", true);
		c.render(new JsonRender().forIE());
	}
}
