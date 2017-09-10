/**
 * Created At 2017年1月30日下午2:29:33 
 * 
 */

package core.admin.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import core.admin.service.user.UserService;
import core.admin.service.user.impl.UserServiceImpl;
import core.model.User;
import core.vo.ConditionsVO;
import core.vo.DTParams;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:29:33
 */

public class UserController extends Controller {
	private Log log = Log.getLog(UserController.class);
	private UserService userService = new UserServiceImpl();

	public void index() {
		render("list.html");
	}

	public void unsubList() {
		render("list-unsub.html");
	}

	public void getSubData() {
		Map<String, Map<String, Object>> map = new HashMap<>();
		Map<String, Object> where = new HashMap<>();
		where.put("SUBSCRIBE", "= 1");
		map.put("limitCond", where);
		getData(map);
	}

	public void getUnSubData() {
		Map<String, Map<String, Object>> map = new HashMap<>();
		Map<String, Object> where = new HashMap<>();
		where.put("SUBSCRIBE", "= 0");
		map.put("limitCond", where);
		getData(map);
	}

	public void lookAddress() {
		String userId = getPara("userId");
		List<Record> records = Db
				.find("SELECT ADDRESS,`NAME`,TEL,`DEFAULT` FROM T_USER_ADDRESS WHERE USERID=? AND DELETED=0", userId);
		JSONArray resultArray = new JSONArray();
		for (Record record : records) {
			resultArray.add(JSONObject.parse(record.toJson()));
		}
		setAttr("addr", resultArray);
		render("lookAddress.html");
	}

	private void getData(Map<String, Map<String, Object>> map) {
		DTParams params = new DTParams(getParaMap());
		Page<Record> users = userService.getDTPage(params, "user.list", map);
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", users.getTotalRow());
		List<Record> list = users.getList();
		result.put("data", list);
		result.put("recordsFiltered", users.getTotalRow());
		renderJson(result);
	}

	public void view() {
		String id = getPara("id");
		ConditionsVO conditionsVO = new ConditionsVO();
		Map<String, String> map = new HashMap<>();
		map.put("ID", " = '" + id + "'");
		conditionsVO.getLimitCond().putAll(map);
		User user = User.dao.findFirst(Db.getSqlPara("user.list", conditionsVO.getConditions()));
		setAttr("user", user);
		render("view.html");
	}
}
