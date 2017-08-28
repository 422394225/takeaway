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

	public void index() {
		renderText("OrderRecieveControllerindex");
	}

	public void orderPayed() {
		String xmlMsg = HttpKit.readData(getRequest());
		System.out.println(xmlMsg);
		Document doc = Jsoup.parse(xmlMsg);
		Element xmlElement = doc.select("xml").first();
		OrderPayResult result = new OrderPayResult(xmlElement);
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
