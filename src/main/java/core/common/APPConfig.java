package core.common;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

import java.io.File;

import core.common.mapping._MappingKit;
import core.common.routes.AdminRoutes;
import core.common.routes.WeixinRoutes;
import core.common.utils.ConfigUtil;
import core.db.migrations.FlywayMigration;
import core.interceptor.LogInterceptor;
import core.weixin.controller.WeixinMsgController;

/**
 * @author Javen
 */
public class APPConfig extends JFinalConfig {
	static Log log = Log.getLog(WeixinMsgController.class);

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		ConfigUtil.loadProp("config_pro.properties", "config.properties");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setEncoding("utf-8");
		me.setViewType(ViewType.FREE_MARKER);
		//设置上传文件保存的路径
		me.setBaseUploadPath(File.separator + "myupload");
		me.setError404View("/WEB-INF/admin/404.html");
		me.setError500View("/WEB-INF/admin/error.html");
		// ApiConfigKit 设为开发模式可以在开发阶段输出请求交互的 xml 与 json 数据
		ApiConfigKit.setDevMode(me.getDevMode());

	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add(new AdminRoutes());
		me.add(new WeixinRoutes());
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置C3p0数据库连接池插件
		DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"),
				PropKit.get("password").trim());
		me.add(druidPlugin);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		arp.setBaseSqlTemplatePath(PathKit.getRootClassPath());
		arp.addSqlTemplate("admin_sql");
		arp.addSqlTemplate("weixin_sql");

		_MappingKit.mapping(arp);
		arp.setShowSql(true);
		me.add(arp);
		me.add(new EhCachePlugin());
		// 定时任务
		QuartzPlugin quartz = new QuartzPlugin();
		quartz.setJobs("job_calc.properties");
		me.add(quartz);
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		me.addGlobalActionInterceptor(new LogInterceptor());
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("base"));
	}

	public void configEngine(Engine arg0) {

	}

	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目
	 * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {

		//sql升级
		ConfigUtil.loadProp("config_pro.properties", "config.properties");
		FlywayMigration fm = new FlywayMigration();
		fm.migrate(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));

		JFinal.start("src/main/webapp", 80, "/", 5);//启动配置项
	}

}
