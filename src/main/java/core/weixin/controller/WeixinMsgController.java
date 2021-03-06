package core.weixin.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.CustomServiceApi;
import com.jfinal.weixin.sdk.api.CustomServiceApi.Articles;
import com.jfinal.weixin.sdk.api.TemplateData;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;
import com.jfinal.weixin.sdk.api.UserApi;
import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.msg.in.InImageMsg;
import com.jfinal.weixin.sdk.msg.in.InLinkMsg;
import com.jfinal.weixin.sdk.msg.in.InLocationMsg;
import com.jfinal.weixin.sdk.msg.in.InShortVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.InVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InVoiceMsg;
import com.jfinal.weixin.sdk.msg.in.event.InCustomEvent;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InLocationEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMassEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.in.event.InQrCodeEvent;
import com.jfinal.weixin.sdk.msg.in.event.InTemplateMsgEvent;
import com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.jfinal.weixin.sdk.msg.out.News;
import com.jfinal.weixin.sdk.msg.out.OutCustomMsg;
import com.jfinal.weixin.sdk.msg.out.OutNewsMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import com.jfinal.weixin.sdk.msg.out.OutVoiceMsg;

import core.model.User;
import core.utils.WeiXinUtils;

/**
 * 将此 DemoController 在YourJFinalConfig 中注册路由， 并设置好weixin开发者中心的 URL 与 token ，使
 * URL 指向该 DemoController 继承自父类 WeixinController 的 index
 * 方法即可直接运行看效果，在此基础之上修改相关的方法即可进行实际项目开发
 */
public class WeixinMsgController extends MsgControllerAdapter {
	static Log logger = Log.getLog(WeixinMsgController.class);
	private static final String helpStr = "\t你的品位不错哦  么么哒。";

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

	protected void processInTextMsg(InTextMsg inTextMsg) {
		ApiConfig ac = getApiConfig();
		ApiConfigKit.setThreadLocalApiConfig(ac);
		logger.info("isEncryptMessage:" + ac.isEncryptMessage() + "");
		logger.info("getEncodingAesKey:" + ac.getEncodingAesKey());
		logger.info("getAppId:" + ac.getAppId());
		logger.info("getAppSecret:" + ac.getAppSecret());
		logger.info("getToken:" + ac.getToken());
		String msgContent = inTextMsg.getContent().trim();
		// 帮助提示
		if ("help".equalsIgnoreCase(msgContent) || "帮助".equals(msgContent)) {
			OutTextMsg outMsg = new OutTextMsg(inTextMsg);
			outMsg.setContent(helpStr);
			render(outMsg);
		} else if (msgContent.equals("1") || msgContent.equals("人脸识别")) {
			msgContent = "请发一张清晰的照片！" + WeiXinUtils.emoji(0x1F4F7);
			renderOutTextMsg(msgContent);
		} else if (msgContent.equals("9") || "QQ咨询".equalsIgnoreCase(msgContent)) {
			String url = "http://wpa.qq.com/msgrd?v=3&uin=1472405080&site=qq&menu=yes";
			String urlStr = "<a href=\"" + url + "\">点击咨询</a>";
			renderOutTextMsg("QQ在线咨询" + urlStr);
		} else if (msgContent.equals("微信支付测试")) {
			String url = "http://javen.ngrok.natapp.cn/paytest?openId=o_pncsidC-pRRfCP4zj98h6slREw";
			String urlStr = "<a href=\"" + url + "\">微信支付测试</a>";
			renderOutTextMsg(urlStr);
		} else if ("jssdk".equalsIgnoreCase(msgContent)) {
			String url = PropKit.get("domain") + "/jssdk";
			String urlStr = "<a href=\"" + url + "\">JSSDK</a>";
			renderOutTextMsg("地址" + urlStr);
		} else if ("模板消息".equalsIgnoreCase(msgContent)) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
			String time = sdf.format(new Date());
			String json = TemplateData.New().setTouser(inTextMsg.getFromUserName())
					.setTemplate_id("BzC8RvHu1ICOQfO4N7kp6EWz9VAbISJjV2fO5t7MiXE").setTopcolor("#743A3A")
					.setUrl("http://www.cnblogs.com/zyw-205520/tag/%E5%BE%AE%E4%BF%A1/")
					.add("first", "您好,你已购买课程成功", "#743A3A").add("keyword1", "微信公众号开发公开课", "#0000FF")
					.add("keyword2", "免费", "#0000FF").add("keyword3", "Javen205", "#0000FF")
					.add("keyword4", time, "#0000FF").add("remark", "请点击详情直接看课程直播，祝生活愉快", "#008000").build();
			System.out.println(json);
			ApiResult result = TemplateMsgApi.send(json);

			System.out.println(result.getJson());

			renderNull();
		} else if ("异步回复多个消息".equals(msgContent)) {
			final String toUser = inTextMsg.getFromUserName();
			new Thread(new Runnable() {

				@Override
				public void run() {

					ApiConfigKit.setThreadLocalApiConfig(getApiConfig());

					ApiResult sendText = CustomServiceApi.sendText(toUser, "客服消息");

					System.out.println(sendText.getJson());

					List<Articles> list = new ArrayList<Articles>();

					Articles articles1 = new Articles();
					articles1.setTitle("测试异步回复多个消息");
					articles1.setDescription("客服多图文消息");
					articles1.setPicurl(
							"http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1609/27/c0/27587202_1474952311163_800x600.jpg");
					articles1.setUrl("http://www.cnblogs.com/zyw-205520/tag/%E5%BE%AE%E4%BF%A1/");

					Articles articles2 = new Articles();
					articles2.setTitle("微信买单、刷卡、扫码、公众号支付");
					articles2.setDescription("微信支付教程");
					articles2.setPicurl(
							"http://desk.fd.zol-img.com.cn/t_s960x600c5/g4/M01/0D/04/Cg-4WVP_npmIY6GRAKcKYPPMR3wAAQ8LgNIuTMApwp4015.jpg");
					articles2.setUrl("http://www.jianshu.com/notebooks/2736169/latest");

					list.add(articles2);
					list.add(articles1);

					CustomServiceApi.sendNews(toUser, list);

				}
			}).start();

			// 回复被动响应消息
			renderOutTextMsg("你发的内容为：" + msgContent);

		}

		else {
			renderOutTextMsg("你发的内容为：" + msgContent);
			// 转发给多客服PC客户端
			// OutCustomMsg outCustomMsg = new OutCustomMsg(inTextMsg);
			// render(outCustomMsg);
		}

	}

	@Override
	protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
		// 转发给多客服PC客户端
		// OutCustomMsg outCustomMsg = new OutCustomMsg(inVoiceMsg);
		// render(outCustomMsg);
		OutVoiceMsg outMsg = new OutVoiceMsg(inVoiceMsg);
		// 将刚发过来的语音再发回去
		outMsg.setMediaId(inVoiceMsg.getMediaId());
		render(outMsg);
	}

	@Override
	protected void processInVideoMsg(InVideoMsg inVideoMsg) {
		/*
		 * 腾讯 api 有 bug，无法回复视频消息，暂时回复文本消息代码测试 OutVideoMsg outMsg = new
		 * OutVideoMsg(inVideoMsg); outMsg.setTitle("OutVideoMsg 发送");
		 * outMsg.setDescription("刚刚发来的视频再发回去"); // 将刚发过来的视频再发回去，经测试证明是腾讯官方的 api
		 * 有 bug，待 api bug 却除后再试 outMsg.setMediaId(inVideoMsg.getMediaId());
		 * render(outMsg);
		 */
		OutTextMsg outMsg = new OutTextMsg(inVideoMsg);
		outMsg.setContent("\t视频消息已成功接收，该视频的 mediaId 为: " + inVideoMsg.getMediaId());
		render(outMsg);
	}

	@Override
	protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
		OutTextMsg outMsg = new OutTextMsg(inShortVideoMsg);
		outMsg.setContent("\t视频消息已成功接收，该视频的 mediaId 为: " + inShortVideoMsg.getMediaId());
		render(outMsg);
	}

	@Override
	protected void processInLocationMsg(InLocationMsg inLocationMsg) {
		OutTextMsg outMsg = new OutTextMsg(inLocationMsg);
		outMsg.setContent("已收到地理位置消息:" + "\nlocation_X = " + inLocationMsg.getLocation_X() + "\nlocation_Y = "
				+ inLocationMsg.getLocation_Y() + "\nscale = " + inLocationMsg.getScale() + "\nlabel = "
				+ inLocationMsg.getLabel());
		render(outMsg);
	}

	@Override
	protected void processInLinkMsg(InLinkMsg inLinkMsg) {
		// 转发给多客服PC客户端
		OutCustomMsg outCustomMsg = new OutCustomMsg(inLinkMsg);
		render(outCustomMsg);
	}

	@Override
	protected void processInCustomEvent(InCustomEvent inCustomEvent) {
		logger.debug("测试方法：processInCustomEvent()");
		renderNull();
	}

	protected void processInImageMsg(InImageMsg inImageMsg) {
		renderNull();
	}

	/**
	 * 实现父类抽方法，处理关注/取消关注消息
	 */
	@Override
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {
		String openid = inFollowEvent.getFromUserName();
		ApiResult apiResult = UserApi.getUserInfo(inFollowEvent.getFromUserName());
		User user = User.dao.findById(openid);
		User newUser = new User();
		newUser.set("ID", openid);
		newUser.set("SUBSCRIBE", apiResult.get("subscribe"));
		if (InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEvent.getEvent())) {
			newUser.set("NICKNAME", apiResult.get("nickname"));
			newUser.set("AVATAR", apiResult.get("headimgurl"));
			newUser.set("SEX", apiResult.get("sex"));
			newUser.set("SUBSCRIBE_TIME", new Date(apiResult.getLong("subscribe_time") * 1000));
			newUser.set("GROUPID", apiResult.get("groupid"));
			newUser.set("UNIONID", apiResult.get("unionid"));
			String welcome = "";
			if (user == null) {
				newUser.save();
				welcome = "感谢您的关注！";
			} else {
				newUser.update();
				welcome = "欢迎回来！";
			}
			OutNewsMsg outNewsMsg = new OutNewsMsg(inFollowEvent);
			List<News> articles = new ArrayList<News>();
			String frontServer = PropKit.get("server.front.address", "http://www.baidu.com");
			articles.add(new News(welcome, "欢迎语", "http://onzsmr037.bkt.clouddn.com/579f016301086.png", ""));
			articles.add(new News("更多", "", null, frontServer));
			outNewsMsg.setArticles(articles);
			logger.debug("关注：" + openid);
			render(outNewsMsg);
		}
		// 如果为取消关注事件，将无法接收到传回的信息
		if (InFollowEvent.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEvent.getEvent())) {
			newUser.update();
			logger.debug("取消关注：" + inFollowEvent.getFromUserName());
		}
	}

	@Override
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
		System.out.println("扫码.......");
		if (InQrCodeEvent.EVENT_INQRCODE_SUBSCRIBE.equals(inQrCodeEvent.getEvent())) {
			logger.debug("扫码未关注：" + inQrCodeEvent.getFromUserName());
			OutTextMsg outMsg = new OutTextMsg(inQrCodeEvent);
			outMsg.setContent("感谢您的关注，二维码内容：" + inQrCodeEvent.getEventKey());
			render(outMsg);
		}
		if (InQrCodeEvent.EVENT_INQRCODE_SCAN.equals(inQrCodeEvent.getEvent())) {
			logger.debug("扫码已关注：" + inQrCodeEvent.getFromUserName());
			renderOutTextMsg("扫码已关注,二维码内容：" + inQrCodeEvent.getEventKey());
		}

	}

	@Override
	protected void processInLocationEvent(InLocationEvent inLocationEvent) {
		logger.debug("发送地理位置事件：" + inLocationEvent.getFromUserName());
		OutTextMsg outMsg = new OutTextMsg(inLocationEvent);
		outMsg.setContent("地理位置是：" + inLocationEvent.getLatitude());
		render(outMsg);
	}

	@Override
	protected void processInMassEvent(InMassEvent inMassEvent) {
		logger.debug("测试方法：processInMassEvent()");
		renderNull();
	}

	/**
	 * 实现父类抽方法，处理自定义菜单事件
	 */
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		logger.debug("菜单事件：" + inMenuEvent.getFromUserName());
		OutTextMsg outMsg = new OutTextMsg(inMenuEvent);
		outMsg.setContent("菜单事件内容是：" + inMenuEvent.getEventKey());
		render(outMsg);
	}

	@Override
	protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults) {
		logger.debug("语音识别事件：" + inSpeechRecognitionResults.getFromUserName());
		OutTextMsg outMsg = new OutTextMsg(inSpeechRecognitionResults);
		outMsg.setContent("语音识别内容是：" + inSpeechRecognitionResults.getRecognition());
		render(outMsg);
	}

	@Override
	protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {
		logger.debug("测试方法：processInTemplateMsgEvent()");
		renderNull();
	}

}
