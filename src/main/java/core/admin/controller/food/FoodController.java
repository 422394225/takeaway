package core.admin.controller.food;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;

import core.admin.controller.log.LogController;
import core.admin.service.food.impl.FoodServiceImpl;
import core.admin.service.log.impl.LogServiceImpl;
import core.interceptor.AdminLoginInterceptor;
import core.interceptor.PowerInterceptor;
import core.model.Data;
import core.model.Food;
import core.utils.SecurityCodeTool;
import core.vo.DTParams;
import core.weixin.api.InterfaceAnalysisApi;

/**
 * 
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:40:27
 */
public class FoodController extends Controller {

	private Log log = Log.getLog(FoodController.class);
	private FoodServiceImpl foodServiceImpl = new FoodServiceImpl();
	public void index() {
		render("list.html");
	}
	public void getData() {
		DTParams params = new DTParams(getParaMap());
		Page<Record> foods = foodServiceImpl.getDTPage(params, "food.list");
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", foods.getTotalRow());
		List<Record> list = foods.getList();
		result.put("data", list);
		result.put("recordsFiltered", foods.getTotalRow());
		renderJson(result);
	}
	public void histroy(){
		setAttr("GOOD_NAME", getPara("GOOD_NAME"));
		setAttr("SHOP_ID", getPara("SHOP_ID"));
		render("histroy.html");
	}
	public void getHistroyData(){
		DTParams params = new DTParams(getParaMap());
		Map<String, Object> limitCondPara = new HashMap<>();
		limitCondPara.put("name", "='"+getPara("GOOD_NAME")+"'");
		limitCondPara.put("shop_id", "="+getPara("SHOP_ID"));
		Map<String, Map<String, Object>> allParap = new HashMap<>();
		allParap.put("limitCond", limitCondPara);
		Page<Record> foods = foodServiceImpl.getDTPage(params, "food.histroy",allParap);
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", foods.getTotalRow());
		List<Record> list = foods.getList();
		result.put("data", list);
		result.put("recordsFiltered", foods.getTotalRow());
		renderJson(result);
	}
}