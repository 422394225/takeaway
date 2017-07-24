/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.shop;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月24日下午10:03:40
 */

public class ShopController extends Controller {
	private Log log = Log.getLog(ShopController.class);

	public void add() {
		render("add.html");
	}
}
