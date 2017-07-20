/**
 * Created At 2017年7月10日下午10:20:11 
 * 
 */

package core.admin.controller.shop;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import core.admin.controller.base.BaseController;
import core.admin.service.shop.ShopService;
import core.admin.service.shop.impl.ShopServiceImpl;
import core.common.utils.QiniuUtils;
import core.model.Shop;
import core.model.ShopType;
import core.utils.MD5Util;
import core.validate.ShopValidate;
import core.vo.DTParams;
import core.vo.JSONError;
import core.vo.JSONSuccess;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月10日下午10:20:11
 */

public class ShopController extends BaseController {
	private final Log log = Log.getLog(ShopController.class);
	private ShopService service = new ShopServiceImpl();

	public void getData() {
		getData(null);
	}

	private void getData(Map<String, Map<String, Object>> map) {
		DTParams params = new DTParams(getParaMap());
		Page<Record> shopType = service.getDTPage(params, "shop.list", map);
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", shopType.getTotalRow());
		List<Record> list = shopType.getList();
		result.put("data", list);
		result.put("recordsFiltered", shopType.getTotalRow());
		renderJson(result);
	}

	public void add() {
		Map<String, String> params = new HashMap<>();
		params.put("not_deleted", "1");
		setAttr("shopTypes", ShopType.dao.find(Db.getSqlPara("shopType.list", params)));
		render("add.html");
	}

	public void edit() {
		String id = getPara("id");
		if (StringUtils.isEmpty(id)) {
			renderJson(new JSONError("该ID不存在"));
		}
		ShopType shopType = ShopType.dao.findById(id);
		setAttr("shopType", shopType);
		render("add.html");
	}

	@Before(ShopValidate.class)
	public void save() {
		String id = getPara("id");

		Shop shop = new Shop();
		shop.set("NAME", getPara("name"));
		shop.set("DESCRIPTION", getPara("description"));
		shop.set("ADDRESS", getPara("address"));
		shop.set("LATIDUTE", getPara("latitude"));
		shop.set("LONGITUDE", getPara("longitude"));
		String addressCode = getPara("addressCode");
		shop.set("PCODE", addressCode.substring(0, 2) + "0000");
		shop.set("PCCODE", addressCode.substring(0, 4) + "00");
		shop.set("PTCODE", addressCode);
		shop.set("PASSWORD", MD5Util.encrypt(getPara("password"), PropKit.get("encrypt_key")));
		shop.set("TEL", getPara("phone"));
		shop.set("QQ", getPara("qq"));
		shop.set("OPEN_TIME", getPara("openTime"));
		shop.set("CLOSE_TIME", getPara("closeTime"));
		int on = 0;
		if ("on".equals(getPara("autoOpen"))) {
			on = 1;
		}
		shop.set("AUTO_OPEN", on);
		shop.set("DELIVERY_PRICE", getParaToDouble("deliveryPrice"));
		shop.set("DELIVERY_OFF_THRESHOLD", getParaToDouble("deliveryOffThreshold"));
		shop.set("DELIVERY_OFF", getParaToDouble("deliveryOff"));
		shop.set("REDUCTION_THRESHOLD", getParaToDouble("redutionThreshold"));
		shop.set("REDUCTION", getParaToDouble("redution"));
		shop.set("GIFT_THRESHOLD", getParaToDouble("giftThreshold"));
		shop.set("GIFT", getPara("gift"));
		shop.set("AUDIT_STATE", 0);
		//先设置默认图片防止没上传的
		String defaultImg = getPara("defaultImg");
		if (StringUtils.isNotEmpty(defaultImg)) {
			shop.set("STATE", -1);
			shop.set("IMG", defaultImg);
		} else {
			shop.set("IMG", PropKit.get("default.noimage"));
		}
		try {
			UploadFile file = getFile("img");
			if (file != null) {
				String localFilePath = file.getUploadPath() + File.separator + file.getFileName();
				String path = QiniuUtils.upload(localFilePath);
				String crop = getPara("crop");//截图参数
				shop.set("IMG", path + (StringUtils.isNotEmpty(crop) ? crop : ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (StringUtils.isNotEmpty(id)) {
			shop.set("ID", id);
			shop.set("UERNAME", getPara("username"));
			shop.update();
		} else {
			shop.save();
		}
		renderJson(new JSONSuccess());
	}

	public void disable() {
		String id = getPara("id");
		if (StringUtils.isEmpty(id)) {
			renderJson(new JSONError("该ID不存在"));
		}
		ShopType shopType = ShopType.dao.findById(id);
		shopType.set("DELETED", 1);
		shopType.update();
		renderJson(new JSONSuccess("停用成功"));
	}

	public void enable() {
		String id = getPara("id");
		if (StringUtils.isEmpty(id)) {
			renderJson(new JSONError("该ID不存在"));
		}
		ShopType shopType = ShopType.dao.findById(id);
		shopType.set("DELETED", 0);
		shopType.update();
		renderJson(new JSONSuccess("恢复成功"));
	}

	public void remove() {
		String shopId = getPara("id");
		if (StringUtils.isNotEmpty(shopId)) {
			if (service.hasOrder(shopId)) {
				renderJson(new JSONError("该店铺下有关联订单不能删除！"));
			} else {
				Shop.dao.deleteById(shopId);
				renderJson(new JSONSuccess("删除店铺成功！"));
			}
		} else {
			renderJson(new JSONError("店铺ID不存在！"));
		}
	}

	public void validateUsername() {
		JSONObject result = new JSONObject();
		ShopService service = new ShopServiceImpl();
		boolean valid = true;
		if (StringUtils.isEmpty(getPara("id"))) {
			valid = !service.registerd(getPara("username"));
		}
		result.put("valid", valid);
		renderJson(result);
	}
}
