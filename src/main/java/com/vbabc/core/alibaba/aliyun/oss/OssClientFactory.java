package com.vbabc.core.alibaba.aliyun.oss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.openservices.oss.OSSClient;
import com.vbabc.core.alibaba.aliyun.oss.properties.OssClientProperties;
import com.vbabc.core.baidu.ueditor.upload.AsynUploaderThreader;

/**
 * OSSClient是OSS服务的Java客户端，它为调用者提供了一系列的方法，用于和OSS服务进行交互<br>
 * 
 * @author fix
 */
public class OssClientFactory {

	private static final Logger logger = LoggerFactory.getLogger(AsynUploaderThreader.class);
	private static OSSClient client = null;

	/**
	 * 新建OSSClient
	 * 
	 * @return client
	 */
	public static OSSClient createOssClient() {
		if (null == client) {
			client = new OSSClient(OssClientProperties.ossCliendEndPoint, OssClientProperties.key,
					OssClientProperties.secret);
			logger.info("First CreateOSSClient success.");
		}
		return client;
	}

}
