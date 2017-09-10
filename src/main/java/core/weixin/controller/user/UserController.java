/**
 * Created At 2017年5月19日上午5:52:47 
 * 
 */

package core.weixin.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import core.model.User;
import core.model.UserAddress;
import core.vo.JSONError;
import core.vo.JSONSuccess;

import java.util.List;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年5月19日上午5:52:47
 */

public class UserController extends Controller {

	public void detail() {
		try {
			String openId = getPara("openId");
			User user = User.dao.findById(openId);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("user", user);
			renderJson(new JSONSuccess(jsonObject));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONError("获取列表失败！"));
		}
	}

	public void ajaxUserAdress(){
		String openId = getPara("openId");
		List<UserAddress> userAddresses = UserAddress.dao.find(Db.getSql("userAddress.getByUid"),openId);
		renderJson(new JSONSuccess(userAddresses));
	}

}
