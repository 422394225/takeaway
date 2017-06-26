/**
 * Created At 2017年3月5日下午8:48:17 
 * 
 */

package core.vo;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年3月5日下午8:48:17
 */

public class JSONError extends JSONResult {

	/** 如何描述serialVersionUID */
	private static final long serialVersionUID = 4507334418175800429L;

	public JSONError() {
		super(true);
	}

	public JSONError(String msg) {
		super(true, msg);
	}

	public JSONError(Object data) {
		super(true, data);
	}

	public JSONError(String msg, Object data) {
		super(true, msg, data);
	}

}
