package com.baidu.ueditor.define;

/**
 * 处理状态接口
 * 
 * @author hancong03@baidu.com
 *
 */
public interface State {
	/**
	 * 成功状态
	 * 
	 * @return
	 */
	public boolean isSuccess();

	/**
	 * 基础信息（字符串）
	 * 
	 * @param name
	 * @param val
	 */
	public void putInfo(String name, String val);

	/**
	 * 集成信息（数值）
	 * 
	 * @param name
	 * @param val
	 */
	public void putInfo(String name, long val);

	/**
	 * 内容输出
	 * 
	 * @return
	 */
	public String toJsonString();

}
