/**
 * Created At 2017年5月19日上午5:52:47 
 * 
 */

package core.weixin.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import core.model.User;
import core.model.UserAddress;
import core.validate.AddressValidate;
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

	public void ajaxUserAddress(){
		String openId = getPara("openId");
		List<UserAddress> userAddresses = UserAddress.dao.find(Db.getSql("userAddress.getByUid"),openId);
		renderJson(new JSONSuccess(userAddresses));
	}

	public void addressForm(){
		String id = getPara("id");
		UserAddress userAddress = UserAddress.dao.findById(id);
		setAttr("userAddress",userAddress);
		render("addressForm.html");
	}

	public void addressList(){
		render("address.html");
	}

	@Before(AddressValidate.class)
	public void saveAddress(){
		UserAddress userAddress = new UserAddress();
		UserAddress defaultAddress = UserAddress.dao.findFirst(Db.getSql("userAddress.getDefault"),getPara("openId"));
		if(defaultAddress==null){
			userAddress.set("DEFAULT",1);
		}
		userAddress.set("USERID",getPara("openId"));
		String addressCode = getPara("addressCode");
		userAddress.set("PCODE",addressCode.substring(0, 2) + "0000");
		userAddress.set("PCCODE",addressCode.substring(0, 4) + "00");
		userAddress.set("PTCODE", addressCode);
		userAddress.set("ADDRESS",getPara("address"));
		userAddress.set("LONGITUDE",getPara("longitude"));
		userAddress.set("LATITUDE",getPara("latitude"));
		userAddress.set("NAME",getPara("realName"));
		userAddress.set("TEL",getPara("phone"));
		userAddress.set("DELETED",0);
		userAddress.set("SEX",getParaToInt("sex"));
		userAddress.save();
		renderJson(new JSONSuccess("添加成功"));
	}

	public void setDefault(){
		String id = getPara("id");
		UserAddress userAddress = UserAddress.dao.findById(id);
		userAddress.set("DEFAULT",1);
		UserAddress defaultAddress = UserAddress.dao.findFirst(Db.getSql("userAddress.getDefault"),userAddress.getStr("USERID"));
		defaultAddress.set("DEFAULT",0);
		defaultAddress.update();
		userAddress.update();
		renderJson(new JSONSuccess("设为默认成功"));
	}

}
