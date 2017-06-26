package core.admin.service.menu.impl;

import com.jfinal.plugin.activerecord.Db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import core.admin.service.base.impl.DataTableServiceImpl;
import core.admin.service.menu.MenuService;
import core.model.Admin;
import core.model.Menu;
import core.model.Role;
import core.vo.ConditionsVO;

public class MenuServiceImpl extends DataTableServiceImpl implements MenuService {
	public List<Menu> getMenuByPower(Admin admin) {
		List<Menu> menus = new ArrayList<>();
		ConditionsVO conditionsVO = new ConditionsVO();
		if ("1".equals(admin.getStr("ID"))) {
			menus = Menu.dao.find(Db.getSql("menu.list", conditionsVO.getConditions()));
		} else {
			Map<String, String> map = new HashMap<>();
			String roleId = admin.get("ROLE_ID");
			if (StringUtils.isNoneEmpty(roleId)) {
				Role role = Role.dao.findById(roleId);
				String power = role.get("POWER");
				if (StringUtils.isNoneBlank(power)) {
					String[] powers = power.split(";");
					String inSql = "(";
					for (String p : powers) {
						inSql += "'" + p + "',";
					}
					inSql = inSql.substring(0, inSql.length() - 1);
					inSql += ")";
					map.put("ID", " IN " + inSql);
					conditionsVO.getLimitCond().putAll(map);
					menus = Menu.dao.find(Db.getSql("menu.list", conditionsVO.getConditions()));
				}
			}
		}
		return menus;
	}
}
