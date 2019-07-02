package com.baidu.ueditor.hunter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.aliyun.openservices.oss.OSSClient;
import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;
import com.vbabc.core.alibaba.aliyun.oss.BucketService;
import com.vbabc.core.alibaba.aliyun.oss.ObjectService;
import com.vbabc.core.alibaba.aliyun.oss.OssClientFactory;
import com.vbabc.core.alibaba.aliyun.oss.properties.OssClientProperties;

/**
 * @author fix
 */
public class FileManager {

	private String dir = null;
	private String rootPath = null;

	private String[] allowFiles = null;
	private int count = 0;

	public FileManager(Map<String, Object> conf) {

		this.rootPath = OssClientProperties.rootPath;
		this.dir = this.rootPath + (String) conf.get("dir");
		this.allowFiles = this.getAllowFiles(conf.get("allowFiles"));
		this.count = (Integer) conf.get("count");

	}

	public State listFile(int index) {

		File dir = new File(this.dir);
		State state = null;

		Collection<File> list = null;

		if (OssClientProperties.useStatus) {
			/**
			 *  从阿里云OSS服务器中获取文件
			 */
			String prefix = this.dir.replace(this.rootPath, "");
			prefix = prefix.replaceFirst("/", "");
			// 获取路径
			OSSClient client = OssClientFactory.createOssClient();
			if (OssClientProperties.autoCreateBucket) {
				BucketService.create(client, OssClientProperties.bucketName);
			}
			List<String> objectList = ObjectService.listObject(client, OssClientProperties.bucketName, null, prefix);

			if (index < 0 || index > objectList.size()) {
				state = new MultiState(true);
			} else {
				Object[] fileList = Arrays.copyOfRange(objectList.toArray(), index, index + this.count);
				state = this.getOssState(fileList);
			}

			state.putInfo("start", index);
			state.putInfo("total", objectList.size());

		} else {
			/**
			 *  从文件夹中获取文件
			 */
			if (!dir.exists()) {
				return new BaseState(false, AppInfo.NOT_EXIST);
			}

			if (!dir.isDirectory()) {
				return new BaseState(false, AppInfo.NOT_DIRECTORY);
			}

			list = FileUtils.listFiles(dir, this.allowFiles, true);

			if (index < 0 || index > list.size()) {
				state = new MultiState(true);
			} else {
				Object[] fileList = Arrays.copyOfRange(list.toArray(), index, index + this.count);
				state = this.getState(fileList);
			}

			state.putInfo("start", index);
			state.putInfo("total", list.size());
		}

		return state;

	}

	private State getState(Object[] files) {

		MultiState state = new MultiState(true);
		BaseState fileState = null;

		File file = null;

		for (Object obj : files) {
			if (obj == null) {
				break;
			}
			file = (File) obj;
			fileState = new BaseState(true);
			fileState.putInfo("url", OssClientProperties.rootPath + PathFormat.format(this.getPath(file)));
			state.addState(fileState);
		}

		return state;

	}

	/**
	 * 处理ailiyun数据
	 * 
	 * @param files
	 * @return
	 */
	private State getOssState(Object[] files) {

		MultiState state = new MultiState(true);
		BaseState fileState = null;

		for (Object obj : files) {
			if (obj == null) {
				break;
			}
			fileState = new BaseState(true);
			fileState.putInfo("url", PathFormat.format(OssClientProperties.ossEndPoint + obj));
			state.addState(fileState);
		}

		return state;

	}

	private String getPath(File file) {
		String path = file.getAbsolutePath();
		String chars = ":";
		int length = 5;
		String str = path.replace(this.rootPath.replaceAll("\\/", "\\\\"), "\\");
		if(str.substring(0, length).indexOf(chars) > -1) {
			str = str.substring(str.indexOf(chars)+2);
		}
		
		return str;
	}

	private String[] getAllowFiles(Object fileExt) {

		String[] exts = null;
		String ext = null;

		if (fileExt == null) {
			return new String[0];
		}

		exts = (String[]) fileExt;

		for (int i = 0, len = exts.length; i < len; i++) {

			ext = exts[i];
			exts[i] = ext.replace(".", "");

		}

		return exts;

	}

}
