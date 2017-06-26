package core.admin.service.menu;

import java.util.List;

import core.admin.service.base.DataTableService;
import core.model.Admin;
import core.model.Menu;

public interface MenuService extends DataTableService {
	List<Menu> getMenuByPower(Admin admin);
}
