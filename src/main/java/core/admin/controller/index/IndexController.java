package core.admin.controller.index;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;

import core.interceptor.AdminLoginInterceptor;
import core.interceptor.PowerInterceptor;
import core.utils.SecurityCodeTool;

/**
 * 
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:40:27
 */
public class IndexController extends Controller {

	public void index() {
		/*
		 * String JsonData=""; //获取昨天的日期 Data data = new Data(); Calendar cal =
		 * Calendar.getInstance(); cal.add(Calendar.DATE, -1); SimpleDateFormat
		 * format = new SimpleDateFormat("yyyy-MM-dd"); String date
		 * =format.format(cal.getTime()).toString(); //根据日期获取周几 周一到周日0-6 int w =
		 * cal.get(Calendar.DAY_OF_WEEK)-2; if (w==-1) { w=6; }
		 * 
		 * InterfaceAnalysisApi iApi = new InterfaceAnalysisApi();
		 * ApiConfigKit.setThreadLocalApiConfig(getApiConfig());
		 * 
		 * //用于存放七天数据 List<Data> list = new ArrayList<>();
		 * 
		 * //从数据库查询昨天数据，如果为空则从api获取 List<Data> yesterday =
		 * Data.dao.find("select * from t_data where REF_DATE= ?",date); if
		 * (yesterday.isEmpty()) { ApiResult apiResult =
		 * iApi.getInterfaceSummaryHour();
		 * 
		 * 
		 * List<Map<String, Object>> apiList = (List<Map<String, Object>>)
		 * apiResult.get("list");
		 * 
		 * if (!apiList.isEmpty()) { for (Map<String, Object> map : apiList) {
		 * data.set("ID", UUID.randomUUID().toString().replace("-", ""));
		 * data.set("REF_DATE",(String)map.get("ref_date"));
		 * data.set("REF_HOUR",(Integer)map.get("ref_hour")/100);
		 * data.set("WEEK", w);
		 * data.set("CALLBACK_COUNT",(Integer)map.get("callback_count"));
		 * data.save(); } }else { data.set("ID",
		 * UUID.randomUUID().toString().replace("-", "")); data.set("WEEK", w);
		 * data.set("REF_DATE",date); data.save(); }
		 * 
		 * list.clear();
		 * 
		 * }
		 * 
		 * //根据数据库每条信息的创建时间查询前一周的数据 list = Data.dao.
		 * find("SELECT * FROM t_data WHERE CREATE_TIME> DATE_ADD(NOW(),INTERVAL -6 DAY)"
		 * ); //初始化数据 Map<String ,String> rs = new TreeMap<>(); for(int
		 * i=0;i<7;i++){ for(int j=0;j<24;j++){
		 * rs.put(""+i+j,"["+i+","+j+",0]"); } } //处理数据 for(Data d:list){ String
		 * week =""+d.<Integer>get("WEEK"); String hour =
		 * ""+d.<Integer>get("REF_HOUR");
		 * rs.put(week+hour,"["+week+","+hour+","+d.<Integer>get(
		 * "CALLBACK_COUNT")+"]");
		 * 
		 * 
		 * } for(String key:rs.keySet()){ JsonData+=rs.get(key)+","; }
		 * JsonData="["+JsonData+"]";
		 * 
		 * setAttr("data", JsonData);
		 */
		render("index.html");
	}

	@Clear({ AdminLoginInterceptor.class, PowerInterceptor.class })
	public void login() {
		render("login.html");
	}

	@Clear({ AdminLoginInterceptor.class, PowerInterceptor.class })
	public void about() {
		render("about.html");
	}

	@Clear({ AdminLoginInterceptor.class, PowerInterceptor.class })
	public void error() {
		String type = getPara("type");
		if ("power".equals(type)) {
			setAttr("code", "401");
			setAttr("msg", "您不具有该URL的访问权限！");
		} else if ("oauth".equals(type)) {
			setAttr("code", "408");
			setAttr("msg", "微信授权失败！");
		} else if ("code".equals(type)) {
			setAttr("code", "");
			setAttr("msg", SecurityCodeTool.decrypt(getPara("msg")));
		} else {
			setAttr("msg", "");
			setAttr("code", "");
		}
		render("error.html");
	}
}