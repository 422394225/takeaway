package core.utils;

import java.io.File;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfinal.kit.Kv;
import com.jfinal.weixin.sdk.api.AccessTokenApi;

public class FileUploadUtil {
	public static Map<String, String> VOICE_CONTENT_TYPE = Kv.by("acp", "audio/x-mei-aac").set("aif", "audio/aiff")
			.set("aiff", "audio/aiff").set("aifc", "audio/aiff").set("au", "audio/basic")
			.set("la1", "audio/x-liquid-file").set("lavs", "audio/x-liquid-secure").set("lmsff", "audio/x-la-lms")
			.set("m3u", "audio/mpegurl").set("midi", "audio/mid").set("mid", "audio/mid").set("mp2", "audio/mp2")
			.set("mp3", "audio/mp3").set("mp4", "audio/mp4").set("mnd", "audio/x-musicnet-download")
			.set("mp1", "audio/mp1").set("mns", "audio/x-musicnet-stream").set("mpga", "audio/rn-mpeg")
			.set("pls", "audio/scpls").set("ram", "audio/x-pn-realaudio").set("rmi", "audio/mid")
			.set("rmm", "audio/x-pn-realaudio").set("snd", "audio/basic").set("wav", "audio/wav")
			.set("wax", "audio/x-ms-wax").set("wma", "audio/x-ms-wma");

	public static String postVedio(String filePath) {
		String fileExt = filePath.substring(filePath.lastIndexOf('.') + 1);
		String uploadurl = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token="
				+ AccessTokenApi.getAccessTokenStr() + "&type=voice";
		org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
		PostMethod post = new PostMethod(uploadurl);
		post.setRequestHeader("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:30.0) Gecko/20100101 Firefox/30.0");
		post.setRequestHeader("Host", "file.api.weixin.qq.com");
		post.setRequestHeader("Connection", "Keep-Alive");
		post.setRequestHeader("Cache-Control", "no-cache");
		String result = null;
		File file = new File(filePath);
		try {
			if (file != null && file.exists()) {
				FilePart filepart = new FilePart("media", file, VOICE_CONTENT_TYPE.get(fileExt), "UTF-8");
				Part[] parts = new Part[] { filepart };
				MultipartRequestEntity entity = new MultipartRequestEntity(parts,

						post.getParams());
				post.setRequestEntity(entity);
				int status = client.executeMethod(post);
				if (status == HttpStatus.SC_OK) {
					String responseContent = post.getResponseBodyAsString();
					System.out.println(responseContent);
					JsonParser jsonparer = new JsonParser();// 初始化解析json格式的对象
					JsonObject json = jsonparer.parse(responseContent).getAsJsonObject();
					if (json.get("errcode") == null)// {"errcode":40004,"errmsg":"invalid
													// media type"}
					{ // 上传成功
						// {"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
						result = json.get("media_id").getAsString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}
	}
}
