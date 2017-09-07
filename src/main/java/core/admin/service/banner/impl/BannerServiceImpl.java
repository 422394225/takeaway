
package core.admin.service.banner.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import core.admin.service.banner.BannerService;
import core.admin.service.base.impl.DataTableServiceImpl;
import core.model.Banner;

public class BannerServiceImpl extends DataTableServiceImpl implements BannerService {

	@Override
	public List<Banner> getActiveBanner() {
		return Banner.dao.find("SELECT * AS RANDOM FROM T_BANNER WHERE DELETED=0");
	}

	@Override
	public List<Banner> getIndexBanner() {
		// TODO Auto-generated method stub
		List<Record> temp = Db
				.find("SELECT *,round(rand()*999999) AS RANDOM FROM T_BANNER WHERE DELETED=0 AND `INDEX`=1");
		temp.sort(new Comparator<Record>() {
			@Override
			public int compare(Record o1, Record o2) {
				return o1.getDouble("RANDOM") > o2.getDouble("RANDOM") ? -1 : 1;
			}
		});
		List<Banner> result = new ArrayList<>();
		for (Record record : temp) {
			Banner banner = new Banner();
			for (String field : record.getColumnNames())
				if (!field.equals("RANDOM"))
					banner.set(field, record.get(field));
			result.add(banner);
		}
		return result;
	}

	@Override
	public List<Banner> getNormalBanner() {
		// TODO Auto-generated method stub
		List<Record> temp = Db
				.find("SELECT *,round(rand()*999999) AS RANDOM FROM T_BANNER WHERE DELETED=0 AND `INDEX`=0");
		temp.sort(new Comparator<Record>() {
			@Override
			public int compare(Record o1, Record o2) {
				return o1.getDouble("RANDOM") > o2.getDouble("RANDOM") ? -1 : 1;
			}
		});
		List<Banner> result = new ArrayList<>();
		for (Record record : temp) {
			Banner banner = new Banner();
			for (String field : record.getColumnNames())
				if (!field.equals("RANDOM"))
					banner.set(field, record.get(field));
			result.add(banner);
		}
		return result;
	}

}
