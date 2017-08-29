package core.vo;

import org.jsoup.nodes.Element;

public class OrderPayResult {
	/** 微信分配的公众账号ID（企业号corpid即为此appId） */
	public String appid;
	/** 微信支付分配的商户号 */
	public String mch_id;
	/** 微信支付分配的终端设备号， */
	public String device_info;
	/** 随机字符串，不长于32位 */
	public String nonce_str;
	/** 签名，详见签名算法 */
	public String sign;
	/** 签名类型，目前支持HMAC-SHA256和MD5，默认为MD5 */
	public String sign_type;
	/** SUCCESS/FAIL */
	public String result_code;
	/** 错误返回的信息描述 */
	public String err_code;
	/** 错误返回的信息描述 */
	public String err_code_des;
	/** 用户在商户appid下的唯一标识 */
	public String openid;
	/** 用户是否关注公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效 */
	public String is_subscribe;
	/** JSAPI、NATIVE、APP */
	public String trade_type;
	/** 银行类型，采用字符串类型的银行标识，银行类型见银行列表 */
	public String bank_type;
	/** 订单总金额，单位为分 */
	public Integer total_fee;
	/** 应结订单金额=订单金额-非充值代金券金额，应结订单金额<=订单金额。 */
	public Integer settlement_total_fee;
	/** 货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型 */
	public String fee_type;
	/** 现金支付金额订单现金支付金额，详见支付金额 */
	public Integer cash_fee;
	/** 货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型 */
	public String cash_fee_type;
	/** 代金券金额<=订单金额，订单金额-代金券金额=现金支付金额，详见支付金额 */
	public Integer coupon_fee_all;
	/** 代金券使用数量 */
	public Integer coupon_count;
	/**
	 * CASH--充值代金券 NO_CASH---非充值代金券
	 * 并且订单使用了免充值券后有返回（取值：CASH、NO_CASH）。$n为下标,从0开始编号，举例：coupon_type_0
	 */
	public String[] coupon_type;
	/** 代金券ID,$n为下标，从0开始编号 */
	public String[] coupon_id;
	/** 单个代金券支付金额,$n为下标，从0开始编号 */
	public Integer[] coupon_fee;
	/** 微信支付订单号 */
	public String transaction_id;
	/** 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 */
	public String out_trade_no;
	/** 商家数据包，原样返回 */
	public String attach;
	/**
	 * 支付完成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
	 */
	public String time_end;

	public OrderPayResult(Element xmlElement) {
		try {
			this.appid = xmlElement.getElementsByTag("appid").first().text();
		} catch (Exception exception) {
		}
		try {
			this.mch_id = xmlElement.getElementsByTag("mch_id").first().text();
		} catch (Exception exception) {
		}
		try {
			this.device_info = xmlElement.getElementsByTag("device_info").first().text();
		} catch (Exception exception) {
		}
		try {
			this.nonce_str = xmlElement.getElementsByTag("nonce_str").first().text();
		} catch (Exception exception) {
		}
		try {
			this.sign = xmlElement.getElementsByTag("sign").first().text();
		} catch (Exception exception) {
		}
		try {
			this.sign_type = xmlElement.getElementsByTag("sign_type").first().text();
		} catch (Exception exception) {
		}
		try {
			this.result_code = xmlElement.getElementsByTag("result_code").first().text();
		} catch (Exception exception) {
		}
		try {
			this.err_code = xmlElement.getElementsByTag("err_code").first().text();
		} catch (Exception exception) {
		}
		try {
			this.err_code_des = xmlElement.getElementsByTag("err_code_des").first().text();
		} catch (Exception exception) {
		}
		try {
			this.openid = xmlElement.getElementsByTag("openid").first().text();
		} catch (Exception exception) {
		}
		try {
			this.is_subscribe = xmlElement.getElementsByTag("is_subscribe").first().text();
		} catch (Exception exception) {
		}
		try {
			this.trade_type = xmlElement.getElementsByTag("trade_type").first().text();
		} catch (Exception exception) {
		}
		try {
			this.bank_type = xmlElement.getElementsByTag("bank_type").first().text();
		} catch (Exception exception) {
		}
		try {
			this.fee_type = xmlElement.getElementsByTag("").first().text();
		} catch (Exception exception) {
		}
		try {
			this.cash_fee_type = xmlElement.getElementsByTag("").first().text();
		} catch (Exception exception) {
		}
		try {
			this.transaction_id = xmlElement.getElementsByTag("transaction_id").first().text();
		} catch (Exception exception) {
		}
		try {
			this.out_trade_no = xmlElement.getElementsByTag("out_trade_no").first().text();
		} catch (Exception exception) {
		}
		try {
			this.attach = xmlElement.getElementsByTag("attach").first().text();
		} catch (Exception exception) {
		}
		try {
			this.time_end = xmlElement.getElementsByTag("time_end").first().text();
		} catch (Exception exception) {
		}
		try {
			String total_fee_string = xmlElement.getElementsByTag("total_fee").first().text();
			if (total_fee_string != null)
				this.total_fee = Integer.valueOf(total_fee_string);
		} catch (Exception exception) {
		}
		try {
			String settlement_total_fee_string = xmlElement.getElementsByTag("settlement_total_fee").first().text();
			if (settlement_total_fee_string != null)
				this.settlement_total_fee = Integer.valueOf(settlement_total_fee_string);
		} catch (Exception exception) {
		}
		try {
			String cash_fee_string = xmlElement.getElementsByTag("cash_fee").first().text();
			if (cash_fee_string != null)
				this.cash_fee = Integer.valueOf(cash_fee_string);
		} catch (Exception exception) {
		}
		try {
			String coupon_fee_all_string = xmlElement.getElementsByTag("coupon_fee_all").first().text();
			if (coupon_fee_all_string != null)
				this.coupon_fee_all = Integer.valueOf(coupon_fee_all_string);
		} catch (Exception exception) {
		}
		try {
			String coupon_count_string = xmlElement.getElementsByTag("coupon_count").first().text();
			if (coupon_count_string != null)
				this.coupon_count = Integer.valueOf(coupon_count_string);
		} catch (Exception exception) {
		}
	}
}
