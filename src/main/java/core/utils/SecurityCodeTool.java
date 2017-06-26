/**
 * Created At 2017年2月16日下午10:46:57 
 * 
 */

package core.utils;

import com.jfinal.kit.PropKit;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年2月16日下午10:46:57
 */

public class SecurityCodeTool {
	private static String key = PropKit.get("encrypt_key");
	private static int time = 3;
	public static DESUtils desUtils = new DESUtils(key);

	public static String encrypt(String orign) {
		String code = orign;
		for (int i = 0; i < time; i++) {
			code = desUtils.encryptString(code);
		}
		return code;
	}

	public static String decrypt(String code) {
		String orign = code;
		for (int i = 0; i < time; i++) {
			orign = desUtils.decryptString(orign);
		}
		return orign;
	}

}
