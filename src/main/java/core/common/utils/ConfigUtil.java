/**
 * Created At 2017年3月28日上午1:32:16 
 * 
 */

package core.common.utils;

import com.jfinal.kit.PropKit;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年3月28日上午1:32:16
 */

public class ConfigUtil {
	/**
	 * 如果生产环境配置文件存在，则优先加载该配置，否则加载开发环境配置文件
	 * @param pro 生产环境配置文件
	 * @param dev 开发环境配置文件
	 */
	public static void loadProp(String pro, String dev) {
		try {
			PropKit.use(pro);
		} catch (Exception e) {
			PropKit.use(dev);
		}
	}
}
