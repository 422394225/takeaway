package core.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;

public class BaiduSpeechUtil {
	// 设置APPID/AK/SK
	private static final String APP_ID = "10079061";
	private static final String API_KEY = "oZaOp5ASmewZe9UdTQixdZ28";
	private static final String SECRET_KEY = "1NlVWRpX1cXF9VsOEHZu8TMGTsWSQ10Q";
	private static final String CUID = "takeaway";
	private static final AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
	static {
		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);
	}

	public static void main(String[] args) {
		synthesisToFile(client, "你有新订单啦！", "order-2.mp3");
	}

	/**
	 * 
	 * tex String 合成的文本，使用UTF-8编码，请注意文本长度必须小于1024字节 是<br>
	 * lang String 语言选择,填写zh 是<br>
	 * ctp String 客户端类型选择，web端填写1 是<br>
	 * cuid String 用户唯一标识，用来区分用户，填写机器 MAC 地址或 IMEI 码，长度为60以内 否<br>
	 * spd String 语速，取值0-9，默认为5中语速 否<br>
	 * pit String 音调，取值0-9，默认为5中语调 否<br>
	 * vol String 语速，取值0-15，默认为5中语速 否<br>
	 * per String 发音人选择, 0为女声，1为男声，3为情感合成-度逍遥，4为情感合成-度丫丫，默认为普通女 否<br>
	 */
	public static void synthesisToFile(AipSpeech client, String content, String fileName) {
		// 设置可选参数
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put("spd", "5");
		options.put("pit", "5");
		options.put("per", "0");
		options.put("cuid", CUID);
		TtsResponse res1 = client.synthesis(content, "zh", 1, options);
		System.out.println(res1.getErrorCode());
		byte[] data = res1.getData();
		try {
			File dir = new File("voice");
			if (!dir.exists())
				dir.mkdirs();
			File voiceFile = new File("voice\\" + fileName);
			if (voiceFile.exists())
				new File("voice\\" + fileName).delete();
			new File("voice\\" + fileName).createNewFile();
			FileUtils.writeByteArrayToFile(new File("voice\\" + fileName), data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * tex String 合成的文本，使用UTF-8编码，请注意文本长度必须小于1024字节 是<br>
	 * lang String 语言选择,填写zh 是<br>
	 * ctp String 客户端类型选择，web端填写1 是<br>
	 * cuid String 用户唯一标识，用来区分用户，填写机器 MAC 地址或 IMEI 码，长度为60以内 否<br>
	 * spd String 语速，取值0-9，默认为5中语速 否<br>
	 * pit String 音调，取值0-9，默认为5中语调 否<br>
	 * vol String 语速，取值0-15，默认为5中语速 否<br>
	 * per String 发音人选择, 0为女声，1为男声，3为情感合成-度逍遥，4为情感合成-度丫丫，默认为普通女 否<br>
	 */
	public static byte[] synthesis(AipSpeech client, String content) {
		// 设置可选参数
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put("spd", "5");
		options.put("pit", "5");
		options.put("per", "0");
		options.put("cuid", CUID);
		TtsResponse res1 = client.synthesis(content, "zh", 1, options);
		System.out.println(res1.getErrorCode());
		byte[] data = res1.getData();
		return data;
	}
}
