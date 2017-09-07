
package core.admin.service.banner;

import java.util.List;

import core.admin.service.base.DataTableService;
import core.model.Banner;

public interface BannerService extends DataTableService {
	/**
	 * 获取所有正在使用的Banner
	 * 
	 * @return
	 */
	public List<Banner> getActiveBanner();

	/**
	 * 获取最先显示的List
	 * 
	 * @return
	 */
	public List<Banner> getIndexBanner();

	/**
	 * 获取一般优先级的List
	 * 
	 * @return
	 */
	public List<Banner> getNormalBanner();
}
