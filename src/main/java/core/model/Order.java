/**
* Created At 2017年7月9日上午2:16:19 
 * 
 */

package core.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月9日上午2:16:19
 */

public class Order extends Model<Order> {

	/** 如何描述serialVersionUID */
	private static final long serialVersionUID = 2333536279692051998L;
	public static final Order dao = new Order();

}
