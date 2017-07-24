/**
 * Created At 2017年7月9日上午1:17:11 
 * 
 */

package core.admin.controller.base;

import com.jfinal.core.Controller;

import org.apache.commons.lang3.StringUtils;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月9日上午1:17:11
 */

public class BaseController extends Controller {
	public void index() {
		render("list.html");
	}

	public Double getParaToDouble(String name) {
		String value = getPara(name);
		if (StringUtils.isEmpty(value)) {
			value = "0";
		}
		return Double.parseDouble(value);
	}

}
