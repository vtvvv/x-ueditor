package com.vbabc.core.baidu.ueditor.upload;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.oss.OSSClient;

/**
 * 异步上传文件到阿里云OSS
 * 
 * @author fix
 */
public class AsynUploaderThreader extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(AsynUploaderThreader.class);
	private JSONObject stateJson = null;
	private OSSClient client = null;
	private HttpServletRequest request = null;

	public AsynUploaderThreader() {
	}

	public void init(JSONObject stateJson, OSSClient client, HttpServletRequest request) {
		this.stateJson = stateJson;
		this.client = client;
		this.request = request;
	}

	@Override
	public void run() {
		SynUploader synUploader = new SynUploader();
		synUploader.upload(stateJson, client, request);
		logger.debug("asynchronous upload image to aliyun oss success.");
	}

}
