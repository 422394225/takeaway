package core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.http.client.ClientProtocolException;

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

	/**
	 * 上传微信视频专用
	 * 
	 * @param url
	 * @param filePath
	 * @param title
	 * @param introduction
	 * @return
	 */
	public static String postVedio1(String filePath) {
		String fileExt = filePath.substring(filePath.lastIndexOf('.') + 1);
		File file = new File(filePath);
		if (!file.exists())
			return null;
		String result = null;
		try {
			URL url1 = new URL("https://api.weixin.qq.com/cgi-bin/media/upload?access_token="
					+ AccessTokenApi.getAccessTokenStr() + "&type=voice");
			HttpsURLConnection conn = (HttpsURLConnection) url1.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			conn.setRequestProperty("Cache-Control", "max-age=0");
			String boundary = "-----------------------------" + System.currentTimeMillis();
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			OutputStream output = conn.getOutputStream();
			output.write(("--" + boundary + "\r\n").getBytes());
			// output.write(String.format("Content-Disposition: form-data;
			// name=\"media\"; filename=\"%s\"\r\n",file.getName()).getBytes());
			String des = String
					.format("Content-Disposition:form-data;name=\"media\";filelength=\"{1}\";filename=\"{0}\"\r\nContent-Type:"
							+ VOICE_CONTENT_TYPE.get(fileExt) + "\r\n\r\n", file.getName(), file.length());
			System.out.println(des);
			output.write(des.getBytes());
			// output.write(("Content-Type: " + VOICE_CONTENT_TYPE.get(fileExt)
			// + " \r\n\r\n").getBytes());
			byte[] data = new byte[1024];
			int len = 0;
			FileInputStream input = new FileInputStream(file);
			while ((len = input.read(data)) > -1) {
				output.write(data, 0, len);
			}
			output.write(("--" + boundary + "\r\n").getBytes());
			// output.write("Content-Disposition: form-data;
			// name=\"description\";\r\n\r\n".getBytes());
			// output.write(String.format("{\"title\":\"%s\",\"introduction\":\"%s\"}",
			// "123", "123").getBytes());
			// output.write(("\r\n--" + boundary + "--\r\n\r\n").getBytes());
			output.flush();
			output.close();
			input.close();
			InputStream resp = conn.getInputStream();
			StringBuffer sb = new StringBuffer();
			while ((len = resp.read(data)) > -1)
				sb.append(new String(data, 0, len, "utf-8"));
			resp.close();
			result = sb.toString();
			System.out.println(result);
		} catch (ClientProtocolException e) {
			System.out.println("postFile，不支持http协议");
		} catch (IOException e) {
			System.out.println("postFile数据传输失败");
		}
		System.out.println("result=" + result);
		return result;
	}

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
