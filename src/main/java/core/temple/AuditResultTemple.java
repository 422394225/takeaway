package core.temple;

import com.alibaba.fastjson.JSONObject;

/**
 * {{first.DATA}}<br>
 * 餐厅：{{keyword1.DATA}}<br>
 * 下单时间：{{keyword2.DATA}}<br>
 * 菜品：{{keyword3.DATA}}<br>
 * 金额：{{keyword4.DATA}}<br>
 * {{remark.DATA}}<br>
 * 
 * @author jerry
 *
 */
public class AuditResultTemple extends TempleModel {
	public String first;
	public String keyword1;
	public String keyword2;
	public String keyword3;
	public String keyword4;
	public String remark;

	@Override
	public JSONObject getData() {
		JSONObject object = new JSONObject();
		object.put("first", getDataObject(first, "#000000"));
		object.put("keyword1", getDataObject(keyword1));
		object.put("keyword2", getDataObject(keyword2));
		object.put("keyword3", getDataObject(keyword3));
		object.put("keyword4", getDataObject(keyword4));
		object.put("remark", getDataObject(remark, "#000000"));
		return object;
	}

}
