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

public class JSONSuccess extends JSONResult {

	/** 如何描述serialVersionUID */
	private static final long serialVersionUID = 2283415250292526863L;

	public JSONSuccess() {
		super(false);
	}

	public JSONSuccess(String msg) {
		super(false, msg);
	}

	public JSONSuccess(Object data) {
		super(false, data);
	}

	public JSONSuccess(String msg, Object data) {
		super(false, msg, data);
	}

}
