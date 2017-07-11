/**
 * Created At 2017年7月10日下午11:35:24 
 * 
 */

package core.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import core.tag.DicCodeDirective;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年7月10日下午11:35:24
 */

public class DirectiveInterceptor implements Interceptor {

	public static DicCodeDirective dcd = new DicCodeDirective();

	public void intercept(Invocation ai) {
		Controller c = ai.getController();
		c.setAttr("_dc", dcd);
		ai.invoke();
	}

}
