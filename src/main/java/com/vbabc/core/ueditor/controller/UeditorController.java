package com.vbabc.core.ueditor.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.baidu.ueditor.ActionEnter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fix
 */
@Slf4j
@Component
public class UeditorController extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 默认资源目录
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");
		log.debug("初始化资源控制器");
		resp.getWriter().write(new ActionEnter(req).exec());
	}

	@Override
	public void init() throws ServletException {

	}

}
