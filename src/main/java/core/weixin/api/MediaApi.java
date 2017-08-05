/**
 * Created At 2017年8月6日上午1:31:39 
 * 
 */

package core.weixin.api;

import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.utils.HttpUtils;

import java.io.InputStream;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年8月6日上午1:31:39
 */

public class MediaApi {
	private static final String MEDIA_GET = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="
			+ AccessTokenApi.getAccessTokenStr() + "&media_id=";

	public static InputStream mediaGet(String mediaId) {
		InputStream in = HttpUtils.download(MEDIA_GET + mediaId, null);
		return in;
	}
}
