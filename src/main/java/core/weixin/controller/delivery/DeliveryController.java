/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.delivery;

import java.io.File;

import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;

import core.utils.MD5Util;
import core.vo.JSONError;
import core.vo.JSONSuccess;
import core.weixin.controller.WeixinMsgController;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月24日下午10:03:40
 */

public class DeliveryController extends WeixinMsgController {
	private Log log = Log.getLog(DeliveryController.class);
	private final static String ENCRIPT_KEY = PropKit.get("encrypt_key");

	public void login() {
		String username = getPara("username");
		String password = getPara("password");
		String passwordMD5 = MD5Util.encrypt(password + ENCRIPT_KEY);
		Integer deliveryId = Db.queryInt("SELECT ID FROM T_DELIVERY WHERE USERNAME=? AND PASSWORD=?", username,
				passwordMD5);
		if (deliveryId == null) {
			deliveryId = Db.queryInt("SELECT ID FROM T_DELIVERY WHERE USERNAME=? ", username);
			if (deliveryId == null) {
				renderJson(new JSONError("查无此人"));
				return;
			} else {
				renderJson(new JSONError("密码不对啦"));
				return;
			}
		}
		renderJson(new JSONSuccess(deliveryId));
	}

	public void download() {
		String fpath = "E:/git/takeaway/src/main/resources";
		System.out.println(fpath);
		File file = new File(fpath + "/nali.apk");

		if (file.exists()) {
			renderFile(file);
		} else {
			renderJson();
		}
	}

	public void recievePosition() {
		String id = getPara("id");
		String longtitude = getPara("longtitude");
		String latidute = getPara("latidude");
		Db.update("UPDATE T_DELIVERY SET NOW_LONGITUDE=?,NOW_LATIDUTE=? WHERE ID=?", longtitude, latidute, id);
		renderJson(new JSONSuccess());
	}

}
