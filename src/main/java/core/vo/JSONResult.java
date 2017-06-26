/**
 * Created At 2017年3月5日下午8:31:36 
 * 
 */

package core.vo;

import com.alibaba.fastjson.JSONObject;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年3月5日下午8:31:36
 */

public class JSONResult extends JSONObject {

	/** 如何描述serialVersionUID */
	private static final long serialVersionUID = -8603165913848923416L;

	protected JSONResult(boolean error) {
		put("error", error);
	}

	protected JSONResult(boolean error, String msg) {
		put("error", error);
		put("msg", msg);
	}

	protected JSONResult(boolean error, Object data) {
		put("error", error);
		put("data", data);
	}

	protected JSONResult(boolean error, String msg, Object data) {
		put("error", error);
		put("msg", msg);
		put("data", data);
	}

}
