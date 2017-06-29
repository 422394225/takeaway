/**
 * Created At 2017年1月30日下午2:29:33 
 * 
 */

package core.admin.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.render.JsonRender;
import com.jfinal.upload.UploadFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import core.admin.service.admin.AdminService;
import core.admin.service.admin.impl.AdminServiceImpl;
import core.common.utils.QiniuUtils;
import core.interceptor.AdminLoginInterceptor;
import core.interceptor.PowerInterceptor;
import core.model.Admin;
import core.model.Role;
import core.utils.DateUtils;
import core.utils.MD5Util;
import core.utils.SecurityCodeTool;
import core.utils.StringTool;
import core.utils.WebUtils;
import core.validate.AdminValidate;
import core.validate.CodeValidate;
import core.validate.EmailValidate;
import core.validate.LoginValidate;
import core.validate.ModPassValidate;
import core.validate.SaveResetValidate;
import core.vo.DTParams;
import core.vo.JSONError;
import core.vo.JSONSuccess;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:29:33
 */

public class AdminController extends Controller {
	private Log log = Log.getLog(AdminController.class);
	private AdminService adminService = new AdminServiceImpl();
	private final static String ENCRIPT_KEY = PropKit.get("encrypt_key");

	@Clear({ AdminLoginInterceptor.class, PowerInterceptor.class })
	@Before(LoginValidate.class)
	public void login() {
		String username = getPara("username");
		String password = getPara("password");
		String remember = getPara("remember");
		Admin admin = adminService.findByUsername(username);
		if (admin != null) {
			if (admin.getStr("PASSWORD").equals(MD5Util.encrypt(password + ENCRIPT_KEY))) {
				setSessionAttr("administrator", admin);
				if (remember != null) {
					long now = System.currentTimeMillis();
					// 超时时间
					int maxAge = 60 * 60 * 24 * 7;
					// 构造cookie
					StringBuilder cookieBuilder = new StringBuilder().append(admin.get("ID").toString()).append("@")
							.append(now);

					// 加密cookie
					String userCookie = SecurityCodeTool.encrypt(cookieBuilder.toString());
					HttpServletResponse response = getResponse();

					String cookieKey = "administrator";
					WebUtils.setCookie(response, cookieKey, userCookie, maxAge);
				}
				setAttr("error", false);
			} else {
				setAttr("error", true);
				setAttr("msg", "密码错误");
			}
		} else {
			setAttr("error", true);
			setAttr("msg", "账号不存在");
		}
		render(new JsonRender().forIE());
	}

	@Clear({ AdminLoginInterceptor.class, PowerInterceptor.class })
	public void logout() {
		Admin admin = getSessionAttr("administrator");
		CacheKit.remove("menuCache", admin.get("ID") + "menus");
		removeSessionAttr("administrator");
		WebUtils.removeCookie(getResponse(), "administrator");
		redirect("/login");
	}

	@Clear({ AdminLoginInterceptor.class, PowerInterceptor.class })
	@Before(EmailValidate.class)
	public void vldEmail() {
		try {
			String email = getPara("email");
			Admin admin = adminService.findByEmail(email);
			if (admin != null) {
				String subject = "重设您的账户密码";
				String secret = admin.getStr("ID") + "@" + "resetPassword" + "@" + System.currentTimeMillis();
				String url = PropKit.get("server.address") + "admin/resetPassword?action=resetPassword&code="
						+ SecurityCodeTool.encrypt(secret) + "&id=" + admin.getStr("ID");
				String limitSecond = DateUtils.secondToString(PropKit.getLong("interval_resetPassword"));
				String limitStr = "";
				if (StringTool.isNotEmpty(limitSecond)) {
					limitStr = "（请在" + limitSecond + "内完成）";
				}
				String content = "<p>亲爱的用户<font color='blue'>" + admin.getStr("NAME") + "</font>您好！</p>"
						+ "<p>请点击以下链接完成密码重置" + limitStr + "：</p>" + "<a href='" + url + "'><font color='red'>" + url
						+ "</font></a>";
				WebUtils.sendAsynMail(email, subject, content);
				setAttr("error", false);
			} else {
				setAttr("msg", "该邮箱未绑定账户");
				setAttr("error", true);
			}
			render(new JsonRender().forIE());
		} catch (Exception e) {
			setAttr("error", true);
			setAttr("msg", "邮件发送失败！请稍后重试。");
			render(new JsonRender().forIE());
		}
	}

	public void index() {
		render("list.html");
	}

	public void unactive() {
		render("unactive.html");
	}

	@Clear({ AdminLoginInterceptor.class, PowerInterceptor.class })
	@Before(CodeValidate.class)
	public void resetPassword() {
		String action = getPara("action");
		if ("resetPassword".equals(action)) {
			setAttr("id", getPara("id"));
			render("reset.html");
		} else {
			setAttr("error", true);
			setAttr("msg", "该code不适用于本功能");
			render(new JsonRender().forIE());
		}
	}

	public void getEnableData() {
		Map<String, Map<String, Object>> map = new HashMap<>();
		Map<String, Object> where = new HashMap<>();
		where.put("ENABLE", "= 1");
		map.put("limitCond", where);
		getData(map);
	}

	public void getDisableData() {
		Map<String, Map<String, Object>> map = new HashMap<>();
		Map<String, Object> where = new HashMap<>();
		where.put("ENABLE", "!= 1");
		map.put("limitCond", where);
		getData(map);
	}

	private void getData(Map<String, Map<String, Object>> map) {
		DTParams params = new DTParams(getParaMap());
		Page<Record> admins = adminService.getDTPage(params, "admin.list", map);
		JSONObject result = new JSONObject();
		result.put("draw", params.getDraw());
		result.put("recordsTotal", admins.getTotalRow());
		List<Record> list = admins.getList();
		result.put("data", list);
		result.put("recordsFiltered", admins.getTotalRow());
		renderJson(result);
	}

	@Clear({ AdminLoginInterceptor.class, PowerInterceptor.class })
	@Before(SaveResetValidate.class)
	public void saveReset() {
		String id = getPara("id");
		String password = getPara("password");
		Admin admin = Admin.dao.findById(id);
		admin.set("PASSWORD", MD5Util.encrypt(password + ENCRIPT_KEY));
		admin.update();
		setAttr("error", false);
		render(new JsonRender().forIE());
	}

	@Clear(PowerInterceptor.class)
	public void modPassword() {
		Admin admin = Admin.dao.findById(getPara("id"));
		System.out.println(admin);
		setAttr("admin", admin);
		render("mod-password.html");
	}

	public void add() {
		//角色列表
		List<Role> roles = Role.dao.find(Db.getSql("role.list"));
		setAttr("roles", roles);
		render("add.html");
	}

	public void edit() {
		Admin admin = Admin.dao.findById(getPara("id"));
		setAttr("admin", admin);
		//角色列表
		List<Role> roles = Role.dao.find(Db.getSql("role.list"));
		setAttr("roles", roles);
		render("add.html");
	}

	@Before({ AdminValidate.class })
	public void save() {
		String id = getPara("id");
		String password = getPara("password");
		String username = getPara("username");

		Admin admin = new Admin();
		if (StringUtils.isNotEmpty(username)) {
			Admin exUsername = adminService.findByUsername(username);
			if (exUsername != null && (StringUtils.isEmpty(id))
					|| (StringUtils.isNotEmpty(id) && !exUsername.get("ID").equals(id))) {
				renderJson(new JSONError("该用户名已存在！"));
				return;
			}
			admin.set("USERNAME", username);
		}

		String email = getPara("email");
		Admin exEmail = adminService.findByEmail(email);
		if (exEmail != null && (StringUtils.isEmpty(id))
				|| (StringUtils.isNotEmpty(id) && !exEmail.get("ID").equals(id))) {
			renderJson(new JSONError("该邮箱已存在！"));
			return;
		}
		admin.set("EMAIL", email);

		if (StringUtils.isNotEmpty(password)) {
			admin.set("PASSWORD", MD5Util.encrypt(password + ENCRIPT_KEY));
		}
		admin.set("ROLE_ID", getPara("roleId"));
		admin.set("NAME", getPara("name"));
		admin.set("PHONE", getPara("phone"));
		admin.set("ENABLE", 1);
		//先设置默认图片防止没上传的
		String defaultImg = getPara("defaultImg");
		if (StringUtils.isNotEmpty(defaultImg)) {
			admin.set("AVATAR", defaultImg);
		} else {
			admin.set("AVATAR", PropKit.get("default.noimage"));
		}
		try {
			UploadFile file = getFile("avatar");
			if (file != null) {
				String localFilePath = file.getUploadPath() + File.separator + file.getFileName();
				String path = QiniuUtils.upload(localFilePath);
				String crop = getPara("crop");//截图参数
				admin.set("AVATAR", path + (StringUtils.isNotEmpty(crop) ? crop : ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (StringUtils.isNotEmpty(id)) {
			admin.set("ID", id);
			admin.update();
		} else {
			admin.set("ID", UUID.randomUUID().toString().replace("-", ""));
			admin.save();
		}
		/**
		 * 刷新session
		 */
		WebUtils.refreshSession(admin, this);
		renderJson(new JSONSuccess());
	}

	public void disable() {
		String id = getPara("id");
		Admin admin = Admin.dao.findById(id);
		admin.set("ENABLE", 0);
		admin.update();
		renderJson(new JSONSuccess("禁用成功"));
	}

	public void enable() {
		String id = getPara("id");
		Admin admin = Admin.dao.findById(id);
		admin.set("ENABLE", 1);
		admin.update();
		renderJson(new JSONSuccess("恢复成功"));
	}

	//修改密码
	@Clear(PowerInterceptor.class)
	@Before({ ModPassValidate.class })
	public void modPass() {
		String newPass = getPara("confirm");
		Admin admin = getSessionAttr("administrator");
		admin.set("PASSWORD", MD5Util.encrypt(newPass + ENCRIPT_KEY));
		admin.update();
		removeSessionAttr("administrator");
		removeCookie("administrator");
		renderJson(new JSONSuccess());
	}

}
