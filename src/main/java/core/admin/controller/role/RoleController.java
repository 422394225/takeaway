/**
 * Created At 2017年1月30日下午2:29:33 
 * 
 */

package core.admin.controller.role;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import core.admin.service.role.RoleService;
import core.admin.service.role.impl.RoleServiceImpl;
import core.model.Role;
import core.utils.StringTool;
import core.utils.WebUtils;
import core.vo.DTParams;
import core.vo.JSONError;
import core.vo.JSONSuccess;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:29:33
 */

public class RoleController extends Controller {
	private Log log = Log.getLog(RoleController.class);
	private RoleService roleService = new RoleServiceImpl();

	public void index() {
		render("list.html");
	}

	public void getData() {
		DTParams params = new DTParams(getParaMap());
		Page<Record> roles = roleService.getDTPage(params, "role.list");
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", roles.getTotalRow());
		List<Record> list = roles.getList();
		result.put("data", list);
		result.put("recordsFiltered", roles.getTotalRow());
		renderJson(result);
	}

	public void add() {
		render("add.html");
	}

	public void edit() {
		String id = getPara("id");
		Role role = Role.dao.findById(id);
		setAttr("role", role);
		render("add.html");
	}

	public void view() {
		Role role = Role.dao.findById(getPara("id"));
		setAttr("role", role);
		render("view.html");
	}

	public void save() {
		String name = getPara("name");
		String power = getPara("power");
		Role role = new Role();
		String id = getPara("id");
		role.set("NAME", name);
		role.set("POWER", power);
		if (StringUtils.isNotEmpty(id)) {
			role.set("ID", id);
			role.update();
		} else {
			role.set("ID", StringTool.getUUID());
			role.save();
		}
		WebUtils.refreshSession(this);
		renderJson(new JSONSuccess());
	}

	public void remove() {
		String id = getPara("id");
		boolean isDeleted = Role.dao.deleteById(id);
		if (isDeleted) {
			renderJson(new JSONSuccess("删除成功"));
		} else {
			renderJson(new JSONError("删除失败，该条记录不存在", id));
		}
	}
}
