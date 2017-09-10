package core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.TemplateData;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;
import com.jfinal.weixin.sdk.utils.HttpUtils;

import core.model.Order;
import core.vo.RecevieMedia;

public class WeiXinUtils {
	private static final String voicePath = PropKit.get("voicePath","c:\\\\tempVoice\\\\");
	public static Map<String, String> VOICE_CONTENT_TYPE = Kv.by("acp", "audio/x-mei-aac").set("aif", "audio/aiff")
			.set("aiff", "audio/aiff").set("aifc", "audio/aiff").set("au", "audio/basic")
			.set("la1", "audio/x-liquid-file").set("lavs", "audio/x-liquid-secure").set("lmsff", "audio/x-la-lms")
			.set("m3u", "audio/mpegurl").set("midi", "audio/mid").set("mid", "audio/mid").set("mp2", "audio/mp2")
			.set("mp3", "audio/mp3").set("mp4", "audio/mp4").set("mnd", "audio/x-musicnet-download")
			.set("mp1", "audio/mp1").set("mns", "audio/x-musicnet-stream").set("mpga", "audio/rn-mpeg")
			.set("pls", "audio/scpls").set("ram", "audio/x-pn-realaudio").set("rmi", "audio/mid")
			.set("rmm", "audio/x-pn-realaudio").set("snd", "audio/basic").set("wav", "audio/wav")
			.set("wax", "audio/x-ms-wax").set("wma", "audio/x-ms-wma");
	static {
		File file = new File(voicePath);
		// System.out.println(voicePath);
		if (!file.exists())
			file.mkdirs();
	}

	public static String filterWeixinEmoji(String source) {
		if (containsEmoji(source)) {
			source = filterEmoji(source);
		}
		return source;
	}

	/**
	 * 检测是否有emoji字符
	 * 
	 * @param source
	 * @return 一旦含有就抛出
	 */
	public static boolean containsEmoji(String source) {
		if (StrKit.isBlank(source)) {
			return false;
		}

		int len = source.length();

		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);

			if (isEmojiCharacter(codePoint)) {
				// do nothing，判断到了这里表明，确认有表情字符
				return true;
			}
		}

		return false;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
				|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}

	/**
	 * 过滤emoji 或者 其他非文字类型的字符
	 * 
	 * @param source
	 * @return
	 */
	public static String filterEmoji(String source) {

		if (!containsEmoji(source)) {
			return source;// 如果不包含，直接返回
		}
		// 到这里铁定包含
		StringBuilder buf = null;

		int len = source.length();

		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);

			if (isEmojiCharacter(codePoint)) {
				if (buf == null) {
					buf = new StringBuilder(source.length());
				}

				buf.append(codePoint);
			} else {
			}
		}

		if (buf == null) {
			return source;// 如果没有找到 emoji表情，则返回源字符串
		} else {
			if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
				buf = null;
				return source;
			} else {
				return buf.toString();
			}
		}
	}

	/**
	 * emoji表情转换(hex -> utf-16)
	 * 
	 * @param hexEmoji
	 * @return
	 */
	public static String emoji(int hexEmoji) {
		return String.valueOf(Character.toChars(hexEmoji));
	}

	/**
	 * 发送模板消息
	 * 
	 * @param orderId
	 * @param price
	 * @param couresName
	 * @param teacherName
	 * @param openId
	 * @param url
	 * @return
	 */
	public static ApiResult sendTemplateMessage_2(String orderId, String price, String couresName, String teacherName,
			String openId, String url) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
		String time = sdf.format(new Date());
		ApiResult result = TemplateMsgApi
				.send(TemplateData.New().setTemplate_id("7y1wUbeiYFsUONKH1IppVi47WwViICAjREZSdR3Zahc")
						.setTopcolor("#743A3A").setTouser(openId).setUrl(url).add("first", "您好,你已成功购买课程", "#000000")
						.add("keyword1", orderId, "#FF0000").add("keyword2", price + "元", "#c4c400")
						.add("keyword3", couresName, "#c4c400").add("keyword4", teacherName, "#c4c400")
						.add("keyword5", time, "#0000FF").add("remark", "\n 请点击详情直接看课程直播，祝生活愉快", "#008000").build());

		return result;
	}

	public static void sendMessage(String openId, String mess) {
		// {
		// "touser":"OPENID",
		// "msgtype":"text",
		// "text":
		// {
		// "content":"Hello World"
		// }
		// }
		JSONObject object = new JSONObject();
		object.put("touser", openId);
		object.put("msgtype", "text");
		JSONObject contentObject = new JSONObject();
		contentObject.put("content", mess);
		object.put("text", contentObject);
		// System.out.println(object.toString());
		HttpUtils.post("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="
				+ AccessTokenApi.getAccessTokenStr(), object.toString());
	}

	public static void sendVoice(String openId, String MEDIA_ID) {
		// {
		// "touser":"OPENID",
		// "msgtype":"voice",
		// "voice":
		// {
		// "media_id":"MEDIA_ID"
		// }
		// }
		JSONObject object = new JSONObject();
		object.put("touser", openId);
		object.put("msgtype", "voice");
		JSONObject voiceObject = new JSONObject();
		voiceObject.put("media_id", MEDIA_ID);
		object.put("voice", voiceObject);
		HttpUtils.post("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="
				+ AccessTokenApi.getAccessTokenStr(), object.toString());

	}

	public static RecevieMedia uploadVoice(String accessToken, byte[] sourceBytes, String fileExt) {
		RecevieMedia recevieMedia = null;
		// 拼装请求地址
		String uploadMediaUrl = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
		uploadMediaUrl = uploadMediaUrl.replace("ACCESS_TOKEN", accessToken).replace("TYPE", "voice");
		// 定义数据分隔符
		String boundary = "------------7da2e536604c8";
		try {
			URL uploadUrl = new URL(uploadMediaUrl);
			java.net.HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
			uploadConn.setDoOutput(true);
			uploadConn.setDoInput(true);
			uploadConn.setRequestMethod("POST");
			// 设置请求头Content-Type
			uploadConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			// 获取媒体文件上传的输出流（往微信服务器写数据）
			OutputStream outputStream = uploadConn.getOutputStream();

			// 从请求头中获取内容类型
			String contentType = VOICE_CONTENT_TYPE.get(fileExt.toLowerCase());
			// 请求体开始
			outputStream.write(("--" + boundary + "\r\n").getBytes());
			outputStream.write(
					String.format("Content-Disposition: form-data; name=\"media\"; filename=\"file1%s\"\r\n", fileExt)
							.getBytes());
			outputStream.write(String.format("Content-Type: %s\r\n\r\n", contentType).getBytes());

			// 获取媒体文件的输入流（读取文件）
			BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(sourceBytes));
			byte[] buf = new byte[8096];
			int size = 0;
			while ((size = bis.read(buf)) != -1) {
				// 将媒体文件写到输出流（往微信服务器写数据）
				outputStream.write(buf, 0, size);
			}
			// 请求体结束
			outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
			outputStream.close();
			bis.close();

			// 获取媒体文件上传的输入流（从微信服务器读数据）
			InputStream inputStream = uploadConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuffer buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			uploadConn.disconnect();

			// 使用JSON-lib解析返回结果
			JSONObject jsonObject = JSONObject.parseObject(buffer.toString());
			recevieMedia = new RecevieMedia();
			recevieMedia.setErrcode(jsonObject.getString("errcode"));
			recevieMedia.setErrmsg(jsonObject.getString("errmsg"));
			recevieMedia.setType(jsonObject.getString("type"));
			// type等于thumb时的返回结果和其它类型不一样
			if ("thumb".equals("voice")) {
				recevieMedia.setMedia_id(jsonObject.getString("thumb_media_id"));
			} else {
				recevieMedia.setMedia_id(jsonObject.getString("media_id"));
			}
			recevieMedia.setCreated_at(jsonObject.getString("created_at"));
		} catch (Exception e) {
			e.printStackTrace();
			recevieMedia = null;
		}
		return recevieMedia;
	}

	/**
	 * 发送订单语音
	 * 
	 * @param hjk
	 */
	public static void sendOrderVideoAndMess(Order order) {
		StringBuilder voiceString = new StringBuilder();
		int shopId = order.getInt("SHOP_ID");
		Record shop = Db.findFirst("SELECT `NAME`,OPENID FROM T_SHOP WHERE ID=?", shopId);
		if (shop.get("OPENID") == null)
			return;
		voiceString.append(shop.getStr("NAME"));
		voiceString.append("。");
		JSONArray foodArray = JSONArray.parseArray(order.getStr("FOODS"));
		for (Object tempObject : foodArray) {
			JSONObject foodObject = (JSONObject) tempObject;
			Record food = Db.findFirst("SELECT `NAME`,UNIT FROM T_FOOD WHERE ID=?", foodObject.get("id"));
			voiceString.append(food.getStr("NAME"));
			voiceString.append(foodObject.get("num"));
			voiceString.append(food.getStr("UNIT"));
			voiceString.append(",");
		}
		voiceString.append("以上就是全部商品。");
		String fileName = order.get("ID").toString() + ".mp3";
		BaiduSpeechUtil.synthesisToFile(voiceString.toString(), voicePath + fileName);
		String mediaId = FileUploadUtil.postVedio(voicePath + fileName);
		// System.out.println(recevieMedia.getMedia_id());
		WeiXinUtils.sendMessage(shop.getStr("OPENID"), voiceString.toString());
		WeiXinUtils.sendVoice(shop.getStr("OPENID"), mediaId);
	}

	public static ApiResult sendTemplateMessageByOpen(String orderId, String price, String couresName,
			String teacherName, String openId, String url) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
		String time = sdf.format(new Date());
		ApiResult result = TemplateMsgApi
				.send(TemplateData.New().setTemplate_id("7y1wUbeiYFsUONKH1IppVi47WwViICAjREZSdR3Zahc")
						.setTopcolor("#743A3A").setTouser(openId).setUrl(url).add("first", "您好,你已成功购买课程", "#000000")
						.add("keyword1", orderId, "#FF0000").add("keyword2", price + "元", "#c4c400")
						.add("keyword3", couresName, "#c4c400").add("keyword4", teacherName, "#c4c400")
						.add("keyword5", time + "\n我们的专业客服人员会在24小时内与您联系，请注意接听我们的电话，再次感谢您的支持！", "#000000")
						.add("remark", "\n 请点击详情直接看课程直播，祝生活愉快", "#008000").build());

		return result;
	}

	public static byte[] callMapToXML(Map map) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		mapToXML(map, sb);
		sb.append("</xml>");
		// System.out.println(sb.toString());
		try {
			return sb.toString().getBytes("UTF-8");
		} catch (Exception e) {
		}
		return null;
	}

	private static void mapToXML(Map map, StringBuffer sb) {
		Set set = map.keySet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = map.get(key);
			if (null == value)
				value = "";
			if (value.getClass().getName().equals("java.util.ArrayList")) {
				ArrayList list = (ArrayList) map.get(key);
				sb.append("<" + key + ">");
				for (int i = 0; i < list.size(); i++) {
					HashMap hm = (HashMap) list.get(i);
					mapToXML(hm, sb);
				}
				sb.append("</" + key + ">");

			} else {
				if (value instanceof HashMap) {
					sb.append("<" + key + ">");
					mapToXML((HashMap) value, sb);
					sb.append("</" + key + ">");
				} else {
					sb.append("<" + key + ">" + value + "</" + key + ">");
				}

			}

		}
	}

	public static String getSign(Map<String, String> keyMap) {
		List<Entry<String, String>> list = new ArrayList<Entry<String, String>>(keyMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
				return (o1.getKey().compareTo(o2.getKey()));
			}
		});
		StringBuilder paraBuilder = new StringBuilder();
		for (Entry<String, String> entry : list) {
			paraBuilder.append(entry.getKey());
			paraBuilder.append("=");
			paraBuilder.append(entry.getValue());
			paraBuilder.append("&");
		}
		paraBuilder.append("key");
		paraBuilder.append("=");
		paraBuilder.append(PropKit.get("mch_key"));
		System.out.println(paraBuilder.toString());
		return MD5Util.encrypt(paraBuilder.toString()).toUpperCase();
	}

	public static String getNonceNorString() {
		StringBuilder nonceNorBuilder = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 16; i++)
			nonceNorBuilder.append((char) (random.nextInt(26) + 'A'));
		return nonceNorBuilder.toString();
	}

	public static ApiResult sendTemplateMessageByPrivate(String orderId, String price, String couresName,
			String teacherName, String openId, String url) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
		String time = sdf.format(new Date());
		ApiResult result = TemplateMsgApi.send(TemplateData.New()
				.setTemplate_id("7y1wUbeiYFsUONKH1IppVi47WwViICAjREZSdR3Zahc").setTopcolor("#743A3A").setTouser(openId)
				.setUrl(url).add("first", "您好,你已成功购买课程", "#000000").add("keyword1", orderId, "#FF0000")
				.add("keyword2", price + "元", "#c4c400").add("keyword3", couresName, "#c4c400")
				.add("keyword4", teacherName, "#c4c400").add("keyword5", time, "#000000")
				.add("remark", "\n我们的专业客服人员会在24小时内与您联系，请注意接听我们的电话，再次感谢您的支持！", "#008000").build());

		return result;
	}

}