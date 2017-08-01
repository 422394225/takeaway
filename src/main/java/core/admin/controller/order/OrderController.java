package core.admin.controller.order;

import java.util.List;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import core.admin.service.order.OrderService;
import core.admin.service.order.impl.OrderServiceImpl;
import core.vo.DTParams;

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
			JSONArray object = (JSONArray) tObject;
			Record foodRecord = orderService.getSimpleFoodHistroy(object.getIntValue(0));
			if (foodRecord == null) {
				defaultFood.put("COUNT", object.get(1));
				foodsResult.add(defaultFood.clone());
			} else {
				foodRecord.set("COUNT", object.get(1));
				foodsResult.add(JSONObject.parse(foodRecord.toJson()));
			}
		}
		System.out.println(foodsResult);
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
			if (record.getInt("CANCEL_STATE") != null)
				record.set("STATESTRING",
						Db.queryStr("select name from t_dict_code where field='t_order.CANCEL_STATE' and code=?",
								record.getInt("CANCEL_STATE")));
			else
				record.set("STATESTRING",
						Db.queryStr("select name from t_dict_code where field='t_order.ORDER_STATE' and code=?",
								record.getInt("ORDER_STATE")));
		}
		result.put("data", list);
		result.put("recordsFiltered", orders.getTotalRow());
		renderJson(result);
	}

	public void accept() {
		renderText(getPara("id"));
	}

	public void reject() {
		renderText(getPara("id"));
	}

}