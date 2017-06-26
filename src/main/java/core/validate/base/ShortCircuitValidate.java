package core.validate.base;

import com.jfinal.kit.PropKit;

import core.model.Admin;
import core.utils.DateUtils;
import core.utils.SecurityCodeTool;

/**
 * 短路校验器
 * @author Javen
 * 2016年4月2日
 */
public abstract class ShortCircuitValidate extends BaseValidator {
	{
		this.setShortCircuit(true);
	}

	protected void validateCode(String operatorIdFIED, String operateFIED, String codeFIED, String errorKey) {
		//解密
		String code = controller.getPara(codeFIED);
		String orign = "";
		try {
			orign = SecurityCodeTool.decrypt(code);
		} catch (Exception e) {
			addError(errorKey, "错误的code");
		}
		if (orign.indexOf("@") < 0) {
			addError(errorKey, "错误的code");
		}

		String[] orignArray = orign.split("@");

		String operatorId = controller.getPara(operatorIdFIED);
		String id = orignArray[0];
		if (!operatorId.equals(id)) {
			addError(errorKey, "请勿擅自更改url上的id");
		}
		Admin admin = Admin.dao.findById(id);
		if (admin == null) {
			addError(errorKey, "不存在的操作者");
		}

		String operate = controller.getPara(operateFIED);
		String action = orignArray[1];
		if (!operate.equals(action)) {
			addError(errorKey, "请勿擅自更改url上的action");
		}

		long beginTimestamp = Long.parseLong(orignArray[2].trim());
		long interval = PropKit.getLong("interval_" + action, (long) 5 * 60);
		if (DateUtils.isOvertime(beginTimestamp, interval)) {
			addError(errorKey, "未能在规定时间内完成该操作");//code超时
		}
	}
}
