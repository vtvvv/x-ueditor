package com.vbabc.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

import com.vbabc.core.alibaba.aliyun.oss.properties.OssClientProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * System Utils
 * 
 * @author fix
 */
@Slf4j
public class SystemUtil {

	private static final String STARTCHAR = "/";
	private static final String BUILD_CLASSES = "build/classes/";
	private static final String WEBINF_CLASSES = "WEB-INF/classes/";
	private static final String WIN_LOW = "win";
	private static final String WIN_UP = "Win";

	private static String classesPath = null;
	private static String projectName = null;

	/**
	 * 获取系统编译文件的路径
	 * 
	 * @return classesPath
	 */
	public static String getProjectClassesPath() {
		if (classesPath == null) {
			classesPath = SystemUtil.class.getClassLoader().getResource("").getPath().trim();
			if (!isLinux()) {
				classesPath = classesPath.replaceFirst("/", "");
			}
			try {
				classesPath = URLDecoder.decode(classesPath, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return classesPath;
	}

	/**
	 * 系统类型
	 */
	private static String osName = null;

	/**
	 * 获取文件存储根目录位置
	 * 
	 * @return file_name
	 */
	public static String getFileStorageDirName() {
		String rootPath = OssClientProperties.rootPath;
		if (rootPath == null || !rootPath.startsWith(STARTCHAR)) {
			String classesPath = getProjectClassesPath();
			log.debug("classesPath is :" + classesPath);
			rootPath = classesPath + rootPath;
		}
		log.debug("rootPath is :" + rootPath);
		return rootPath;
	}

	public static String inputStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}

	/**
	 * 获取项目名称
	 * 
	 * @return project_name
	 */
	public static String getProjectName() {
		if (projectName == null) {
			String classesPath = getProjectClassesPath();
			// java
			String rootPath = "";
			if (classesPath.endsWith(BUILD_CLASSES)) {
				rootPath = classesPath.replace(BUILD_CLASSES, "");
			} else if (classesPath.endsWith(WEBINF_CLASSES)) {
				// java web
				rootPath = classesPath.replace(WEBINF_CLASSES, "");
			}
			rootPath += "__";
			rootPath = rootPath.replace("/__", "");
			rootPath = rootPath.replaceAll("/", "/__");
			int index = rootPath.lastIndexOf("/__");
			if (index == -1) {
				return "";
			}
			projectName = rootPath.substring(index + 3);
		}
		return projectName;
	}

	/**
	 * 获取系统的类型
	 * 
	 * @return osName
	 */
	public static String getOsName() {
		if (osName == null) {
			Properties prop = System.getProperties();
			osName = prop.getProperty("os.name");
		}
		return osName;
	}

	/**
	 * 判断系统是否为Linux
	 * 
	 * @return true：linux false: win
	 */
	public static boolean isLinux() {
		if (getOsName().startsWith(WIN_LOW) || getOsName().startsWith(WIN_UP)) {
			return false;
		}
		return true;
	}

}
