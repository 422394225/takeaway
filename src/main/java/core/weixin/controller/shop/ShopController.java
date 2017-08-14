/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.shop;

import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import core.admin.service.shop.relation.ShopTypeRelationService;
import core.admin.service.shop.relation.impl.ShopTypeRelationServiceImpl;
import core.common.utils.QiniuUtils;
import core.interceptor.JSSDKInterceptor;
import core.model.Shop;
import core.model.ShopType;
import core.model.ShopTypeRelation;
import core.utils.MD5Util;
import core.validate.ShopWXValidate;
import core.vo.ConditionsVO;
import core.vo.JSONError;
import core.vo.JSONSuccess;
import core.weixin.api.MediaApi;
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

	/**
	 * 如果要支持多公众账号，只需要在此返回各个公众号对应的 ApiConfig 对象即可 可以通过在请求 url 中挂参数来动态从数据库中获取
	 * ApiConfig 属性值
	 */
	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();

		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));

		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
		return ac;
	}

	@Before(JSSDKInterceptor.class)
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
		//上传素材
		ApiConfigKit.setThreadLocalApiConfig(getApiConfig());

		String logoUrl = QiniuUtils.upload(MediaApi.mediaGet(getPara("logo")));
		shop.set("IMG", logoUrl + "?imageView2/1/w/200/h/200");
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

	public void ajaxShopTypes() {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> where = new HashMap<>();
		where.put("DELETED", " == 1");
		map.put("limitCond", where);
		if (getPara("type") == null) {
			map.put("only", 7);
		}
		List<ShopType> shopTypes = ShopType.dao.find(Db.getSqlPara("shopType.list", map));
		renderJson(new JSONSuccess(shopTypes));
	}

	public void ajaxShops() {
		ConditionsVO conditionsVO = new ConditionsVO();
		Map<String, Object> condi = new LinkedHashMap<>();
		String flag = getPara("flag");
		if (StringUtils.isNotEmpty(flag)) {
			switch (flag) {
			case "hot": {
				condi.put("RATE_AVG", "DESC");
				condi.put("SALE_NUM", "DESC");
				conditionsVO.getOrderbyCond().putAll(condi);
				break;
			}
			default:
				break;
			}
		}
		Integer pageNumber = getParaToInt("pageNumber");
		Integer pageSize = getParaToInt("pageSize");
		if (pageNumber != null) {
			if (pageSize == null) {
				pageSize = 10;
			}
			Page<Shop> shops = Shop.dao.paginate(pageNumber, pageSize,
					Db.getSqlPara("shop.list", conditionsVO.getConditions()));
			renderJson(new JSONSuccess(shops));
		} else {
			List<Shop> shops = Shop.dao.find(Db.getSqlPara("shop.list", conditionsVO.getConditions()));
			renderJson(new JSONSuccess(shops));
		}
	}

	public void ajaxSearchShops() {
		String keyword = getPara("keyword");
		String type = getPara("type");
		List<Record> records = null;
		Map<String, String> map = new HashMap<>();
		map.put("keyword", keyword);
		if ("food".equals(type)) {
			records = Db.find(Db.getSqlPara("food.search", map));
		} else if ("shop".equals(type)) {
			records = Db.find(Db.getSqlPara("shop.search", map));
		}
		renderJson(new JSONSuccess(records));
	}

	public void typeDetail() {
		String id = getPara("id");
		if (StringUtils.isNotEmpty(id)) {
			setAttr("typeName", ShopType.dao.findById(id).getStr("NAME"));
			List<Shop> shops = Shop.dao.find(Db.getSql("shop.getByType"), id);
			setAttr("shops", shops);
			render("typeDetail.html");
		} else {
			renderJson(new JSONError("该店铺分类不存在！"));
		}
	}

	public void typeMore() {
		render("typeMore.html");
	}
}
