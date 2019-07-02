package com.vbabc.core.baidu.ueditor.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.vbabc.core.alibaba.aliyun.oss.ObjectService;
import com.vbabc.core.alibaba.aliyun.oss.properties.OssClientProperties;
import com.vbabc.core.utils.SystemUtil;

/**
 * 同步上传文件到阿里云OSS<br>
 * 
 * @author fix
 */

public class SynUploader extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(AsynUploaderThreader.class);

	public boolean upload(JSONObject stateJson, OSSClient client, HttpServletRequest request) {
		String key = stateJson.getString("url").replaceFirst("/", "");
		try {
			String path = SystemUtil.getFileStorageDirName() + "/" + key;
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			PutObjectResult result = ObjectService.putObject(client, OssClientProperties.bucketName, key,
					fileInputStream);
			logger.debug("upload file to aliyun OSS object server success. ETag: " + result.getETag());
			return true;
		} catch (FileNotFoundException e) {
			logger.error("upload file to aliyun OSS object server occur FileNotFoundException.");
		} catch (NumberFormatException e) {
			logger.error("upload file to aliyun OSS object server occur NumberFormatException.");
		} catch (IOException e) {
			logger.error("upload file to aliyun OSS object server occur IOException.");
		}
		return false;
	}

}
