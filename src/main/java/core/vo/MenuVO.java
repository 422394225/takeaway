/**
 * Created At 2017年3月24日上午12:10:07 
 * 
 */

package core.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年3月24日上午12:10:07
 */

public class MenuVO {
	public Map<String, Object> data;
	public List<MenuVO> chidren;

	public MenuVO(Map<String, Object> data) {
		this.data = data;
		chidren = new ArrayList<>();
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public List<MenuVO> getChidren() {
		return chidren;
	}

	public void setChidren(List<MenuVO> chidren) {
		this.chidren = chidren;
	}

}
