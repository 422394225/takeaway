package core.common.mapping;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

import core.model.*;

public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("t_admin", "ID", Admin.class);
		arp.addMapping("t_feedback", "ID", Feedback.class);
		arp.addMapping("t_log", "ID", Log.class);
		arp.addMapping("t_role", "ID", Role.class);
		arp.addMapping("t_user", "ID", User.class);
		arp.addMapping("t_menu", "ID", Menu.class);
		arp.addMapping("t_data", "ID", Data.class);
		arp.addMapping("t_shop", "ID", Shop.class);
		arp.addMapping("t_shop_type", "ID", ShopType.class);
		arp.addMapping("t_shop_type_relation", "ID", ShopTypeRelation.class);
		arp.addMapping("t_dict_code", "ID", DictCode.class);
		arp.addMapping("t_food", "ID", Food.class);
		arp.addMapping("t_food_histroy", "ID", FoodHistroy.class);
		arp.addMapping("t_food_type", "ID", FoodType.class);
		arp.addMapping("t_discount", "ID", Discount.class);
		arp.addMapping("t_order", "ID", Order.class);
		arp.addMapping("t_delivery", "ID", Delivery.class);
		arp.addMapping("t_audit", "ID", Audit.class);
		arp.addMapping("t_banner", "ID", Banner.class);
		arp.addMapping("t_user_address", "ID", UserAddress.class);
	}
}
