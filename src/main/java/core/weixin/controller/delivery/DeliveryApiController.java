/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.delivery;

import java.io.File;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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

public class DeliveryApiController extends WeixinMsgController {
	private Log log = Log.getLog(DeliveryApiController.class);
	private final static String ENCRIPT_KEY = PropKit.get("encrypt_key");

	public void login() {
		String from = getPara("from");
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
		if ("wx".equals(from)) {
			String uuid = UUID.randomUUID().toString();
			log.info(deliveryId + " accessKey " + uuid);
			DeliveryController.addLoginAccessKey(uuid, deliveryId);
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					DeliveryController.removeLoginAccessKey(uuid);
				}
			}, 1000 * 60 * 10);
			renderJson(new JSONSuccess(uuid));
		} else
			renderJson(new JSONSuccess());
	}

	public void download() {
		URL url = getClass().getClassLoader().getResource("nali.apk");
		System.out.println(url.getPath());
		// BufferedReader bufferedReader = new BufferedReader(reader);
		// String fpath = System.getProperty("user.dir");
		// System.out.println(fpath);
		// System.out.println(fpath);
		File file = new File(url.getPath());
		if (file.exists()) {
			renderFile(file);
		} else {
			renderText("文件不存在" + url.getPath());
		}
	}

	public void recievePosition() {
		String id = getPara("id");
		String longtitude = getPara("longtitude");
		String latidute = getPara("latidude");
		Db.update("UPDATE T_DELIVERY SET NOW_LONGITUDE=?,NOW_LATITUDE=? WHERE ID=?", longtitude, latidute, id);
		renderJson(new JSONSuccess());
	}

}
