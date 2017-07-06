/**
 * Created At 2017年2月25日下午4:38:28 
 * 
 */

package core.vo;

import com.alibaba.fastjson.JSONArray;

import java.util.Map;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年2月25日下午4:38:28
 */

public class DTParams {
	private Map<String, String[]> map = null;
	/**
	 * 将datatable发送过来的请求解析成对应类型
	 */
	private final String DRAW = "draw";
	private final String START = "start";
	private final String LENGTH = "length";
	private final String SEARCH_VALUE = "search[value]";
	private final String SEARCH_REGEX = "search[regex]";
	private final String TIME_STAMP = "_";
	/**
	 * coluums部分
	 */
	private final String COLUMNS = "columns[";
	private final String C_DATA = "][data]";
	private final String C_NAME = "][name]";
	private final String C_SEARCH_VALUE = "][search][value]";
	private final String C_SEARCH_REGEX = "][search][regex]";
	private final String C_SEARCHABLE = "][searchable]";
	private final String C_ORDERABLES = "][orderable]";
	/**
	 * order部分
	 */
	private final String ORDER = "order[";
	private final String ORDER_DIR = "][dir]";
	private final String ORDER_COLUMN = "][column]";
	/**
	 * 自定义参数
	 */
	private final String CUSTOM_PARAMS = "custom_search";

	/**
	 * 传入parameterMap
	 */
	public DTParams(Map<String, String[]> map) {
		this.map = map;
	}

	public Integer getDraw() {
		return Integer.parseInt(map.get(DRAW)[0]);
	}

	public Integer getStart() {
		return Integer.parseInt(map.get(START)[0]);
	}

	public Integer getLength() {
		return Integer.parseInt(map.get(LENGTH)[0]);
	}

	public boolean getSearchRegex() {
		return Boolean.parseBoolean(map.get(SEARCH_REGEX)[0]);
	}

	public String getSearchValue() {
		return map.get(SEARCH_VALUE)[0];
	}

	/**
	 * 
	*Column部分
	 */
	public String getCData(int index) {
		String[] cData = map.get(COLUMNS + index + C_DATA);
		if (cData == null) {
			return null;
		}
		return cData[0] == "" ? "ingnore" : cData[0];
	}

	public String getCName(int index) {
		return map.get(COLUMNS + index + C_NAME)[0];
	}

	public String getCSearchValue(int index) {
		return map.get(COLUMNS + index + C_SEARCH_VALUE)[0];
	}

	public boolean getCSearchRegex(int index) {
		return Boolean.parseBoolean(map.get(COLUMNS + index + C_SEARCH_REGEX)[0]);
	}

	public boolean getCSearchable(int index) {
		return Boolean.parseBoolean(map.get(COLUMNS + index + C_SEARCHABLE)[0]);
	}

	public boolean getOrderable(int index) {
		return Boolean.parseBoolean(map.get(COLUMNS + index + C_ORDERABLES)[0]);
	}

	/**
	 * order部分
	 */
	public String getOrderDir() {
		return getOrderDir(0);
	}

	public Integer getOrderColumn() {
		return getOrderColumn(0);
	}

	public String getOrderDir(int index) {
		return map.get(ORDER + index + ORDER_DIR)[0];
	}

	public Integer getOrderColumn(int index) {
		String[] orderColum = map.get(ORDER + index + ORDER_COLUMN);
		if (orderColum == null) {
			return null;
		}
		return Integer.parseInt(orderColum[0]);
	}

	/**
	 * 其他
	 */
	public long getTimeStamp() {
		return Long.parseLong(map.get(TIME_STAMP)[0]);
	}

	public JSONArray getCustomParams() {
		String str = "[]";
		try {
			str = map.get(CUSTOM_PARAMS)[0];
		} catch (Exception e) {
		}
		return JSONArray.parseArray(str);
	}
}
