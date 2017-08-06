/**
 * Created At 2017年4月6日下午11:09:17 
 * 
 */

package core.common.utils;

import com.google.gson.Gson;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.mchange.v1.io.InputStreamUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.util.Auth;

import java.io.IOException;
import java.io.InputStream;

/**
 * Description:
 * 
 * @author weifaguo
 * @date 2017年4月6日下午11:09:17
 */

public class QiniuUtils {
	private static Log log = Log.getLog(QiniuUtils.class);
	private static final String ACCESS_KEY = PropKit.get("qiniu.accessKey");
	private static final String SECRET_KEY = PropKit.get("qiniu.secretKey");
	private static final String BUCKET_NAME = PropKit.get("qiniu.bucketName");

	//上传本地文件
	public static String upload(String path) {
		return upload(path, false);
	}

	public static String upload(String path, boolean remote) {
		//构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone2());
		//...其他参数参考类注释
		UploadManager uploadManager = new UploadManager(cfg);

		//默认不指定key的情况下，以文件内容的hash值作为文件名
		String key = null;
		Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
		String upToken = auth.uploadToken(BUCKET_NAME);

		if (remote) {
			BucketManager bm = new BucketManager(auth, cfg);
			try {
				FetchRet fetchRet = bm.fetch(path, BUCKET_NAME);
				String filePath = PropKit.get("qiniu.url") + fetchRet.key;
				log.info("文件上传成功！" + path + "\n" + "\tPath:\t" + path + "\tMd5:\t" + fetchRet.hash + "\tKey:\t"
						+ fetchRet.key);
				return filePath;
			} catch (QiniuException e) {
				e.printStackTrace();
				return path;
			}
		} else {
			try {
				Response response = uploadManager.put(path, key, upToken);
				//解析上传成功的结果
				DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
				String filePath = PropKit.get("qiniu.url") + putRet.key;
				log.info("文件上传成功！" + path + "\n" + "\tPath:\t" + path + "\tMd5:\t" + putRet.hash + "\tKey:\t"
						+ putRet.key);
				return filePath;
			} catch (QiniuException ex) {
				Response r = ex.response;
				System.err.println(r.toString());
				try {
					System.err.println(r.bodyString());
				} catch (QiniuException ex2) {
					//ignore
				}
			}
		}
		return null;
	}

	public static String upload(InputStream in) {
		//构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone2());
		//...其他参数参考类注释

		UploadManager uploadManager = new UploadManager(cfg);

		//默认不指定key的情况下，以文件内容的hash值作为文件名
		String key = null;

		Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
		String upToken = auth.uploadToken(BUCKET_NAME);

		try {
			Response response = null;
			try {
				response = uploadManager.put(InputStreamUtils.getBytes(in), key, upToken);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//解析上传成功的结果
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			String filePath = PropKit.get("qiniu.url") + putRet.key;
			log.info("文件上传成功！" + "\tMd5:\t" + putRet.hash + "\tKey:\t" + putRet.key);
			return filePath;
		} catch (QiniuException ex) {
			Response r = ex.response;
			System.err.println(r.toString());
			try {
				System.err.println(r.bodyString());
			} catch (QiniuException ex2) {
				//ignore
			}
		}

		return null;
	}

	public static String upload(byte[] bytes) {
		//构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone2());
		//...其他参数参考类注释

		UploadManager uploadManager = new UploadManager(cfg);

		//默认不指定key的情况下，以文件内容的hash值作为文件名
		String key = null;

		Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
		String upToken = auth.uploadToken(BUCKET_NAME);

		try {
			Response response = null;
			try {
				response = uploadManager.put(bytes, key, upToken);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//解析上传成功的结果
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			String filePath = PropKit.get("qiniu.url") + putRet.key;
			log.info("文件上传成功！" + "\tMd5:\t" + putRet.hash + "\tKey:\t" + putRet.key);
			return filePath;
		} catch (QiniuException ex) {
			Response r = ex.response;
			System.err.println(r.toString());
			try {
				System.err.println(r.bodyString());
			} catch (QiniuException ex2) {
				//ignore
			}
		}

		return null;
	}

}
