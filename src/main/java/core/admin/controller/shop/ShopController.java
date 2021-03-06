/**
 * Created At 2017年7月10日下午10:20:11 
 * 
 */

package core.admin.controller.shop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

import core.admin.controller.base.BaseController;
import core.admin.service.audit.AuditService;
import core.admin.service.audit.impl.AuditServiceImpl;
import core.admin.service.shop.ShopService;
import core.admin.service.shop.impl.ShopServiceImpl;
import core.admin.service.shop.relation.ShopTypeRelationService;
import core.admin.service.shop.relation.impl.ShopTypeRelationServiceImpl;
import core.common.utils.QiniuUtils;
import core.interceptor.PowerInterceptor;
import core.model.Shop;
import core.model.ShopType;
import core.model.ShopTypeRelation;
import core.temple.AuditResultTemple;
import core.utils.MD5Util;
import core.validate.ModPassValidate;
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
	private ShopTypeRelationService strService = new ShopTypeRelationServiceImpl();
	private AuditService auditService = new AuditServiceImpl();
	private static final String LAST_MONTH_SQL = "select count(1) as order_num,sum(pay_price) as total_price from t_order where SHOP_ID=? and date_format(create_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m') AND IFNULL(CANCEL_STATE,'-1')<>2 ";
	private static final String THIS_MONTH_SQL = "select count(1) as order_num,sum(pay_price) as total_price from t_order where SHOP_ID=? and date_format(create_time,'%Y-%m')=date_format(now(),'%Y-%m') AND IFNULL(CANCEL_STATE,'-1')<>2 ";
	private static final String THIS_WEEK_SQL = "select count(1) as order_num,sum(pay_price) as total_price from t_order where SHOP_ID=? and YEARWEEK(date_format(create_time,'%Y-%m-%d')) = YEARWEEK(now()) AND IFNULL(CANCEL_STATE,'-1')<>2 ";
	private static final String LAST_WEEK_SQL = "select count(1) as order_num,sum(pay_price) as total_price from t_order where SHOP_ID=? and YEARWEEK(date_format(create_time,'%Y-%m-%d')) = YEARWEEK(now())-1 AND IFNULL(CANCEL_STATE,'-1')<>2 ";

	@Override
	public void index() {
		Map<String, String> params = new HashMap<>();
		params.put("not_deleted", "1");
		setAttr("shopTypes", ShopType.dao.find(Db.getSqlPara("shopType.list", params)));
		render("list.html");
	}

	public void audit() {
		render("audit.html");
	}

	public void calc() {
		int shopId = getParaToInt("id");
		setAttr("shopId", shopId);
		Record record = Db.findFirst(LAST_MONTH_SQL, shopId);
		setAttr("LAST_MONTH_COUNT", record.get("order_num"));
		setAttr("LAST_MONTH_PRICE", record.get("total_price"));
		record = Db.findFirst(THIS_MONTH_SQL, shopId);
		setAttr("THIS_MONTH_COUNT", record.get("order_num"));
		setAttr("THIS_MONTH_PRICE", record.get("total_price"));
		record = Db.findFirst(THIS_WEEK_SQL, shopId);
		setAttr("THIS_WEEK_COUNT", record.get("order_num"));
		setAttr("THIS_WEEK_PRICE", record.get("total_price"));
		record = Db.findFirst(LAST_WEEK_SQL, shopId);
		setAttr("LAST_WEEK_COUNT", record.get("order_num"));
		setAttr("LAST_WEEK_PRICE", record.get("total_price"));
		render("calc.html");
		// renderText("123");
	}

	public void getPurchasingPrice() {
		int shopId = 0;
		String viewType = null;
		try {
			shopId = getParaToInt("shopId");
			viewType = getPara("viewType");
			if (viewType == null || shopId == 0)
				throw new Exception();
		} catch (Exception e) {
			renderJson(new JSONError("参数错误"));
		}
		// <foodId,purchasingPrice>
		Map<Integer, Double> purchasingPriceMap = service.getFoodPurchasingPrice(shopId);
		double purchasingPrice = 0;
		ArrayList<JSONArray> orderFoodsList = service.getOrderFoodsMapByShop(shopId, viewType);
		if (orderFoodsList == null) {
			renderJson(new JSONError("类型错误"));
			return;
		}
		for (JSONArray foodArray : orderFoodsList) {
			for (Object object : foodArray) {
				JSONObject foodObject = (JSONObject) object;
				if (!purchasingPriceMap.containsKey(foodObject.getInteger("id"))) {
					renderJson(new JSONError("商品永久从数据库中删除，无法统计"));
					return;
				}
				purchasingPrice += purchasingPriceMap.get(foodObject.getInteger("id")) * foodObject.getIntValue("num");
			}
		}
		renderJson(new JSONSuccess(purchasingPrice));
	}

	public void getData() {
		getData(null, null);
	}

	public void getAuditData() {
		Map<String, Map<String, Object>> map = new HashMap<>();
		Map<String, Object> order = new HashMap<>();
		order.put("AUDIT_STATE", "ASC");
		order.put("CREATE_TIME", "DESC");
		map.put("orderbyCond", order);
		getData(map, "shop.audit");
	}

	private void getData(Map<String, Map<String, Object>> map, String sqlkey) {
		DTParams params = new DTParams(getParaMap());
		if (StringUtils.isEmpty(sqlkey)) {
			sqlkey = "shop.list";
		}
		Page<Record> shopType = service.getDTPage(params, sqlkey, map);
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", shopType.getTotalRow());
		List<Record> list = shopType.getList();
		result.put("data", list);
		result.put("recordsFiltered", shopType.getTotalRow());
		renderJson(result);
	}

	public void doBusiness() {
		try {
			Integer shopId = getParaToInt("shopId");
			int result = Db.update("UPDATE T_SHOP SET STATE=2 WHERE ID=? AND STATE>-2", shopId);
			if (result == 0) {
				renderText("不存在店家或店家状态错误");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderText("操作失败");
		}
		renderText("success");
	}

	public void rest() {
		try {
			Integer shopId = getParaToInt("shopId");
			int result = Db.update("UPDATE T_SHOP SET STATE=0 WHERE ID=? AND STATE=2", shopId);
			if (result == 0) {
				renderText("不存在店家或店家状态错误");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderText("操作失败");
		}
		renderText("success");
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
		Shop shop = Shop.dao.findById(id);

		List<Record> shopTypes = Db.find(Db.getSql("shopTyperRelations.getShopType"), id);
		setAttr("types", shopTypes);
		Map<String, String> params = new HashMap<>();
		setAttr("shopTypes", ShopType.dao.find(Db.getSqlPara("shopType.list", params)));
		setAttr("shop", shop);
		render("add.html");
	}

	public void passAudit() {
		String shopId = getPara("id");
		Shop shop = Shop.dao.findById(shopId);
		if (shop != null) {
			service.audit(this, shop, 1);
			if (shop.get("OPENID") != null) {
				AuditResultTemple auditResultTemple = new AuditResultTemple();
				auditResultTemple.first = "审核通过了哦~";
				auditResultTemple.keyword1 = shop.getStr("NAME");
				auditResultTemple.keyword2 = shop.getStr("ADDRESS");
				auditResultTemple.keyword3 = "通过审核！";
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				auditResultTemple.keyword4 = dateFormat.format(new Date());
				auditResultTemple.remark = "欢迎加入那里外卖";
			}
			renderJson(new JSONSuccess("审核成功！"));
		} else {
			renderJson(new JSONError("商家ID不存在！"));
		}
	}

	public void sendBack() {
		String shopId = getPara("id");
		Shop shop = Shop.dao.findById(shopId);
		if (shop != null) {
			if (shop.get("OPENID") != null) {
				AuditResultTemple auditResultTemple = new AuditResultTemple();
				auditResultTemple.first = "审核没通过啊_(:з」∠)_";
				auditResultTemple.keyword1 = shop.getStr("NAME");
				auditResultTemple.keyword2 = shop.getStr("ADDRESS");
				auditResultTemple.keyword3 = "没通过审核";
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				auditResultTemple.keyword4 = dateFormat.format(new Date());
				auditResultTemple.remark = "信息核对下再申请试试吧~";
			}
			setAttr("shop", shop);
		}
		render("sendBack.html");
	}

	public void sendbackSave() {
		String shopId = getPara("id");
		Shop shop = Shop.dao.findById(shopId);
		if (shop != null) {
			service.audit(this, shop, 2);
			renderJson(new JSONSuccess("退回成功！"));
		} else {
			renderJson(new JSONError("商家ID不存在！"));
		}
	}

	@Before(ShopValidate.class)
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
		shop.set("LATITUDE", getPara("latitude"));
		shop.set("LONGITUDE", getPara("longitude"));
		String addressCode = getPara("addressCode");
		shop.set("PCODE", addressCode.substring(0, 2) + "0000");
		shop.set("PCCODE", addressCode.substring(0, 4) + "00");
		shop.set("PTCODE", addressCode);
		String password = getPara("password");
		shop.set("PASSWORD", MD5Util.encrypt(password, PropKit.get("encrypt_key")));
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
		shop.set("DELIVERY_THRESHOLD", getParaToDouble("deliveryThreshold"));
		shop.set("DELIVERY_OFF_THRESHOLD", getParaToDouble("deliveryOffThreshold"));
		shop.set("DELIVERY_OFF", getParaToDouble("deliveryOff"));
		shop.set("REDUCTION_THRESHOLD", getParaToDouble("redutionThreshold"));
		shop.set("REDUCTION", getParaToDouble("redution"));
		shop.set("GIFT_THRESHOLD", getParaToDouble("giftThreshold"));
		shop.set("GIFT", getPara("gift"));
		// 先设置默认图片防止没上传的
		String defaultImg = getPara("defaultImg");
		if (StringUtils.isNotEmpty(defaultImg)) {
			shop.set("IMG", defaultImg);
		} else {
			shop.set("IMG", PropKit.get("default.noimage"));
		}
		try {
			UploadFile file = getFile("img");
			if (file != null) {
				String localFilePath = file.getUploadPath() + File.separator + file.getFileName();
				String path = QiniuUtils.upload(localFilePath);
				String crop = getPara("crop");// 截图参数
				shop.set("IMG", path + (StringUtils.isNotEmpty(crop) ? crop : ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (StringUtils.isNotEmpty(id)) {
			shop.update();
			strService.deleteAll(id);
		} else {
			shop.set("USERNAME", getPara("username"));
			shop.set("AUDIT_STATE", 0);
			shop.set("STATE", -1);
			shop.save();
		}
		Integer[] shopTypeId = getParaValuesToInt("shopTypeId");
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

	public void delete() {
		String shopId = getPara("id");
		if (StringUtils.isNotEmpty(shopId)) {
			if (service.hasOrder(shopId)) {
				renderJson(new JSONError("该商家下有关联订单不能删除！"));
			} else if (service.hasFood(shopId)) {
				renderJson(new JSONError("该商家下有关联商品不能删除！"));
			} else {
				Shop.dao.deleteById(shopId);
				strService.deleteAll(shopId);
				renderJson(new JSONSuccess("删除商家成功！"));
			}
		} else {
			renderJson(new JSONError("商家ID不存在！"));
		}
	}

	public void remove() {
		String shopId = getPara("id");
		if (StringUtils.isNotEmpty(shopId)) {
			Shop shop = Shop.dao.findById(shopId);
			shop.set("STATE", -2);
			shop.update();
			renderJson(new JSONSuccess("移除商家成功！"));
		} else {
			renderJson(new JSONError("商家ID不存在！"));
		}
	}

	public void unremove() {
		String shopId = getPara("id");
		if (StringUtils.isNotEmpty(shopId)) {
			Shop shop = Shop.dao.findById(shopId);
			shop.set("STATE", -1);
			shop.update();
			renderJson(new JSONSuccess("恢复商家成功！"));
		} else {
			renderJson(new JSONError("商家ID不存在！"));
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

	// 修改密码
	@Clear(PowerInterceptor.class)
	@Before({ ModPassValidate.class })
	public void modPass() {
		String newPass = getPara("confirm");
		Shop shop = getSessionAttr("shop");
		shop.set("PASSWORD", MD5Util.encrypt(newPass + PropKit.get("encrypt_key")));
		shop.update();
		removeSessionAttr("shop");
		removeCookie("shop");
		renderJson(new JSONSuccess());
	}

	public void auditHistory() {
		String shopId = getPara("id");
		setAttr("audits", auditService.getByShopId(shopId));
		render("audit-history.html");
	}
}
