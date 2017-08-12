package core.weixin.controller.index;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.interceptor.JSSDKInterceptor;
import core.model.Shop;
import core.model.ShopType;
import core.vo.JSONSuccess;

/**
 * 
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:40:27
 */
public class IndexController extends Controller {

	@Before(JSSDKInterceptor.class)
	public void index() {
		render("index.html");
	}

	public void ajaxShopTypes() {
		Map<String, Map<String, Object>> map = new HashMap<>();
		Map<String, Object> where = new HashMap<>();
		where.put("DELETED", " == 1");
		map.put("limitCond", where);
		List<ShopType> shopTypes = ShopType.dao.find(Db.getSqlPara("shopType.list", map));
		renderJson(new JSONSuccess(shopTypes));
	}

	public void ajaxShops() {
		List<Shop> shops = Shop.dao.find(Db.getSqlPara("shop.list"));
		renderJson(new JSONSuccess(shops));
	}

	public void search() {
		render("search.html");
	}
}