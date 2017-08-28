/**
 * Created At 2017年7月24日下午10:03:40 
 * 
 */

package core.weixin.controller.order;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Random;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.jfinal.MsgInterceptor;
import com.jfinal.weixin.sdk.utils.HttpUtils;

import core.model.Food;
import core.model.Order;
import core.model.Shop;
import core.model.User;
import core.utils.MD5Util;
import core.vo.JSONError;
import core.vo.JSONSuccess;
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
