/**
 * Created At 2017年1月26日下午2:04:01 
 * 
 */

package core.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * 
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月30日下午12:48:09
 */
public class Audit extends Model<Audit> {

	/** 如何描述serialVersionUID */
	private static final long serialVersionUID = 6361588398203750400L;
	public static final Audit dao = new Audit();

}
