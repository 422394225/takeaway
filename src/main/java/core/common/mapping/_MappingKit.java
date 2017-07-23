package core.common.mapping;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

import core.model.Admin;
import core.model.Data;
import core.model.DictCode;
import core.model.Discount;
import core.model.Feedback;
import core.model.Food;
import core.model.FoodHistroy;
import core.model.FoodType;
import core.model.Log;
import core.model.Menu;
import core.model.Order;
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
	}
}
