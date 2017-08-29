/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.order;

import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;

import core.model.Order;
import core.vo.OrderPayResult;
import core.weixin.controller.WeixinMsgController;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月24日下午10:03:40
 */

public class OrderRecieveController extends WeixinMsgController {
	private Log log = Log.getLog(OrderRecieveController.class);
	private final String RECIEVE_SUCCESS_RESULT = "<xml>  <return_code><![CDATA[SUCCESS]]></return_code> <return_msg><![CDATA[OK]]></return_msg></xml>";
	private final String RECIEVE_FAIL_RESULT_PRE = "<xml>  <return_code><![CDATA[FAIL]]></return_code> <return_msg><![CDATA[";
	private final String RECIEVE_FAIL_RESULT_SUF = "]]></return_msg></xml>";

	public void index() {
		renderText("OrderRecieveControllerindex");
	}

	public void orderPayed() {
		String xmlMsg = HttpKit.readData(getRequest());
		System.out.println(xmlMsg);
		Document doc = Jsoup.parse(xmlMsg);
		Element xmlElement = doc.select("xml").first();
		OrderPayResult result = new OrderPayResult(xmlElement);
		if ("SUCCESS".equals(result.result_code)) {
			Order order = Order.dao.findById(result.out_trade_no);
			if (order.getDouble("PAY_PRICE") * 100 != result.total_fee) {
				renderText(RECIEVE_FAIL_RESULT_PRE + "金额校验失败" + RECIEVE_FAIL_RESULT_SUF);
				return;
			}
			if (order.getInt("ORDER_STATE") == 1 && order.getInt("PAY_STATE") == 0
					&& order.getInt("CANCEL_STATE") == null) {
				order.set("ORDER_STATE", 2);
				order.set("PAY_STATE", 1);
				order.update();
			}
		}
		renderText(RECIEVE_SUCCESS_RESULT);
	}

	public void orderPayed1() {
		JSONObject params = JSONObject.parseObject(HttpKit.readData(getRequest()));
		Map<String, String[]> paraMap = getParaMap();
		for (Entry<String, String[]> entry : paraMap.entrySet()) {
			System.out.println(entry.getKey());
			for (String string : entry.getValue()) {
				System.out.println("\t" + string);
			}
		}
		renderText("orderPayed");

	}
}
