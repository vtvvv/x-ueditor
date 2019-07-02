package com.vbabc.core.alibaba.aliyun.oss.properties;

import java.util.Properties;

import org.springframework.stereotype.Component;

import com.vbabc.core.UeditorAutoConfiguration;
import com.vbabc.core.utils.PropertiesLoader;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fix
 */
@Slf4j
@Component
public class OssClientProperties {

	private static Properties OSSKeyProperties = null;
	/**
	 * 阿里云是否启用配置
	 */
	public static String configPath;
	public static String rootPath = "";
	public static String serverUrl = "";
	
	public static boolean showFileView = true;
	public static boolean useStatus = false;
	public static String bucketName = "";
	public static String key = "";
	public static String secret = "";
	public static boolean autoCreateBucket = false;

	public static String ossCliendEndPoint = "";
	public static String ossEndPoint = "";
	public static boolean useCDN = false;
	public static String cdnEndPoint = "";

	public static boolean useLocalStorager = false;
	public static String uploadBasePath = "upload";
	public static boolean useAsynUploader = false;

	static {
		try {
			configPath = UeditorAutoConfiguration.configPath;
			if (OSSKeyProperties == null) {
				OSSKeyProperties = new PropertiesLoader(new String[] { configPath + "/OSSKey.properties" }).getProperties();
			}

			rootPath = null == (String) OSSKeyProperties.get("rootPath") ? "/ueditordir" : (String) OSSKeyProperties.get("rootPath") ;
			serverUrl = null == (String) OSSKeyProperties.get("serverUrl") ? "/ueditor/jsp/controller" : (String) OSSKeyProperties.get("serverUrl") ;
			
			showFileView = "false".equalsIgnoreCase((String) OSSKeyProperties.get("showFileView")) ? false : true;
			useStatus = "true".equalsIgnoreCase((String) OSSKeyProperties.get("useStatus")) ? true : false;
			bucketName = (String) OSSKeyProperties.get("bucketName");
			key = (String) OSSKeyProperties.get("key");
			secret = (String) OSSKeyProperties.get("secret");
			autoCreateBucket = "true".equalsIgnoreCase((String) OSSKeyProperties.get("autoCreateBucket")) ? true
					: false;

			ossCliendEndPoint = (String) OSSKeyProperties.get("ossCliendEndPoint");
			ossEndPoint = (String) OSSKeyProperties.get("ossEndPoint");
			useCDN = "true".equalsIgnoreCase((String) OSSKeyProperties.get("useCDN")) ? true : false;
			cdnEndPoint = (String) OSSKeyProperties.get("cdnEndPoint");

			useLocalStorager = "true".equalsIgnoreCase((String) OSSKeyProperties.get("useLocalStorager")) ? true
					: false;
			uploadBasePath = (String) OSSKeyProperties.get("uploadBasePath");
			useAsynUploader = "true".equalsIgnoreCase((String) OSSKeyProperties.get("useAsynUploader")) ? true : false;
		} catch (Exception e) {
			log.warn("系统未找到指定文件：OSSKey.properties --> 系统按照ueditor默认配置执行。");
		}
	}
	
}
