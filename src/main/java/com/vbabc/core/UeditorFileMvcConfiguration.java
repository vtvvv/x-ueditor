package com.vbabc.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.vbabc.core.alibaba.aliyun.oss.properties.OssClientProperties;

import lombok.extern.slf4j.Slf4j;
/**
 * @author vtvvv
 */
@Slf4j
@Configuration
public class UeditorFileMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	if(OssClientProperties.showFileView && !OssClientProperties.useStatus) {
    		log.debug("开启图片映射展示");
	        registry.addResourceHandler("/ueditordir/**").addResourceLocations("file:/ueditordir/");
	        WebMvcConfigurer.super.addResourceHandlers(registry);
    	}
    }
}
