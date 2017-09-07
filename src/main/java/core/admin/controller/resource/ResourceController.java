package core.admin.controller.resource;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;

import core.admin.service.banner.BannerService;
import core.admin.service.banner.impl.BannerServiceImpl;
import core.common.utils.QiniuUtils;
import core.model.Banner;
import core.vo.JSONError;
import core.vo.JSONSuccess;

/**
 * 
 * Description:
 * 
 * @author weifaguo
 * @date 2017年1月30日下午2:40:27
 */
public class ResourceController extends Controller {
	BannerService bannerService = new BannerServiceImpl();

	public void index() {
		render("index.html");
	}

	public void getBannerList() {
		List<Banner> indexList = bannerService.getIndexBanner();
		List<Banner> normalList = bannerService.getNormalBanner();
		JSONArray resultArray = new JSONArray();
		for (Banner banner : indexList) {
			JSONObject object = new JSONObject();
			object.put("ID", banner.get("ID"));
			object.put("IMG_URL", banner.get("IMG_URL"));
			object.put("INDEX", banner.getInt("INDEX") == 1);
			resultArray.add(object);
		}
		for (Banner banner : normalList) {
			JSONObject object = new JSONObject();
			object.put("IMG_URL", banner.get("IMG_URL"));
			object.put("ID", banner.get("ID"));
			object.put("INDEX", banner.getInt("INDEX") == 1);
			resultArray.add(object);
		}
		System.out.println(resultArray);
		renderJson(new JSONSuccess(resultArray));
	}

	public void addBannerHtml() {
		render("addBanner.html");
	}

	public void addBanner() {
		try {
			// getFile();
			Banner banner = new Banner();
			try {
				UploadFile file = getFile("IMG");
				if (file != null) {
					String localFilePath = file.getUploadPath() + File.separator + file.getFileName();
					String path = QiniuUtils.upload(localFilePath);
					String crop = getPara("crop");// 截图参数
					banner.set("IMG_URL", path + (StringUtils.isNotEmpty(crop) ? crop : ""));
				}
			} catch (Exception e) {
				renderJson(new JSONError("图片错误"));
				return;
			}
			String link = getPara("LINK");
			if (StringUtils.isBlank(link)) {
				link = "javascript:void(0);";
			}
			banner.set("LINK", link);
			Object isIndex = getPara("INDEX");
			if (isIndex == null)
				banner.set("INDEX", "0");
			else
				banner.set("INDEX", "1");
			banner.save();
			renderJson(new JSONSuccess());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONError("发生错误"));
			return;
		}
	}

	public void troggleIndex() {
		try {
			Integer bannerId = getParaToInt("bannerId");
			Banner banner = Banner.dao.findById(bannerId);
			if (banner.getInt("INDEX") == 1)
				banner.set("INDEX", 0);
			else
				banner.set("INDEX", 1);
			banner.update();
			renderJson(new JSONSuccess());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONError("发生错误"));
		}
	}

	public void deleteBanner() {
		try {
			Integer bannerId = getParaToInt("bannerId");
			Banner banner = Banner.dao.findById(bannerId);
			banner.delete();
			renderJson(new JSONSuccess());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(new JSONError("发生错误"));
		}
	}
}