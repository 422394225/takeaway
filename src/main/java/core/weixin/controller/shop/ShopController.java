/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.shop;

import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import core.admin.service.shop.relation.ShopTypeRelationService;
import core.admin.service.shop.relation.impl.ShopTypeRelationServiceImpl;
import core.model.Shop;
import core.model.ShopType;
import core.model.ShopTypeRelation;
import core.utils.MD5Util;
import core.validate.ShopWXValidate;
import core.vo.JSONSuccess;
import core.weixin.base.BaseController;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月24日下午10:03:40
 */

public class ShopController extends BaseController {
	private Log log = Log.getLog(ShopController.class);
	private ShopTypeRelationService strService = new ShopTypeRelationServiceImpl();

	public void add() {
		Map<String, String> params = new HashMap<>();
		params.put("not_deleted", "1");
		setAttr("shopTypes", ShopType.dao.find(Db.getSqlPara("shopType.list", params)));
		render("add.html");
	}

	@Before(ShopWXValidate.class)
	public void save() {
		String id = getPara("id");

		Shop shop = Shop.dao.findById(id);
		if (shop == null) {
			shop = new Shop();
		}
		shop.set("NAME", getPara("name"));
		shop.set("ADMIN_NAME", getPara("adminName"));
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
		String onStr = getPara("autoOpen");
		if ("on".equals(onStr)) {
			on = 1;
		}
		shop.set("AUTO_OPEN", on);
		if (StringUtils.isNotEmpty(id)) {
			shop.update();
			strService.deleteAll(id);
		} else {
			shop.set("USERNAME", getPara("username"));
			shop.set("AUDIT_STATE", 0);
			shop.set("STATE", -1);
			shop.save();
		}
		String[] temp = getPara("shopTypeId").split(",");
		Integer[] shopTypeId = new Integer[temp.length];
		for (int i = 0; i < temp.length; i++) {
			shopTypeId[i] = Integer.valueOf(temp[i]);
		}
		if (shopTypeId != null) {
			for (Integer typeId : shopTypeId) {
				ShopTypeRelation shopTypeRelation = new ShopTypeRelation();
				shopTypeRelation.set("TYPEID", typeId);
				shopTypeRelation.set("SHOPID", shop.getInt("ID"));
				shopTypeRelation.save();
			}
		}
		renderJson(new JSONSuccess());
	}
}
