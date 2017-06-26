/**
 * Created At 2017年3月28日上午12:35:32 
 * 
 */

package core.db.migrations;

import com.jfinal.kit.PropKit;

import org.flywaydb.core.Flyway;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年3月28日上午12:35:32
 */

public class FlywayMigration {

	public void migrate(String url, String user, String password) {
		migrate(url, user, password, null);
	}

	public void migrate(String url, String user, String password, String... initSqls) {
		Flyway flyway = new Flyway();

		flyway.setDataSource(url, user, password, initSqls);
		flyway.setSchemas(PropKit.get("schema")); // 设置接受flyway进行版本管理的数据库，多个数据库以逗号分隔
		flyway.setTable("schema_version"); // 设置存放flyway metadata数据的表名  
		flyway.setLocations("flyway"); // 设置flyway扫描sql升级脚本
		flyway.setEncoding("UTF-8"); // 设置sql脚本文件的编码  
		flyway.setValidateOnMigrate(true); // 设置执行migrate操作之前的validation行为  
		flyway.setBaselineOnMigrate(true);//没有这句不能初始化生成schema_version
		flyway.setOutOfOrder(true);//忽略执行顺序

		// 设置当validation失败时的系统行为
		try {
			flyway.migrate();
		} catch (Exception e) {
			try {
				flyway.repair();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			throw e;
		}
	}

}
