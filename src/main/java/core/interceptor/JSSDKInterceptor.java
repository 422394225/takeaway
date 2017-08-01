package core.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.JsTicket;
import com.jfinal.weixin.sdk.api.JsTicketApi;
import com.jfinal.weixin.sdk.api.JsTicketApi.JsApiType;

import java.util.UUID;

/**
 * @author Javen
 * 2016年5月13日
 */
/**
 * 对整个Controller或者其中的方法添加JSSDK签名验证拦截器
 */

public class JSSDKInterceptor implements Interceptor {
	/**
	 * 如果要支持多公众账号，只需要在此返回各个公众号对应的 ApiConfig 对象即可 可以通过在请求 url 中挂参数来动态从数据库中获取
	 * ApiConfig 属性值
	 */
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

	public void intercept(Invocation ai) {
		Controller c = ai.getController();
		ApiConfigKit.setThreadLocalApiConfig(getApiConfig());
		String jsapi_ticket = CacheKit.get("jssdk", "ticket", new IDataLoader() {
			@Override
			public Object load() {
				JsTicket jsApiTicket = JsTicketApi.getTicket(JsApiType.jsapi);
				String jsapi_ticket = jsApiTicket.getTicket();
				return jsapi_ticket;

			}
		});
		String nonce_str = create_nonce_str();
		// 注意 URL 一定要动态获取，不能 hardcode.
		String url = "http://" + c.getRequest().getServerName() // 服务器地址
		// + ":"
		// + getRequest().getServerPort() //端口号
				+ c.getRequest().getContextPath() // 项目名称
				+ c.getRequest().getServletPath();// 请求页面或其他地址
		String qs = c.getRequest().getQueryString(); // 参数
		if (qs != null) {
			url = url + "?" + (c.getRequest().getQueryString());
		}
		String timestamp = create_timestamp();
		// 这里参数的顺序要按照 key 值 ASCII 码升序排序
		//注意这里参数名必须全部小写，且必须有序
		String str = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url="
				+ url;
		String signature = HashKit.sha1(str);

		JSONObject jssdk = new JSONObject();
		jssdk.put("appId", ApiConfigKit.getApiConfig().getAppId());
		jssdk.put("nonceStr", nonce_str);
		jssdk.put("timestamp", timestamp);
		jssdk.put("url", url);
		jssdk.put("signature", signature);
		jssdk.put("jsapi_ticket", jsapi_ticket);
		c.setAttr("jssdk", jssdk);
		ai.invoke();
	}

	private static String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	private static String create_nonce_str() {
		return UUID.randomUUID().toString();
	}

}
