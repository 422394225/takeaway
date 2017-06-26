package core.interceptor;

import java.util.UUID;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import core.model.Admin;
import core.model.Log;

public class LogInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation inv) {
			Controller controller = inv.getController();
			boolean b = false;
			String ck = inv.getControllerKey();
			String method = inv.getMethodName();
			Admin admin = controller.getSessionAttr("administrator");			
			Log log = new Log();
			
			inv.invoke();	
			
			if ("/admin".equals(ck)) {
				if ("edit".equals(method)) {
					b=true;
					log.set("OPERATION","编辑管理员");
					
				}
				if ( "add".equals(method)) {
					b=true;
					log.set("OPERATION","添加管理员");
					
				}
				if ( "enable".equals(method)) {
					b=true;
					log.set("OPERATION","恢复管理员账户");
					
				}
				if ( "disable".equals(method)) {
					b=true;
					log.set("OPERATION","停用管理员账户");
					
				}
				if ( "modPassword".equals(method)) {
					b=true;
					log.set("OPERATION","修改管理员密码");
					
				}
			}
			if ("/competition".equals(ck)) {
				if ("add".equals(method)) {
					b=true;
					log.set("OPERATION","添加赛事");
					
				}
				if ( "edit".equals(method)) {
					b=true;
					log.set("OPERATION","编辑赛事");
					
				}
				if ("remove".equals(method)) {
					b=true;
					log.set("OPERATION","编辑赛事");
					
				}
			}
			if ("/applay".equals(ck)) {
				if ("add".equals(method)) {
					b=true;
					log.set("OPERATION","添加比赛报名");
					
				}
				if ( "state".equals(method)) {
					b=true;
					log.set("OPERATION","编辑审核状态");
					
				}
				if ( "view".equals(method)) {
					b=true;
					log.set("OPERATION","查看比赛报名");
					
				}
				if ( "remove".equals(method)) {
					b=true;
					log.set("OPERATION","删除比赛报名");
					
				}
			}
			if ("/host".equals(ck)) {
				if ("add".equals(method)) {
					b=true;
					log.set("OPERATION","添加举办方");
					
				}
				if ( "edit".equals(method)) {
					b=true;
					log.set("OPERATION","编辑举办方");
					
				}
				if ( "remove".equals(method)) {
					b=true;
					log.set("OPERATION","删除举办方");
					
				}
			}
			if ("/work".equals(ck)) {
				if ("add".equals(method)) {
					b=true;
					log.set("OPERATION","添加作品");
					
				}
				if ( "edit".equals(method)) {
					b=true;
					log.set("OPERATION","编辑作品");
					
				}
				if ( "remove".equals(method)) {
					b=true;
					log.set("OPERATION","删除作品");
					
				}
			}
			if ("/customer".equals(ck)) {

				if ("add".equals(method)) {
					b=true;
					log.set("OPERATION","添加客服");
					
				}
				if ("edit".equals(method)) {
					b=true;
					log.set("OPERATION","编辑客服");
					
				}
				if ("remove".equals(method)) {
					b=true;
					log.set("OPERATION","编辑客服");
					
				}
			}
			if ("/ticket".equals(ck)) {
				if ("add".equals(method)) {
					b=true;
					log.set("OPERATION","添加餐券");
					
				}
				if ( "edit".equals(method)) {
					b=true;
					log.set("OPERATION","编辑餐券");
					
				}
				if ( "remove".equals(method)) {
					b=true;
					log.set("OPERATION","删除餐券");
					
				}
			}
			if ("/dining".equals(ck)) {
				if ("add".equals(method)) {
					b=true;
					log.set("OPERATION","添加餐厅");
					
				}
				if ("edit".equals(method)) {
					b=true;
					log.set("OPERATION","编辑餐厅");
					
				}
				if ("remove".equals(method)) {
					b=true;
					log.set("OPERATION","删除餐厅");
					
				}
			}
			if ("/hotel".equals(ck)) {
				if ("add".equals(method)) {
					b=true;
					log.set("OPERATION","添加酒店");
					
				}
				if ( "edit".equals(method)) {
					b=true;
					log.set("OPERATION","编辑酒店");
					
				}
				if ( "remove".equals(method)) {
					b=true;
					log.set("OPERATION","删除酒店");
					
				}
			}
			
			if (b) {
				log.set("ID",  UUID.randomUUID().toString().replace("-", ""));
				log.set("ADMIN_ID", admin.get("ID"));
				log.save();
				b=false;
			}
			
	}
	
}
