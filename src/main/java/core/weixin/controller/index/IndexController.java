package core.weixin.controller.index;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

import core.interceptor.JSSDKInterceptor;

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

	public void search() {
		String keyword = getPara("keyword");
		String type = getPara("type");
		setAttr("keyword", keyword);
		setAttr("type", type);
		render("search.html");
	}

}