/**
 * Created At 2017年7月16日下午3:34:13 
 * 
 */

package takeaway;

import com.jfinal.kit.PropKit;

import core.common.utils.ConfigUtil;
import core.utils.MD5Util;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月16日下午3:34:13
 */

public class GetPassword {
	//生成密码

	public static void main(String[] args) {
		ConfigUtil.loadProp("config_pro.properties", "config.properties");
		String ENCRIPT_KEY = PropKit.get("encrypt_key");
		System.out.println(MD5Util.encrypt("admin", ENCRIPT_KEY));
	}
}
