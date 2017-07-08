package core.common.mapping;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

import core.model.Admin;
import core.model.Customer;
import core.model.Data;
import core.model.Feedback;
import core.model.Log;
import core.model.Menu;
import core.model.Role;
import core.model.Shop;
import core.model.ShopType;
import core.model.ShopTypeRelation;
import core.model.User;

public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("t_admin", "ID", Admin.class);
		arp.addMapping("t_feedback", "ID", Feedback.class);
		arp.addMapping("t_log", "ID", Log.class);
		arp.addMapping("t_role", "ID", Role.class);
		arp.addMapping("t_user", "ID", User.class);
		arp.addMapping("t_customer", "ID", Customer.class);
		arp.addMapping("t_menu", "ID", Menu.class);
		arp.addMapping("t_data", "ID", Data.class);
		arp.addMapping("t_shop", "ID", Shop.class);
		arp.addMapping("t_shop_type", "ID", ShopType.class);
		arp.addMapping("t_shop_type_relation", "ID", ShopTypeRelation.class);
	}
}
