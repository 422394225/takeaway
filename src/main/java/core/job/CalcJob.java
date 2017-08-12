package core.job;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class CalcJob implements Job {

	private Log log = Log.getLog(CalcJob.class);
	private static final DecimalFormat df = new DecimalFormat("#.00");

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		calcShopRate();
		calcDelivery();
		calcFood();
	}

	private void calcFood() {
		log.info("开始统计商品信息");
		Pattern pattern;
		List<Record> foodRecords = Db.find("SELECT ID FROM T_FOOD WHERE STATE>=0");
		try {
			for (Record foodIdRecord : foodRecords) {
				int foodId = foodIdRecord.getInt("ID");
				int sum = 0;
				List<String> jsonList = Db
						.query("SELECT FOODS FROM T_ORDER WHERE FOODS LIKE '%id:" + foodId + "%' AND ORDER_STATE>4");
				for (int recordIndex = 0; recordIndex < jsonList.size(); recordIndex++) {
					JSONArray array = JSONArray.parseArray(jsonList.get(recordIndex));
					for (Object object : array) {
						JSONObject foodObject = (JSONObject) object;
						if (foodObject.getIntValue("id") == foodId) {
							sum += foodObject.getIntValue("num");
							break;
						}
					}
				}
				Db.update("UPDATE T_FOOD SET SALE_NUM=? WHERE ID=?", sum, foodId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calcDelivery() {
		log.info("开始统计配送员送餐时间");
		List<Record> records = Db.find(
				"SELECT DELIVERY_ID,SUM(RECIEVE_TIME-DELIVERY_TIME) AS SUM_TIME,COUNT(1) AS RECORD_COUNT FROM T_ORDER WHERE RECIEVE_TIME IS NOT NULL AND DELIVERY_TIME IS NOT NULL GROUP BY DELIVERY_ID");
		for (Record record : records) {
			try {
				Db.update("UPDATE T_DELIVERY SET ORDER_NUM=?,DELIVERY_TIME=? WHERE ID=?",
						record.getLong("RECORD_COUNT").intValue(), df.format(record.getBigDecimal("SUM_TIME").intValue()
								/ record.getLong("RECORD_COUNT").intValue()),
						record.getInt("DELIVERY_ID"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void calcShopRate() {
		log.info("开始执行统计商家评价");
		List<Record> shopIdRecords = Db.find("SELECT ID FROM T_SHOP WHERE STATE>=0");
		for (Record shopIdRecord : shopIdRecords) {
			int shopId = shopIdRecord.getInt("ID");
			Record SummeryRecord = Db.findFirst(
					"SELECT SUM(RATE) AS RATE_SUM,COUNT(*) AS RECORD_COUNT FROM T_RATE WHERE TYPE=0 AND SHOP_ID=?",
					shopId);
			long count = SummeryRecord.getLong("RECORD_COUNT");
			if (count == 0)
				continue;
			BigDecimal sum = SummeryRecord.getBigDecimal("RATE_SUM");
			float rate_avg = 0;
			rate_avg = sum.floatValue() / count;
			log.info(shopId + "\tsum: " + sum + "\tcount: " + count);
			List<Record> countRecords = Db.find(
					"SELECT RATE,COUNT(*) AS RECORD_COUNT FROM T_RATE WHERE TYPE=0 AND SHOP_ID=? GROUP BY RATE",
					shopId);
			long[] rateCount = new long[6];
			for (Record countRecord : countRecords) {
				rateCount[countRecord.getInt("RATE")] = countRecord.getLong("RECORD_COUNT");
			}
			Db.update(
					"UPDATE T_SHOP SET RATE_COUNT=?,RATE_1=?,RATE_2=?,RATE_3=?,RATE_4=?,RATE_5=?,RATE_AVG=? WHERE ID=?",
					(int) count, rateCount[1], rateCount[2], rateCount[3], rateCount[4], rateCount[5],
					df.format(rate_avg), shopId);
		}
	}

}
