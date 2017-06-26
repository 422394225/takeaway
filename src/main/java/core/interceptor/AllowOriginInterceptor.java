/**
 * Created At 2017年4月5日上午12:29:53 
 * 
 */

package core.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import javax.servlet.http.HttpServletResponse;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月5日上午12:29:53
 */

public class AllowOriginInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation ai) {
		HttpServletResponse response = ai.getController().getResponse();
		//		HttpServletRequest request = ai.getController().getRequest();
		//		Map<String, String[]> map = request.getParameterMap();
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Headers",
				"Content-Type,X-Requested-With,token,application/x-www-form-urlencoded,multipart/form-data,text/plain");
		ai.invoke();
	}

}
