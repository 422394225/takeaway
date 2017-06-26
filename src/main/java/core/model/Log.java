/**
 * Created At 2017年1月26日下午2:04:01 
 * 
 */

package core.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月26日下午2:04:01
 */

public class Log extends Model<Log> {

	/** 如何描述serialVersionUID */
	private static final long serialVersionUID = 2932396697556657729L;
	public static final Log dao = new Log();
}
