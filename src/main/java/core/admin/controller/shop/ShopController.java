/**
 * Created At 2017年7月10日下午10:20:11 
 * 
 */

package core.admin.controller.shop;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
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

import core.admin.service.shop.ShopService;
import core.admin.service.shop.impl.ShopServiceImpl;
import core.common.utils.QiniuUtils;
import core.model.ShopType;
import core.validate.ShopTypeValidate;
import core.vo.DTParams;
import core.vo.JSONError;
import core.vo.JSONSuccess;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月10日下午10:20:11
 */

public class ShopController extends Controller {
	private final Log log = Log.getLog(ShopController.class);
	private ShopService service = new ShopServiceImpl();

	public void index() {
		render("list.html");
	}

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

	@Before(ShopTypeValidate.class)
	public void save() {
		String id = getPara("id");

		ShopType shopType = new ShopType();
		shopType.set("NAME", getPara("name"));
		shopType.set("DELETED", getPara("deleted"));
		//先设置默认图片防止没上传的
		String defaultImg = getPara("defaultImg");
		if (StringUtils.isNotEmpty(defaultImg)) {
			shopType.set("ICON", defaultImg);
		} else {
			shopType.set("ICON", PropKit.get("default.noimage"));
		}
		try {
			UploadFile file = getFile("icon");
			if (file != null) {
				String localFilePath = file.getUploadPath() + File.separator + file.getFileName();
				String path = QiniuUtils.upload(localFilePath);
				String crop = getPara("crop");//截图参数
				shopType.set("ICON", path + (StringUtils.isNotEmpty(crop) ? crop : ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (StringUtils.isNotEmpty(id)) {
			shopType.set("ID", id);
			shopType.update();
		} else {
			shopType.save();
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

}
