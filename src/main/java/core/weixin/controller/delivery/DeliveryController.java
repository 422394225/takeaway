/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.delivery;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import core.model.Order;
import core.vo.JSONSuccess;
import core.weixin.controller.WeixinMsgController;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月24日下午10:03:40
 */

public class DeliveryController extends WeixinMsgController {
	private static final Map<String, Integer> LOGIN_ACCESS_KEY = new Hashtable<>();
	private Log log = Log.getLog(DeliveryController.class);
	private final static String ENCRIPT_KEY = PropKit.get("encrypt_key");
	private static Record defaultOrder;
	static {
		defaultOrder = new Record();
		defaultOrder.set("ID", -1);
		defaultOrder.set("STYLE", "display:none");
	}

	public void index() {
		String accessKey = getPara("accessKey");
		if (accessKey == null || !LOGIN_ACCESS_KEY.containsKey(accessKey)) {
			render("login.html");
			return;
		} else {
			setAttr("deliveryId", LOGIN_ACCESS_KEY.get(accessKey));
			List<Record> records = Db.find(
					"SELECT A.ID,A.USER_NAME,A.USER_TEL,A.USER_ADDRESS,B.ADDRESS AS SHOP_ADDRESS,B.TEL AS SHOP_TEL,B.IMG,'' AS STYLE,B.NAME AS SHOP_NAME FROM T_ORDER A LEFT JOIN T_SHOP B ON A.SHOP_ID=B.ID WHERE A.DELIVERY_ID IS NULL AND A.ORDER_STATE<5 AND A.CANCEL_STATE IS NULL LIMIT 10");
			while (records.size() < 10) {
				records.add(defaultOrder);
			}
			setAttr("orderInfo", records);
			render("takeOrder.html");
		}
	}

	public void listOrder() {
		Integer deliveryId = getParaToInt("deliveryId");
		setAttr("deliveryId", deliveryId);
		if (deliveryId == null || !LOGIN_ACCESS_KEY.containsValue(deliveryId)) {
			render("login.html");
			return;
		}
		List<Record> records = Db.find(
				"SELECT A.ID,A.USER_NAME,A.USER_TEL,A.USER_ADDRESS,B.ADDRESS AS SHOP_ADDRESS,B.TEL AS SHOP_TEL,B.IMG,'' AS STYLE,B.NAME AS SHOP_NAME FROM T_ORDER A LEFT JOIN T_SHOP B ON A.SHOP_ID=B.ID WHERE A.DELIVERY_ID =? AND A.ORDER_STATE<5 AND A.CANCEL_STATE IS NULL",
				deliveryId);
		setAttr("orderInfo", records);
		render("listOrder.html");
	}

	public void getListOrder() {
		Integer deliveryId = getParaToInt("deliveryId");
		setAttr("deliveryId", deliveryId);
		List<Record> records = Db.find(
				"SELECT A.ID,A.USER_NAME,A.USER_TEL,A.USER_ADDRESS,B.ADDRESS AS SHOP_ADDRESS,B.TEL AS SHOP_TEL,B.IMG,'' AS STYLE,B.NAME AS SHOP_NAME FROM T_ORDER A LEFT JOIN T_SHOP B ON A.SHOP_ID=B.ID WHERE A.DELIVERY_ID =? AND A.ORDER_STATE<5 AND A.CANCEL_STATE IS NULL",
				deliveryId);
		renderJson(new JSONSuccess(records));

	}

	public synchronized void takeOrder() {
		Integer deliveryId = getParaToInt("deliveryId");
		if (deliveryId == null || !LOGIN_ACCESS_KEY.containsValue(deliveryId)) {
			render("login.html");
			return;
		}
		Integer orderId = getParaToInt("orderId");
		if (orderId == null) {
			setAttr("msg", "参数错误");
			render("takeOrderError.html");
			return;
		}
		Order order = Order.dao.findById(orderId);
		if (order == null) {
			setAttr("msg", "找不到订单");
			render("takeOrderError.html");
			return;
		}
		if (order.getInt("DELIVERY_ID") != null) {
			setAttr("msg", "可惜了，没抢到~");
			render("takeOrderError.html");
			return;
		}
		order.set("DELIVERY_ID", deliveryId);
		order.update();
		listOrder();
	}

	protected static void addLoginAccessKey(String accessKey, int deliveryId) {
		LOGIN_ACCESS_KEY.put(accessKey, deliveryId);
	}

	protected static void removeLoginAccessKey(String accessKey) {
		LOGIN_ACCESS_KEY.remove(accessKey);
	}
}
