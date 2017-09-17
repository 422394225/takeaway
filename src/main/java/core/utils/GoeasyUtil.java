package core.utils;

import com.jfinal.kit.PropKit;

import io.goeasy.GoEasy;

public class GoeasyUtil {
	/**
	 * 客户端使用，仅可订阅
	 */
	public static final String SUBSCRIBE_KEY = PropKit.get("goeasy.SubscribeKey");
	/**
	 * 服务器使用，可发布消息和订阅
	 */
	public static final String COMMON_KEY = PropKit.get("goeasy.CommonKey");
	/**
	 * 服务器地址
	 */
	public static final String REST_HOST = PropKit.get("goeasy.RESTHost");
	/**
	 * 客户端
	 */
	private static GoEasy GO_EASY;

	private static synchronized void initGoEasy() {
		if (GO_EASY != null)
			return;
		GO_EASY = new GoEasy(REST_HOST, COMMON_KEY);
	}

	public static void sendOrderMess() {
		if (GO_EASY == null)
			initGoEasy();
		GO_EASY.publish("ORDER_ALERT_ADMIN", "有新订单啦");
	}

	public static void sendOrderAcceptMess() {
		if (GO_EASY == null)
			initGoEasy();
		GO_EASY.publish("ORDER_ALERT_DELIVERY", "有新订单啦");
	}
}
