package core.admin.controller.index;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

import core.interceptor.AdminLoginInterceptor;
import core.interceptor.PowerInterceptor;
import core.model.Delivery;
import core.model.Shop;
import core.utils.SecurityCodeTool;

/**
 * 
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:40:27
 */
public class IndexController extends Controller {

	public void getShopPlaceDot() {
		JSONArray result = new JSONArray();
		List<Delivery> shops = Delivery.dao.find("select ID,`NAME`,LONGITUDE,LATITUDE from t_shop where state>-1");
		for (Delivery shop : shops) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("NAME", shop.get("NAME"));
			jsonObject.put("ID", shop.get("ID"));
			JSONArray place = new JSONArray();
			place.add(shop.get("LONGITUDE"));
			place.add(shop.get("LATITUDE"));
			jsonObject.put("PLACE", place);
			result.add(jsonObject);
		}
		renderJson(result);

	}

	public void index() {
		setAttr("amapKey", PropKit.get("amap.key"));
		List<Shop> shops = Shop.dao.find("select LONGITUDE,LATITUDE from t_shop where state>-1");
		double LONGITUDE = 0;
		double LATITUDE = 0;
		for (Shop shop : shops) {
			LONGITUDE += shop.getDouble("LONGITUDE");
			LATITUDE += shop.getDouble("LATITUDE");
		}
		LONGITUDE /= shops.size();
		LATITUDE /= shops.size();
		setAttr("LONGITUDE", LONGITUDE);
		setAttr("LATITUDE", LATITUDE);
		render("index.html");
	}

	@Clear({ AdminLoginInterceptor.class, PowerInterceptor.class })
	public void login() {
		render("login.html");
	}

	@Clear({ AdminLoginInterceptor.class, PowerInterceptor.class })
	public void about() {
		render("about.html");
	}

	@Clear({ AdminLoginInterceptor.class, PowerInterceptor.class })
	public void error() {
		String type = getPara("type");
		if ("power".equals(type)) {
			setAttr("code", "401");
			setAttr("msg", "您不具有该URL的访问权限！");
		} else if ("oauth".equals(type)) {
			setAttr("code", "408");
			setAttr("msg", "微信授权失败！");
		} else if ("code".equals(type)) {
			setAttr("code", "");
			setAttr("msg", SecurityCodeTool.decrypt(getPara("msg")));
		} else {
			setAttr("msg", "");
			setAttr("code", "");
		}
		render("error.html");
	}
}