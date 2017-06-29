/**
 * Created At 2017年6月29日下午8:15:36 
 * 
 */

package core.weixin.controller.test;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年6月29日下午8:15:36
 */

public class TestController extends Controller {
	private Log log = Log.getLog(TestController.class);

	public void index() {
		render("order.html");
	}
}
