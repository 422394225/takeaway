/**
 * Created At 2017年4月9日下午1:55:53 
 * 
 */

package core.admin.controller.tools;

import java.awt.Desktop;
import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

import core.interceptor.WxApiConfigInterceptor;
import core.temple.AuditResultTemple;
import core.utils.BaiduSpeechUtil;
import core.utils.ClientCustomSSL;
import core.utils.GoeasyUtil;
import core.utils.SMSUtils;
import core.utils.WeiXinUtils;
import core.validate.PhoneValidate;
import core.vo.JSONError;
import core.vo.JSONSuccess;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月9日下午1:55:53
 */
public class ToolsController extends Controller {

	public void index() {
		render("index.html");
	}

	public void reboot() {
		String filePath = URLDecoder.decode(getClass().getClassLoader().getResource("reboot.bat").getPath());
		File rebootFile = new File(filePath);
		if (!rebootFile.exists()) {
			renderJson(new JSONError("重启脚本损坏"));
			return;
		}
		try {
			Desktop.getDesktop().open(rebootFile);
			renderJson(new JSONSuccess("重启成功，请等待后刷新"));
		} catch (Exception e) {
			renderJson(new JSONError("调起重启失败"));
		}
	}

	@Before(WxApiConfigInterceptor.class)
	public void sendMess() {
		// Order order = Order.dao.findById(107);
		// WeiXinUtils.sendOrderVideoAndMess(order);
		// renderJson(new JSONSuccess("发送成功"));

		BaiduSpeechUtil.synthesisToFile("新订单来啦！", "order-2.mp3");
		renderJson(new JSONSuccess("发送成功"));
	}

	public void goeasyTest() {
		GoeasyUtil.sendOrderMess();
		renderJson(new JSONSuccess("发送成功"));
	}

	public void goeasyDeliveryTest() {
		GoeasyUtil.sendOrderAcceptMess();
		renderJson(new JSONSuccess("发送成功"));
	}

	@Before(WxApiConfigInterceptor.class)
	public void templeTest() {
		AuditResultTemple auditResultTemple = new AuditResultTemple();
		auditResultTemple.touser = "okQ2Rw9_v9M-Rb1fXW5KoMLrQCOw";
		auditResultTemple.first = "下单成功";
		auditResultTemple.keyword1 = "餐厅1\n1";
		auditResultTemple.keyword2 = "2017-9-13";
		auditResultTemple.keyword3 = "红烧鱼1";
		auditResultTemple.keyword4 = "12元";
		auditResultTemple.remark = "请等待接单";
		renderJson(new JSONSuccess(auditResultTemple.sendAndBackMsg()));
	}

	public void cutter() {
		setAttr("ratio", getPara("ratio"));
		render("cutter.html");
	}

	/**
	 * <xml><br>
	 * <mch_appid>wxe062425f740c30d8</mch_appid><br>
	 * <mchid>10000098</mchid><br>
	 * <nonce_str>3PG2J4ILTKCH16CQ2502SI8ZNMTM67VS</nonce_str><br>
	 * <partner_trade_no>100000982014120919616</partner_trade_no><br>
	 * <openid>ohO4Gt7wVPxIT1A9GjFaMYMiZY1s</openid><br>
	 * <check_name>FORCE_CHECK</check_name><br>
	 * <re_user_name>张三</re_user_name><br>
	 * <amount>100</amount><br>
	 * <desc>节日快乐!</desc><br>
	 * <spbill_create_ip>10.2.3.10</spbill_create_ip><br>
	 * <sign>C97BDBACF37622775366F38B629F45E3</sign><br>
	 * </xml>
	 * 
	 * 
	 */
	public void payToShop() {
		Map<String, String> paraMap = new HashMap<>();
		// paraMap.put("appid", PropKit.get("appId"));
		paraMap.put("mchid", PropKit.get("mch_id"));
		paraMap.put("mch_appid", PropKit.get("appId"));
		paraMap.put("nonce_str", WeiXinUtils.getNonceNorString());
		// paraMap.put("sign_type", "MD5");
		paraMap.put("partner_trade_no", "2");
		paraMap.put("openid", "oNCcWxDAdFm-PsfV9h6yWfoFoirw");
		paraMap.put("check_name", "NO_CHECK");
		paraMap.put("amount", "1800");
		paraMap.put("desc", URLEncoder.encode("订单：1"));
		paraMap.put("spbill_create_ip", PropKit.get("spbill_create_ip"));
		paraMap.put("sign", WeiXinUtils.getSign(paraMap));
		String para = new String(WeiXinUtils.callMapToXML(paraMap));
		String urlResult = null;
		try {
			urlResult = ClientCustomSSL.post("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers",
					para);
		} catch (Exception exception) {
			renderText("接口调用失败");
		}
		System.out.println(urlResult);
		Document doc = Jsoup.parse(urlResult);
		renderJson(new JSONError(doc.getElementsByTag("return_msg").text()));
	}

	@Before(PhoneValidate.class)
	public void sendCode() {
		String phone = getPara("phone");
		JSONObject jsonObject = SMSUtils.send(phone);
		if (jsonObject.size() == 0) {
			renderJson(new JSONSuccess("发送成功！"));
		} else {
			renderJson(new JSONError());
		}
	}

	public void getLocation() {
		setAttr("key", PropKit.get("amap.key"));
		render("map.html");
	}

}
