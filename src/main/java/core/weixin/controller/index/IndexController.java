package core.weixin.controller.index;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

import com.jfinal.plugin.activerecord.Db;
import core.interceptor.JSSDKInterceptor;
import core.model.Banner;

import java.util.List;

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
		List<Banner> banners = Banner.dao.find(Db.getSql("banner.getActive"));
		setAttr("banners",banners);
		render("index.html");
	}

	public void search() {
		String keyword = getPara("keyword");
		String type = getPara("type");
		setAttr("keyword", keyword);
		setAttr("type", type);
		render("search.html");
	}

}