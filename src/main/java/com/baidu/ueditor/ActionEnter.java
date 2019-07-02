package com.baidu.ueditor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.hunter.FileManager;
import com.baidu.ueditor.upload.Uploader;

/**
 * 
 * @author fix
 *
 */
public class ActionEnter {

	private HttpServletRequest request = null;

	private String contextPath = null;

	private String actionType = null;

	private ConfigManager configManager = null;

	private static final String ACTION_URI = "//ueditor/controller";
	private static final String MATCH_ROLE = "^[a-zA-Z_]+[\\\\w0-9_]*$";

	public ActionEnter(HttpServletRequest request) {

		this.request = request;
		this.actionType = request.getParameter("action");
		this.contextPath = request.getContextPath();
		String uri = request.getRequestURI();
		if (ACTION_URI.equals(uri)) {
			uri = "/tmp";
		}
		this.configManager = ConfigManager.getInstance(this.contextPath, uri);

	}

	public String exec() {

		String callbackName = this.request.getParameter("callback");

		if (callbackName != null) {

			if (!validCallbackName(callbackName)) {
				return new BaseState(false, AppInfo.ILLEGAL).toJsonString();
			}

			return callbackName + "(" + this.invoke() + ");";

		} else {
			return this.invoke();
		}

	}

	public String invoke() {

		if (actionType == null || !ActionMap.MAPPING.containsKey(actionType)) {
			return new BaseState(false, AppInfo.INVALID_ACTION).toJsonString();
		}

		if (this.configManager == null || !this.configManager.valid()) {
			return new BaseState(false, AppInfo.CONFIG_ERROR).toJsonString();
		}

		State state = null;

		int actionCode = ActionMap.getType(this.actionType);

		Map<String, Object> conf = null;

		switch (actionCode) {

		case ActionMap.CONFIG:
			return this.configManager.getAllConfig().toString();

		case ActionMap.UPLOAD_IMAGE:
		case ActionMap.UPLOAD_SCRAWL:
		case ActionMap.UPLOAD_VIDEO:
		case ActionMap.UPLOAD_FILE:
			conf = this.configManager.getConfig(actionCode);
			state = new Uploader(request, conf).doExec();
			break;

		case ActionMap.CATCH_IMAGE:
			conf = configManager.getConfig(actionCode);
			// String[] list = this.request.getParameterValues( (String)conf.get(
			// "fieldName" ) );
			// state = new ImageHunter( conf ).capture( list );
			// state = Base64Uploader.save(content, conf);
			state = new Uploader(request, conf).doExec();
			break;

		case ActionMap.LIST_IMAGE:
		case ActionMap.LIST_FILE:
			conf = configManager.getConfig(actionCode);
			int start = this.getStartIndex();
			state = new FileManager(conf).listFile(start);
			break;
		default:
			;
		}

		return state.toJsonString();

	}

	public int getStartIndex() {

		String start = this.request.getParameter("start");

		try {
			return Integer.parseInt(start);
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * callback参数验证
	 * 
	 * @param name callbackname
	 * @return boolean validCallbackName
	 */
	public boolean validCallbackName(String name) {

		if (name.matches(MATCH_ROLE)) {
			return true;
		}

		return false;
	}

}