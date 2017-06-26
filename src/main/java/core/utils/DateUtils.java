/**
 * Created At 2017年2月18日下午3:53:06 
 * 
 */

package core.utils;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年2月18日下午3:53:06
 */

public class DateUtils {

	/**
	 * 
	* @Title: getBetweenTime 
	* Description:根据两个时间戳返回间隔时间（单位s）
	* @author weifaguo 
	* @date 2017年2月18日下午3:55:56
	 */
	public static long getBetweenTime(long beginTimestamp, long endTimestamp) {
		return (endTimestamp - beginTimestamp) / 1000;
	}

	/**
	 * 
	* @Title: iOvertime 
	* Description:long beginTimestamp为时间戳long interval单位为秒
	* @author weifaguo 
	* @date 2017年2月18日下午4:06:54
	 */
	public static boolean isOvertime(long beginTimestamp, long interval) {
		long between = getBetweenTime(beginTimestamp, System.currentTimeMillis());
		if (between > interval)
			return true;
		else
			return false;
	}

	public static String secondToString(long second) {
		String timeStr = "";
		long day = second / (3600 * 24);
		long hour = (second % (3600 * 24)) / 3600;
		long minute = ((second % (3600 * 24)) % 3600) / 60;
		long sec = ((second % (3600 * 24)) % 3600) % 60;
		if (sec != 0)
			timeStr = sec + "秒";
		if (minute != 0 || (minute == 0 && sec != 0 && (hour != 0 || day != 0)))
			timeStr = minute + "分" + timeStr;
		if (hour != 0 || (hour == 0 && sec != 0 && minute != 0 && day != 0))
			timeStr = hour + "小时" + timeStr;
		if (day != 0)
			timeStr = day + "天" + timeStr;
		if (sec == 0 && hour == 0 && day == 0 && minute != 0)
			timeStr = minute + "分钟";
		return timeStr;
	}
}
