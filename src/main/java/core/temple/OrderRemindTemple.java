package core.temple;

import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.TemplateData;
import core.model.Order;
import core.model.Shop;

/**
 * 催单模板
 * {{first.DATA}}
 *订餐人：{{keyword1.DATA}}
 *订单号：{{keyword2.DATA}}
 *下单时间：{{keyword3.DATA}}
 *订餐数量：{{keyword4.DATA}}
 *消费金额：{{keyword5.DATA}}
 {{remark.DATA}}
 * 
 * @author weifg
 *
 */
public class OrderRemindTemple{

	public static TemplateData setData(Order order){
		TemplateData templateData = TemplateData.New();
		templateData.setTemplate_id(PropKit.get("temple.userRemindShop"));
		templateData.setTopcolor("#000");
		Shop shop = Shop.dao.findById(order.getInt("SHOP_ID"));
		templateData.setTouser(shop.getStr("OPENID"));
//		templateData.setUrl("");
		return templateData;
	}

}
