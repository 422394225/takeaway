package core.validate.base;

import com.jfinal.validate.Validator;

/**
 * 通用校验器
 * @author Javen
 * 2016年4月2日
 */

public abstract class BaseValidator extends Validator {
	//校验手机号码正则表达式  
	private static final String PHONE_PATTERN = "^((00|\\+)?(86(?:-| )))?((\\d{11})|(\\d{3}[- ]{1}\\d{4}[- ]{1}\\d{4})|((\\d{2,4}[- ]){1}(\\d{7,8}|(\\d{3,4}[- ]{1}\\d{4}))([- ]{1}\\d{1,4})?))$";

	/**  
	 * 验证手机号码  
	 */
	protected void validatePhone(String field, String errorKey, String errorMsg) {
		validateRegex(field, PHONE_PATTERN, false, errorKey, errorMsg);
	}

	protected void validatePostRequired(String value, String errorKey, String errorMessage) {
		if (value == null || "".equals(value)) {
			this.addError(errorKey, errorMessage);
		}
	}
}
