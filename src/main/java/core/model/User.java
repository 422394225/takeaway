/**
 * Created At 2017年1月26日下午2:03:27 
 * 
 */

package core.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月26日下午2:03:27
 */

public class User extends Model<User> {

	/** 如何描述serialVersionUID */
	private static final long serialVersionUID = -8695650734994702894L;
	public static final User dao = new User();
}
