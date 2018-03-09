/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.ldk.wx.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ldk.wx.core.utils.SignUtil;
import com.ldk.wx.core.utils.accessToken.ObtainAccessTokenScheduler;

/**
 * 微信前台控制器。包含微信核心相关与公众号通讯的接口，以及用户可见的部分接口。
 * 
 * @author ldks
 * @version 2017-10-11
 */
@Controller
@RequestMapping(value = "${frontPath}/wx/core")
public class WeiXinFrontController {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ProcessRequest processRequest;
	@Autowired
	private ObtainAccessTokenScheduler obtainAccessTokenScheduler;

	/**
	 * 服务端接入微信初始握手认证接口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String signup(HttpServletRequest request, HttpServletRequest response) {
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");
		// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
		if (SignUtil.checkSignature(signature, timestamp, nonce)) {
			logger.info("微信服务端接入成功！");
			obtainAccessTokenScheduler.obtainAccessToken();
			return echostr;
		}
		return null;
	}

	/**
	 * 微信各种消息的接收接口。
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String handleMsgs(HttpServletRequest request, HttpServletResponse response) {
		// 调用核心业务类接收消息、处理消息
		String respMessage = StringUtils.EMPTY;
		try {
			respMessage = processRequest.process(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respMessage;
	}

}