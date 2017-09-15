/**
 * Created At 2017年7月8日下午11:28:18 
 * 
 */

package core.admin.service.shop;

import java.util.ArrayList;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;

import core.admin.service.base.DataTableService;
import core.model.Shop;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月8日下午11:28:18
 */

public interface ShopService extends DataTableService {
	static final String SELECT_PURCHASING_PRICE_PREFIX = "SELECT ID,FOODS FROM T_ORDER WHERE SHOP_ID=? AND IFNULL(CANCEL_STATE,'-1')<>2 AND";
	static final Map<String, String> SELECT_PURCHASING_PRICE_SUFFIX = Kv
			.by("LAST_MONTH_PURCHASING_PRICE",
					" date_format(create_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m') ")
			.set("LAST_WEEK_PURCHASING_PRICE", " YEARWEEK(date_format(create_time,'%Y-%m-%d')) = YEARWEEK(now())-1 ")
			.set("THIS_MONTH_PURCHASING_PRICE", " date_format(create_time,'%Y-%m')=date_format(now(),'%Y-%m') ")
			.set("THIS_WEEK_PURCHASING_PRICE", " YEARWEEK(date_format(create_time,'%Y-%m-%d')) = YEARWEEK(now())");

	boolean hasOrder(String shopId);

	boolean registerd(String username);

	boolean hasFood(String shopId);

	void audit(Controller controller, Shop shop, int state);

	/**
	 * @param shopId
	 * @return<foodId,purchasingPrice>
	 */
	public Map<Integer, Double> getFoodPurchasingPrice(int shopId);

	/**
	 * 获取店家的在一定时期内的所有商品json<br>
	 * 即在order表里的FOODS字段
	 */
	ArrayList<JSONArray> getOrderFoodsMapByShop(int shopId, String limitType);
}
