package com.baidu.ueditor.upload;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.Bucket;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.hunter.ImageHunter;
import com.vbabc.core.alibaba.aliyun.oss.BucketService;
import com.vbabc.core.alibaba.aliyun.oss.OssClientFactory;
import com.vbabc.core.alibaba.aliyun.oss.properties.OssClientProperties;
import com.vbabc.core.baidu.ueditor.upload.AsynUploaderThreader;
import com.vbabc.core.baidu.ueditor.upload.SynUploader;

/**
 * 同步上传文件到阿里云OSS<br>
 * 
 * @author fix
 */
public class Uploader {
	private static final Logger logger = LoggerFactory.getLogger(AsynUploaderThreader.class);

	private static final String ISBASE64 = "isBase64";
	private static final String TRUE = "true";
	private static final String FILENAME = "filename";
	private static final String REMOTE = "remote";

	private HttpServletRequest request = null;
	private Map<String, Object> conf = null;

	public Uploader(HttpServletRequest request, Map<String, Object> conf) {
		this.request = request;
		this.conf = conf;
	}

	public final State doExec() {
		String filedName = (String) this.conf.get(FILENAME);
		State state = null;

		if (TRUE.equals(this.conf.get(ISBASE64))) {
			state = Base64Uploader.save(this.request.getParameter(filedName), this.conf);
		} else {
			/**
			 * 对远程粘帖的图片先进行缓存
			 */
			if (REMOTE.equals(this.conf.get(FILENAME))) {
				String[] list = this.request.getParameterValues((String) conf.get("fieldName"));
				state = new ImageHunter(conf).capture(list);
			} else {
				state = BinaryUploader.save(this.request, this.conf);
			}
			JSONObject stateJson = JSONObject.parseObject(state.toJsonString());
			/**
			 * 判别云同步方式
			 */
			if (OssClientProperties.useStatus) {

				String bucketName = OssClientProperties.bucketName;
				OSSClient client = OssClientFactory.createOssClient();
				/**
				 * auto create Bucket to default zone
				 */
				if (OssClientProperties.autoCreateBucket) {
					Bucket bucket = BucketService.create(client, bucketName);
					logger.debug("Bucket 's " + bucket.getName() + " Created.");
				}

				/**
				 * upload type
				 */
				if (OssClientProperties.useAsynUploader) {
					AsynUploaderThreader asynThreader = new AsynUploaderThreader();
					asynThreader.init(stateJson, client, this.request);
					/**
					 * 构造一个线程池
					 */
					ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS,
							new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
					threadPool.execute(() -> {
						try {
							asynThreader.run();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							threadPool.shutdown();
						}
					});
				} else {
					SynUploader synUploader = new SynUploader();
					/**
					 * 对远程粘帖的图片进行上传
					 */
					if (REMOTE.equals(this.conf.get(FILENAME))) {
						JSONArray jarr = stateJson.getJSONArray("list");
						for (int i = 0; i < jarr.size(); i++) {
							JSONObject stateJsonItor = jarr.getJSONObject(i);
							if("SUCCESS".equals(stateJsonItor.getString("state"))) {
								synUploader.upload(stateJsonItor, client, this.request);								
							}
						}
					} else {
						synUploader.upload(stateJson, client, this.request);
					}
				}

				/**
				 * storage type
				 */
				deleteLocalStorager(stateJson);

				/**
				 * 返回结果
				 */
				if (REMOTE.equals(this.conf.get(FILENAME))) {
					MultiState states = remoteFormatResoult(stateJson);
					return states;
				} else {
					state.putInfo("url", OssClientProperties.ossEndPoint + stateJson.getString("url"));
				}
			} else {
				if (REMOTE.equals(this.conf.get(FILENAME))) {
					MultiState states = remoteFormatResoult(stateJson);
					return states;
				} else {
					state.putInfo("url", OssClientProperties.rootPath + stateJson.getString("url"));					
				}
			}
		}
		logger.debug(state.toJsonString());
		return state;
	}

	/**
	 * 远程图片操作结果格式化
	 */
	public MultiState remoteFormatResoult(JSONObject stateJson) {
		JSONArray jarr = stateJson.getJSONArray("list");
		MultiState states = new MultiState(true);
		for (int i = 0; i < jarr.size(); ++i) {
			State stateNew = new BaseState(true);
			JSONObject stateJsonItor = jarr.getJSONObject(i);
			if(!"SUCCESS".equals(stateJsonItor.getString("state"))) {
				continue;
			}
			stateNew.putInfo("size", stateJsonItor.getString("size"));
			stateNew.putInfo("title", stateJsonItor.getString("title"));
			if (OssClientProperties.useStatus) {
				stateNew.putInfo("url", OssClientProperties.ossEndPoint + stateJsonItor.getString("url"));				
			} else if(!OssClientProperties.useStatus && OssClientProperties.showFileView) {
				stateNew.putInfo("url", OssClientProperties.rootPath + stateJsonItor.getString("url"));	
			} else {
				stateNew.putInfo("url", stateJsonItor.getString("url"));
			}
			stateNew.putInfo("source", stateJsonItor.getString("source"));
			states.addState(stateNew);
		}
		return states;
	}

	/**
	 * 清除本地缓存
	 */
	public void deleteLocalStorager(JSONObject stateJson) {
		/**
		 * 判断配置文件中设置
		 */
		if (false == OssClientProperties.useLocalStorager) {
			if (REMOTE.equals(this.conf.get(FILENAME))) {
				JSONArray jarr = stateJson.getJSONArray("list");

				for (int i = 0; i < jarr.size(); ++i) {
					JSONObject stateJsonItor = jarr.getJSONObject(i);
					String uploadFilePath = OssClientProperties.rootPath + (String) stateJsonItor.get("url");
					File uploadFile = new File(uploadFilePath);
					if (uploadFile.isFile() && uploadFile.exists()) {
						uploadFile.delete();
					}
				}
			} else {
				String uploadFilePath = OssClientProperties.rootPath + (String) stateJson.get("url");
				File uploadFile = new File(uploadFilePath);
				if (uploadFile.isFile() && uploadFile.exists()) {
					uploadFile.delete();
				}
			}

		}
	}
}
