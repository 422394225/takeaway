package core.admin.controller.order;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import core.admin.service.order.OrderService;
import core.admin.service.order.impl.OrderServiceImpl;
import core.interceptor.WxApiConfigInterceptor;
import core.model.Order;
import core.utils.ClientCustomSSL;
import core.utils.WeiXinUtils;
import core.vo.DTParams;
import core.vo.JSONSuccess;

/**
 * 
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:40:27
 */
public class OrderController extends Controller {

	private Log log = Log.getLog(OrderController.class);
	private OrderService orderService = new OrderServiceImpl();
	private static final String WEIXIN_REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	public void index() {
		String showType = getPara("showType");
		setAttr("showType", showType);
		render("list.html");
	}

	public void view() {
		String orderId = getPara("id");

		Record detail = orderService.getDetail(orderId);
		for (Entry<String, Object> entry : detail.getColumns().entrySet()) {
			setAttr(entry.getKey(), entry.getValue());
		}
		try {
			Record cancelRecord = Db.findFirst(
					"select name from t_dict_code where field='t_order.CANCEL_STATE' and code=?",
					detail.getInt("CANCEL_STATE"));
			setAttr("CANCEL_STRING", cancelRecord.getStr("name"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		JSONArray foods = JSONArray.parseArray(detail.getStr("FOODS"));
		JSONArray foodsResult = new JSONArray();
		JSONObject defaultFood = new JSONObject();
		defaultFood.put("NAME", "该商品已删除");
		defaultFood.put("PRICE", "0");
		defaultFood.put("TYPE", "");
		defaultFood.put("UNIT", "");
		defaultFood.put("IMG", "");

		for (Object tObject : foods) {
			JSONObject object = (JSONObject) tObject;
			Record foodRecord = orderService.getSimpleFoodHistroy(object.getIntValue("id"));
			if (foodRecord == null) {
				defaultFood.put("COUNT", object.get("num"));
				foodsResult.add(defaultFood.clone());
			} else {
				foodRecord.set("COUNT", object.get("num"));
				foodsResult.add(JSONObject.parse(foodRecord.toJson()));
			}
		}
		setAttr("foodArray", foodsResult);
		// JSONObject result = new JSONObject();
		// result.put("draw", params.getDraw());
		// result.put("recordsTotal", foods.getTotalRow());
		// List<Record> list = foods.getList();
		// result.put("data", list);
		// result.put("recordsFiltered", foods.getTotalRow());
		// renderJson(result);
		render("view.html");
	}

	public void getData() {
		String showType = getPara("showType");
		DTParams params = new DTParams(getParaMap());
		Page<Record> orders = null;
		switch (showType) {
		case "all":
			orders = orderService.getDTPage(params, "order.listAll");
			break;
		case "active":
			orders = orderService.getDTPage(params, "order.listActive");
			break;
		default:
			renderText("error");
			return;
		}
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", orders.getTotalRow());
		List<Record> list = orders.getList();
		for (Record record : list) {
			if (record.getInt("CANCEL_STATE") != null
					&& (record.getInt("CANCEL_STATE") == 1 || record.getInt("CANCEL_STATE") == 3)) {
				System.out.println(orderService.getCode("t_order.ORDER_STATE", record.getInt("ORDER_STATE")));
				record.set("STATESTRING", record.getStr("STATESTRING") + ","
						+ orderService.getCode("t_order.ORDER_STATE", record.getInt("ORDER_STATE")));
			}
		}
		result.put("data", list);
		result.put("recordsFiltered", orders.getTotalRow());
		renderJson(result);
	}

	@Before(WxApiConfigInterceptor.class)
	public void accept() {
		Order order = Order.dao.findById(getParaToInt("id"));
		if (order.getInt("ORDER_STATE") != 2) {
			renderText("订单状态错误:" + orderService.getCode("t_order.ORDER_STATE", order.getInt("ORDER_STATE")));
			return;
		}
		if (order.getInt("PAY_STATE") != 1 && order.getInt("PAY_STATE") != -1) {
			renderText("支付状态错误:" + orderService.getCode("t_order.PAY_STATE", order.getInt("PAY_STATE")));
			return;
		}
		if (order.getInt("CANCEL_STATE") != null
				&& (order.getInt("CANCEL_STATE") == 0 || order.getInt("CANCEL_STATE") == 2)) {
			renderText("退款状态错误:" + orderService.getCode("t_order.CANCEL_STATE", order.getInt("CANCEL_STATE")));
			return;
		}
		order.set("ORDER_STATE", 3);
		order.set("TAKING_TIME", new Date());
		order.update();
		try {
			WeiXinUtils.sendOrderVideoAndMess(order);
		} catch (Exception exception) {
			exception.printStackTrace();
			int shopId = order.getInt("SHOP_ID");
			String tel = Db.findFirst("SELECT TEL FROM T_SHOP WHERE ID=?", shopId).getStr("TEL");
			renderText("接单成功啦~~~o(*￣▽￣*)ブ,<br>但是发送微信消息失败，<br>请电话通知：" + tel);
			return;
		}
		renderText("接单成功啦~~~o(*￣▽￣*)ブ");
	}

	public void refund() {
		int orderId = getParaToInt("id");
		Order order = Order.dao.findById(orderId);
		if (order.getStr("REFUND_NO") == null) {
			order.set("REFUND_NO", UUID.randomUUID().toString().replace("-", ""));
			order.update();
		}
		double tempPayPrice = order.getDouble("PAY_PRICE") * 100;
		int payPrice = (int) tempPayPrice;
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("appid", PropKit.get("appId"));
		paraMap.put("mch_id", PropKit.get("mch_id"));
		paraMap.put("nonce_str", WeiXinUtils.getNonceNorString());
		paraMap.put("sign_type", "MD5");
		paraMap.put("out_trade_no", orderId + "");
		paraMap.put("out_refund_no", order.getStr("REFUND_NO"));
		paraMap.put("total_fee", payPrice + "");
		paraMap.put("refund_fee", payPrice + "");
		paraMap.put("refund_desc", URLEncoder.encode("order refund"));
		paraMap.put("sign", WeiXinUtils.getSign(paraMap));
		// paraMap.put("refund_desc", "订单退款");
		String para = new String(WeiXinUtils.callMapToXML(paraMap));
		String urlResult = null;
		try {
			urlResult = ClientCustomSSL.post(WEIXIN_REFUND_URL, para);
		} catch (Exception exception) {
			renderText("接口调用失败");
		}
		System.out.println(urlResult);
		Document doc = Jsoup.parse(urlResult);
		if ("SUCCESS".equals(doc.getElementsByTag("result_code").text())
				|| "订单已全额退款".equals(doc.select("err_code_des").text())) {
			renderText("退款成功！");
			order.set("PAY_STATE", -2);
			order.set("CANCEL_STATE", 2);
			order.set("FINISH_CANCEL_TIME", new Date());
			order.update();
			return;
		}
		order.set("PAY_STATE", -1);
		order.set("CANCEL_STATE", 1);
		order.update();
		if (doc.select("err_code_des").size() == 0)
			renderText("退款失败：" + doc.select("return_msg").text());
		else
			renderText("退款失败：" + doc.select("err_code_des").text());
	}

	public void rejectRefund() {
		int orderId = getParaToInt("id");
		Order order = Order.dao.findById(orderId);
		order.set("CANCEL_STATE", 1);
		order.set("CANCEL_SHOP_REASON", getPara("rejectReason"));
		order.set("FINISH_CANCEL_TIME", new Date());
		order.update();
		renderJson(new JSONSuccess("成功"));
	}

}