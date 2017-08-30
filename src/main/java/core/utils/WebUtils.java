package core.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

import core.admin.service.menu.MenuService;
import core.admin.service.menu.impl.MenuServiceImpl;
import core.model.Admin;
import core.utils.EmailUtils.MailData;

/**
 * Web相关工具类
 * 
 * @author Javen 2016年4月2日
 */
public final class WebUtils {

	private WebUtils() {
	}

	/**
	 * 密码:md5hex
	 * 
	 * @param pwd
	 * @return
	 */
	public static String pwdEncode(String password) {
		return HashKit.md5(password);
	}

	/**
	 * 读取cookie
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public static String getCookie(HttpServletRequest request, String key) {
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (key.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 清除 某个指定的cookie
	 * 
	 * @param response
	 * @param key
	 */
	public static void removeCookie(HttpServletResponse response, String key) {
		setCookie(response, key, null, 0);
	}

	/**
	 * 设置cookie
	 * 
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAgeInSeconds
	 */
	public static void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(maxAgeInSeconds);
		// 指定为httpOnly保证安全性
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}

	/**
	 * 获取浏览器信息
	 * 
	 * @param Controller
	 * @return String
	 */
	public static String getUserAgent(Controller c) {
		return getUserAgent(c.getRequest());
	}

	/**
	 * 获取浏览器信息
	 * 
	 * @param HttpServletRequest
	 * @return String
	 */
	public static String getUserAgent(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}

	/**
	 * 获取ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getIP(Controller c) {
		HttpServletRequest request = c.getRequest();
		return getIP(request);
	}

	/**
	 * 获取ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Requested-For");
		if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public enum SendEmialType {
		REGISTER, FORGET
	}

	/**
	 * 邮箱发送验证码
	 * 
	 * @param email
	 * @param code
	 * @param sendEmialType
	 * @return
	 */
	public static boolean sendMailCode(String email, String code, SendEmialType sendEmialType) {
		String type = sendEmialType.name().toLowerCase();
		if (type.equals("register")) {
			return sendMail(email, "Javen微信开发指南", "【Javen微信开发指南】您本次注册的验证码是:" + code + "，感谢您的使用！如非本人操作，请忽略。");
		} else if (type.equals("forget")) {
			return sendMail(email, "Javen微信开发指南", "【Javen微信开发指南】您本次账号找回密码的验证码是:" + code + "，感谢您的使用！如非本人操作，请忽略。");
		}
		return false;
	}

	/**
	 * 意见反馈
	 * 
	 * @param email
	 * @param contact
	 * @param account
	 * @param idea
	 * @return
	 */
	public static boolean sendIdeaMail(String email, String contact, String account, String idea) {
		return sendMail(email, "Javen微信开发指南-意见反馈",
				"【Javen微信开发指南-意见反馈】联系方式:" + contact + "，  " + account + "<br/>反馈的意见：" + idea);
	}

	public static void sendOrderMail(String email, String nickName, String couresName, String userMobile,
			String courseCount, String imageUrl) {
		StringBuffer sbf = new StringBuffer();
		sbf.append("订单通知：").append(nickName).append(" 已下单,购买课程为:《").append(couresName).append("》").append("课时数：")
				.append(courseCount).append(",openId:").append(userMobile).append(",图像地址:").append(imageUrl);
		sendAsynMail(email, "Javen微信开发指南-订单通知", sbf.toString());
	}

	/**
	 * 异步发送Email
	 * 
	 * @param email
	 * @param subject
	 * @param content
	 */
	public static void sendAsynMail(String email, String subject, String content) {
		EmailUtils.asynMail(MailData.New().subject(subject).content(content).to(email));
	}

	/**
	 * 同步发送Email
	 * 
	 * @param email
	 * @param subject
	 * @param content
	 * @return
	 */
	public static boolean sendMail(String email, String subject, String content) {
		return EmailUtils.sendMail(
				MailData.New().subject(subject).content(content).to(email).cc(PropKit.get("notify_email_test")));
	}

	public static void refreshSession(Controller controller) {
		refreshSession(null, controller);
	}

	public static void refreshSession(Admin admin, Controller controller) {
		Admin sessionAdmin = controller.getSessionAttr("administrator");
		if (admin == null) {
			admin = sessionAdmin;
		}
		if (sessionAdmin == null || sessionAdmin.get("ID").equals(admin.get("ID"))) {
			controller.removeSessionAttr("administrator");
			controller.setSessionAttr("administrator", admin);
		}
		MenuService menuService = new MenuServiceImpl();
		CacheKit.put("menuCache", admin.get("ID") + "menus", menuService.getMenuByPower(admin));
	}

}
