package com.vbabc.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vbabc.core.alibaba.aliyun.oss.properties.OssClientProperties;
import com.vbabc.core.ueditor.controller.UeditorController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fix
 */
@Slf4j
@Configuration
public class UeditorAutoConfiguration {

	/**
	 * 预留修改配置文件目录的入口
	 * 读取默认配置文件中，application.yml的ueditor.config-path属性
	 */
	public static String configPath;

	@Bean
	public ServletRegistrationBean<UeditorController> ueditorController() {
		
		UeditorController uc = new UeditorController();
		ServletRegistrationBean<UeditorController> srb = new ServletRegistrationBean<>(uc);
		log.info("Ueditor Server Url: [{}]",  OssClientProperties.serverUrl);
		srb.addUrlMappings( OssClientProperties.serverUrl);
		return srb;
	}

	@Value("${ueditor.config-path:/ueditor}")
	public void setConfigPath(String configPath) {
		UeditorAutoConfiguration.configPath = configPath;
	}

}
