/**
 * Created At 2017年4月17日下午9:48:53 
 * 
 */

package core.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月17日下午9:48:53
 */

public class WxApiConfigInterceptor extends Controller implements Interceptor {

	@Override
	public void intercept(Invocation ai) {
		try {
			ApiConfigKit.setThreadLocalApiConfig(getApiConfig());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("ApiConfig初始化失败");
		}
		ai.invoke();
	}

	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();

		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));

		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
		return ac;
	}
}
