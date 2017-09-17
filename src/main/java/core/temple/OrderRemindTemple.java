package core.temple;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
		//templateData.setTouser("ooGbdwiMIscdAG1OK7fzGGMIQqeM");
//		templateData.setUrl("");
		templateData.add("first","您有一个新的催单","#000000");
		templateData.add("keyword1",order.get("USER_NAME"),"#ff0000");
		templateData.add("keyword2",order.get("ID")+"","#000000");
		templateData.add("keyword3",(order.get("CREATE_TIME")+"").replace(".0",""),"#ff0000");
		JSONArray foods = JSONArray.parseArray(order.getStr("FOODS"));
		int count = 0;
		for(int i=0;i<foods.size();i++){
			JSONObject food = foods.getJSONObject(i);
			count += food.getInteger("num");
		}
		templateData.add("keyword4",count+"","#000000");
		templateData.add("keyword5",order.get("PAY_PRICE")+"","#000000");
		templateData.add("remark","客户已经等不及了哦，请及时处理.","#000000");
		return templateData;
	}

}
