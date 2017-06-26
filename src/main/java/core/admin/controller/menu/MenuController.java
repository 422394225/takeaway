package core.admin.controller.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

import core.admin.service.menu.MenuService;
import core.admin.service.menu.impl.MenuServiceImpl;
import core.interceptor.LogInterceptor;
import core.interceptor.PowerInterceptor;
import core.model.Admin;
import core.model.Menu;
import core.model.Role;
import core.vo.JSONError;
import core.vo.JSONSuccess;
import core.vo.MenuVO;
@Clear({PowerInterceptor.class,LogInterceptor.class})
public class MenuController extends Controller {
	private Log log = Log.getLog(MenuController.class);
	private MenuService menuService = new MenuServiceImpl();

	public void getData() {
		Admin admin = getSessionAttr("administrator");
		List<Menu> menus = CacheKit.get("menuCache", admin.get("ID") + "menus");
		if (menus == null) {
			CacheKit.put("menuCache", admin.get("ID") + "menus", menuService.getMenuByPower(admin));
			menus = CacheKit.get("menuCache", admin.get("ID") + "menus");
		}
		
		renderJson(menus);
	}

	public void getMenuTree() {
		try {
			String roleId = getPara("roleId");
			if (StringUtils.isNotEmpty(roleId)) {
				Role role = Role.dao.findById(roleId);
				String power = role.get("POWER");
				Map<String, String> powerMap = new HashMap<>();
				if (StringUtils.isNoneBlank(power)) {
					String[] powers = power.split(";");
					for (String p : powers) {
						if (StringUtils.isNoneEmpty(p)) {
							powerMap.put(p, "1");
						}
					}
				}
				renderJson(new JSONSuccess("", getChildren(getMenu(), powerMap)));
			} else {
				renderJson(new JSONSuccess("", getChildren(getMenu())));
			}
		} catch (Exception e) {
			renderJson(new JSONError("加载失败！"));
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private MenuVO getMenu() {
		List<Record> menus = Db.find(Db.getSql("menu.list", new HashMap<>()));
		Map<String, MenuVO> map = new HashMap<>();
		for (Record record : menus) {
			String id = record.getStr("ID");
			MenuVO item = map.get(id);
			if (item == null) {
				item = new MenuVO(record.getColumns());
				map.put(id, item);
			} else {
				item.data = record.getColumns();
			}
			String parentId = record.getStr("PARENT_ID");
			if (StringUtils.isEmpty(parentId)) {
				continue;
			}
			MenuVO parent = (MenuVO) map.get(parentId);
			if (parent == null) {
				parent = new MenuVO(null);
				map.put(parentId, parent);
			}
			parent.chidren.add(item);
		}
		return map.get("0");
	}

	public JSONArray getChildren(MenuVO menu) {
		return getChildren(menu, null);
	}

	public JSONArray getChildren(MenuVO menu, Map<String, String> powerMap) {
		JSONArray tree = new JSONArray();
		Map<String, Object> nodesData = menu.data;
		List<MenuVO> children = menu.chidren;
		JSONObject leaf = new JSONObject();
		leaf.put("text", nodesData.get("NAME"));
		leaf.put("icon", nodesData.get("ICON"));
		String menuID = (String) nodesData.get("ID");
		leaf.put("menuId", menuID);
		if (powerMap != null) {
			if (powerMap.get(menuID) != null || (powerMap.size() > 0 && "0".equals(menuID))) {
				Map<String, Boolean> stateMap = new HashMap<>();
				stateMap.put("selected", true);
				leaf.put("state", stateMap);
			}
		}
		if (children.size() > 0) {
			JSONArray nodes = new JSONArray();
			for (MenuVO m : children) {
				JSONArray childArray = getChildren(m, powerMap);
				nodes.addAll(childArray);
			}
			leaf.put("nodes", nodes);
		}
		tree.add(leaf);
		return tree;
	}
}
