package core.weixin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.jfinal.ApiController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Javen 2015年12月5日下午2:20:44
 *
 */

public class WeiXinOauthController extends ApiController {
	static Log log = Log.getLog(WeiXinOauthController.class);

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

	public void index() {
		// 用户同意授权，获取code
		String code = getPara("code");
		String state = getPara("state");
		String redirect = getPara("redirect");
		if (code != null) {
			String appId = ApiConfigKit.getApiConfig().getAppId();
			String secret = ApiConfigKit.getApiConfig().getAppSecret();
			// 通过code换取网页授权access_token
			SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(appId, secret, code);
			// String json=snsAccessToken.getJson();
			// String token = snsAccessToken.getAccessToken();
			String openId = snsAccessToken.getOpenid();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("openId", openId);

			if (openId == null) {//没获取到重试
				String oauthUrl = null;
				String stateUrl = null;
				try {
					oauthUrl = URLEncoder.encode(PropKit.get("server.address")+"/oauth","UTF-8");
					stateUrl = URLEncoder.encode(state,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri="+oauthUrl+"&response_type=code&scope=snsapi_base&state="+stateUrl+"#wechat_redirect";
				redirect(url);
			} else {
				getRequest().setAttribute("openId", openId);
				forwardAction(state);
			}
			/*
			 * //拉取用户信息(需scope为 snsapi_userinfo) ApiResult apiResult =
			 * SnsApi.getUserInfo(token, openId);
			 * 
			 * log.warn("getUserInfo:" + apiResult.getJson()); if
			 * (apiResult.isSucceed()) { JSONObject jsonObject =
			 * JSON.parseObject(apiResult.getJson()); User user =
			 * User.dao.findById(openId); jsonObject.put("user", user); if
			 * ("register".equals(state)) { String competitionId =
			 * getPara("id"); Competition competition =
			 * Competition.dao.findById(competitionId); jsonObject.put("detail",
			 * competition); } if ("apply".equals(state)) { String competitionId
			 * = getPara("id"); ApplyService applyService = new
			 * ApplyServiceImpl(); Apply apply =
			 * applyService.applyDetail(competitionId, openId);
			 * jsonObject.put("apply", apply); } renderJson(new
			 * JSONSuccess(jsonObject)); } else { renderJson(new
			 * JSONError("拉取用户信息失败！")); }
			 */
		} else {
			redirect("/error?type=oauth");
		}
	}

	/**
	 * PC扫码登陆回调 获取AccessToken以及用户信息跟微信公众号授权用户用户信息一样
	 */
	public void webCallBack() {
		// 用户同意授权，获取code
		String code = getPara("code");
		String state = getPara("state");
		if (code != null) {
			System.out.println("code>" + code + " state>" + state);
			String appId = PropKit.get("webAppId");
			String secret = PropKit.get("webAppSecret");
			// 通过code换取网页授权access_token
			SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(appId, secret, code);
			String json = snsAccessToken.getJson();
			System.out.println("通过code获取access_token>>" + json);
			String token = snsAccessToken.getAccessToken();
			String openId = snsAccessToken.getOpenid();
			// 拉取用户信息(需scope为 snsapi_userinfo)
			ApiResult apiResult = SnsApi.getUserInfo(token, openId);

			log.warn("getUserInfo:" + apiResult.getJson());
			if (apiResult.isSucceed()) {
				JSONObject jsonObject = JSON.parseObject(apiResult.getJson());
				String nickName = jsonObject.getString("nickname");
				// 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
				int sex = jsonObject.getIntValue("sex");
				String city = jsonObject.getString("city");// 城市
				String province = jsonObject.getString("province");// 省份
				String country = jsonObject.getString("country");// 国家
				String headimgurl = jsonObject.getString("headimgurl");
				String unionid = jsonObject.getString("unionid");
			}
			renderText("通过code获取access_token>>" + json + "  \n" + "getUserInfo:" + apiResult.getJson());
		}

	}

}
