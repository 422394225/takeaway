package core.temple;

import com.alibaba.fastjson.JSONObject;

/**
 * {{first.DATA}}<br>
 * 
 * 商店名称：{{storeName.DATA}}<br>
 * 预订时间：{{bookTime.DATA}} <br>
 * 订单编号：{{orderId.DATA}}<br>
 * 订单类型：{{orderType.DATA}}<br>
 * {{remark.DATA}}
 * 
 * @author jerry
 *
 */
public class OrderCreateTemple extends TempleModel {
	public String first;
	public String storeName;
	public String bookTime;
	public String orderId;
	public String orderType;
	public String remark;

	@Override
	public JSONObject getData() {
		JSONObject object = new JSONObject();
		object.put("first", getDataObject(first, "#000000"));
		object.put("storeName", getDataObject(storeName));
		object.put("bookTime", getDataObject(bookTime));
		object.put("orderId", getDataObject(orderId));
		object.put("orderType", getDataObject(orderType));
		object.put("remark", getDataObject(remark, "#000000"));
		return object;
	}

}
