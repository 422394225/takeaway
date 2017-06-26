/**
 * Created At 2017年4月17日下午8:04:42 
 * 
 */

package core.weixin.api;

import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.utils.HttpUtils;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月17日下午8:04:42
 */
public class CustomApi {
	private final static String GET_KFLIST = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=";

	public static ApiResult getKFList() {
		String access_token = AccessTokenApi.getAccessTokenStr();
		String jsonResult = HttpUtils.get(GET_KFLIST + access_token);
		return new ApiResult(jsonResult);
	}

}
