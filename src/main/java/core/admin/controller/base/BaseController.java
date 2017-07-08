/**
 * Created At 2017年7月9日上午1:17:11 
 * 
 */

package core.admin.controller.base;

import com.jfinal.core.Controller;

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

}
