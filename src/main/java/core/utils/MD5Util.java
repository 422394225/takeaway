package core.utils;

import java.security.MessageDigest;

/**
 * 
 * Description: MD5信息摘要
 * 
 * @author chenyh
 * @date 2015年8月4日
 * @version 1.0
 *
 */
public class MD5Util {

	public final static String encrypt(String... strs) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuilder sBuilder = new StringBuilder();
		for (String s : strs) {
			sBuilder.append(s);
		}
		try {
			byte[] btInput = sBuilder.toString().getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
