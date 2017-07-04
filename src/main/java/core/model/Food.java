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

public class Food extends Model<Food> {

	/** 如何描述serialVersionUID */
	private static final long serialVersionUID = -5822031050151872770L;
	public static final Food dao = new Food();

}
