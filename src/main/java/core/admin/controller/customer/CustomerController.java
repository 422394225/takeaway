package core.admin.controller.customer;

/**
 * Created At 2017年1月30日下午2:29:33 
 * 
 */

import com.alibaba.fastjson.JSONObject;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.CustomServiceApi;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import core.admin.service.customer.CustomerService;
import core.admin.service.customer.impl.CustomerServiceImpl;
import core.common.utils.QiniuUtils;
import core.model.Customer;
import core.validate.CustomerValidate;
import core.vo.DTParams;
import core.vo.JSONError;
import core.vo.JSONSuccess;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:29:33
 */

public class CustomerController extends Controller {
	private Log log = Log.getLog(CustomerController.class);
	private CustomerService customerService = new CustomerServiceImpl();

	public void index() {
		render("list.html");
	}

	public void getData() {
		DTParams params = new DTParams(getParaMap());
		Page<Record> customers = customerService.getDTPage(params, "customer.list");
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", customers.getTotalRow());
		List<Record> list = customers.getList();
		result.put("data", list);
		result.put("recordsFiltered", customers.getTotalRow());
		renderJson(result);
	}

	public void add() {
		render("add.html");
	}

	public void edit() {
		String id = getPara("id");
		Customer customer = Customer.dao.findById(id);
		setAttr("customer", customer);
		render("add.html");
	}

	@Before(CustomerValidate.class)
	public void save() {
		Customer customer = new Customer();
		String nickname = getPara("nickname");
		customer.set("NICKNAME", nickname);
		String kfAccount = null;
		try {
			kfAccount = PinyinHelper.getShortPinyin(nickname);
		} catch (PinyinException e1) {
			e1.printStackTrace();
		}
		Customer exists = customerService.findByKfAccount(kfAccount);
		if (exists != null) {
			long count = customerService.countByKfAccount(kfAccount);
			kfAccount += count + 1;
		}
		kfAccount += "@competition";
		customer.set("DUOKEFU", kfAccount);
		String password = getPara("password");
		customer.set("PASSWORD", password);
		customer.set("PHONE", getPara("phone"));
		customer.set("WEIXIN", getPara("weixin"));
		customer.set("DESCRIBE", getPara("describe"));
		//先设置默认图片防止没上传的
		String defaultImg = getPara("defaultImg");
		if (StringUtils.isNotEmpty(defaultImg)) {
			customer.set("AVATAR", defaultImg);
		} else {
			customer.set("AVATAR", PropKit.get("default.noimage"));
		}
		UploadFile file = null;
		try {
			file = getFile("avatar");
			if (file != null) {
				String localFilePath = file.getUploadPath() + File.separator + file.getFileName();
				String path = QiniuUtils.upload(localFilePath);
				String crop = getPara("crop");//截图参数
				customer.set("AVATAR", path + (StringUtils.isNotEmpty(crop) ? crop : ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String id = getPara("id");
		if (StringUtils.isNotEmpty(id)) {
			customer.set("ID", id);
			customer.update();
			ApiResult updateResult = CustomServiceApi.updateKfAccount(kfAccount, nickname, password);
			if (file != null) {
				ApiResult uploadUDResult = CustomServiceApi.uploadKfAccountHeadImg(kfAccount, file.getFile());
			}
		} else {
			customer.set("ID", UUID.randomUUID().toString().replace("-", ""));
			customer.save();
			ApiResult addResult = CustomServiceApi.addKfAccount(kfAccount, nickname, password);
			if (file != null) {
				ApiResult uploadResult = CustomServiceApi.uploadKfAccountHeadImg(kfAccount, file.getFile());
			}
		}
		renderJson();
	}

	public void remove() {
		String id = getPara("id");
		Customer customer = Customer.dao.findById(id);
		String kfAccount = customer.get("DUOKEFU");
		ApiResult updateResult = CustomServiceApi.delKfAccount(kfAccount);
		boolean isDeleted = customer.delete();
		if (isDeleted) {
			renderJson(new JSONSuccess("删除成功"));
		} else {
			renderJson(new JSONError("删除失败，该条记录不存在", id));
		}
	}

}
