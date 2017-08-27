/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.food;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import core.admin.service.shop.relation.ShopTypeRelationService;
import core.admin.service.shop.relation.impl.ShopTypeRelationServiceImpl;
import core.common.constants.ShopConstants;
import core.common.utils.QiniuUtils;
import core.interceptor.JSSDKInterceptor;
import core.model.Food;
import core.model.Shop;
import core.model.ShopType;
import core.model.ShopTypeRelation;
import core.utils.LocationUtils;
import core.utils.MD5Util;
import core.validate.ShopWXValidate;
import core.vo.ConditionsVO;
import core.vo.JSONError;
import core.vo.JSONSuccess;
import core.weixin.api.MediaApi;
import core.weixin.base.BaseController;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月24日下午10:03:40
 */

public class FoodController extends BaseController {
	private Log log = Log.getLog(FoodController.class);
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

	public void ajaxFoodByType(){
		String id = getPara("id");
		if(StringUtils.isNotEmpty(id)){
			List<Food> foods = Food.dao.find(Db.getSqlPara("food.getByType",Kv.by("id",id)));
			renderJson(new JSONSuccess(foods));
		}else {
			renderJson(new JSONError("商品类别ID为空！"));
		}
	}
}
