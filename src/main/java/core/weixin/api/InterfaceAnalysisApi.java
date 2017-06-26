package core.weixin.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.utils.HttpUtils;

public class InterfaceAnalysisApi {
	private final static String GET_INTERFACE_SUMMARY_HOUR  = "https://api.weixin.qq.com/datacube/getinterfacesummaryhour?access_token=";
	
	public ApiResult getInterfaceSummaryHour(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = cal.getTime();
		String AccessToken = AccessTokenApi.getAccessTokenStr();
		
		String data = "{ \"begin_date\":\""+format.format(date)+"\", \"end_date\": \""+format.format(date)+"\"}";
		String JsonResult = HttpUtils.post(GET_INTERFACE_SUMMARY_HOUR+AccessToken, data);
		return new ApiResult(JsonResult);
	}

}
