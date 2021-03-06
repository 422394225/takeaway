package core.weixin.controller;

import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import core.vo.JSONError;
import core.vo.JSONSuccess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WeixinApiController extends ApiController {
	private Log log = Log.getLog(WeixinApiController.class);

	/**
	 * 如果要支持多公众账号，只需要在此返回各个公众号对应的  ApiConfig 对象即可
	 * 可以通过在请求 url 中挂参数来动态从数据库中获取 ApiConfig 属性值
	 */
	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();

		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));

		/**
		 *  是否对消息进行加密，对应于微信平台的消息加解密方式：
		 *  1：true进行加密且必须配置 encodingAesKey
		 *  2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
		return ac;
	}

	/**
	 * 获取公众号菜单
	 */
	public void getMenu() {
		ApiResult apiResult = MenuApi.getMenu();
		if (apiResult.isSucceed())
			renderJson(new JSONSuccess(apiResult.getJson()));
		else
			renderJson(new JSONError(apiResult.getErrorMsg()));
	}

	/**
	 * 创建菜单
	 */
	public void createMenu() throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer("");
		try {//从配置读取菜单
			InputStreamReader reader = new InputStreamReader(
					getClass().getClassLoader().getResourceAsStream("menu.json"),"UTF-8");
			BufferedReader bufferedReader = new BufferedReader(reader);
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONSuccess("菜单JSON文件读取失败"));
			return;
		}
		String menuStr = sb.toString();
		String domian = PropKit.get("server.address");
		menuStr = menuStr.replace("@DOMAIN_ENCODE@", URLEncoder.encode(domian,"UTF-8"));
		menuStr = menuStr.replace("@DOMAIN@",domian);
		menuStr = menuStr.replace("@APPID@",PropKit.get("appId"));
		log.info(menuStr);
		ApiResult apiResult = MenuApi.createMenu(menuStr);
		if (apiResult.isSucceed()){
			renderJson(new JSONSuccess("菜单生成成功!"));
		}
		else
			renderJson(new JSONError("菜单生成失败!" + sb.toString() + "\n" + apiResult.getErrorMsg()));
	}

	/**
	 * 发送模板消息
	 */
	public void sendMsg() {
		String str = " {\n" + "           \"touser\":\"ohbweuNYB_heu_buiBWZtwgi4xzU\",\n"
				+ "           \"template_id\":\"9SIa8ph1403NEM3qk3z9-go-p4kBMeh-HGepQZVdA7w\",\n"
				+ "           \"url\":\"http://www.sina.com\",\n" + "           \"topcolor\":\"#FF0000\",\n"
				+ "           \"data\":{\n" + "                   \"first\": {\n"
				+ "                       \"value\":\"恭喜你购买成功！\",\n" + "                       \"color\":\"#173177\"\n"
				+ "                   },\n" + "                   \"keyword1\":{\n"
				+ "                       \"value\":\"去哪儿网发的酒店红包（1个）\",\n"
				+ "                       \"color\":\"#173177\"\n" + "                   },\n"
				+ "                   \"keyword2\":{\n" + "                       \"value\":\"1元\",\n"
				+ "                       \"color\":\"#173177\"\n" + "                   },\n"
				+ "                   \"remark\":{\n" + "                       \"value\":\"欢迎再次购买！\",\n"
				+ "                       \"color\":\"#173177\"\n" + "                   }\n" + "           }\n"
				+ "       }";
		ApiResult apiResult = TemplateMsgApi.send(str);
		renderText(apiResult.getJson());
	}

	/**
	 * 获取参数二维码
	 */
	public void getQrcode() {
		String str = "{\"expire_seconds\": 604800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 123}}}";
		ApiResult apiResult = QrcodeApi.create(str);
		renderText(apiResult.getJson());

		//        String str = "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"123\"}}}";
		//        ApiResult apiResult = QrcodeApi.create(str);
		//        renderText(apiResult.getJson());
	}

	/**
	 * 测试输出的结果
	 * create>>{"ticket":"gQFo8DoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL0cwT21FZjNtM3RXbmd3REF6Ml82AAIEzyFQVwMEAAAAAA==","url":"http:\/\/weixin.qq.com\/q\/G0OmEf3m3tWngwDAz2_6"}
	qrcodeUrl:https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQFo8DoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL0cwT21FZjNtM3RXbmd3REF6Ml82AAIEzyFQVwMEAAAAAA==
	 * 
	 * 
	 */
	public void getQrcode2() {
		ApiResult apiResult = QrcodeApi.createPermanent(100);
		String qrcodeUrl = QrcodeApi.getShowQrcodeUrl(apiResult.getStr("ticket"));
		renderText("create>>" + apiResult.getJson() + " qrcodeUrl:" + qrcodeUrl);
	}

	/**
	 * 长链接转成短链接
	 */
	public void getShorturl() {
		String str = "{\"action\":\"long2short\","
				+ "\"long_url\":\"http://wap.koudaitong.com/v2/showcase/goods?alias=128wi9shh&spm=h56083&redirect_count=1\"}";
		ApiResult apiResult = ShorturlApi.getShorturl(str);
		renderText(apiResult.getJson());
	}

	/**
	 * 获取客服聊天记录
	 */
	public void getRecord() {
		String str = "{\n" + "    \"endtime\" : 987654321,\n" + "    \"pageindex\" : 1,\n" + "    \"pagesize\" : 10,\n"
				+ "    \"starttime\" : 123456789\n" + " }";
		ApiResult apiResult = CustomServiceApi.getRecord(str);
		renderText(apiResult.getJson());
	}

	/**
	 * 获取微信服务器IP地址
	 */
	public void getCallbackIp() {
		ApiResult apiResult = CallbackIpApi.getCallbackIp();
		renderText(apiResult.getJson());
	}

}
