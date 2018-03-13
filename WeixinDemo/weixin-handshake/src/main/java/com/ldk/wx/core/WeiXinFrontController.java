/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.ldk.wx.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.ldk.wx.core.pojo.AccessToken;
import com.ldk.wx.core.pojo.Button;
import com.ldk.wx.core.pojo.FirstLevelButton;
import com.ldk.wx.core.pojo.Menu;
import com.ldk.wx.core.pojo.OAuth2AccessToken;
import com.ldk.wx.core.pojo.SecondLevelButton;
import com.ldk.wx.core.pojo.WXOAuthUserInfo;
import com.ldk.wx.core.utils.SignUtil;
import com.ldk.wx.core.utils.WXOAuth2AuthorizeUtil;
import com.ldk.wx.core.utils.accessToken.ObtainAccessTokenScheduler;
import com.ldk.wx.core.utils.accessToken.WXUtil;
import com.ldk.wx.util.CacheUtils;
import com.ldk.wx.util.Global;

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
	 * 前端基础路径
	 */
	@Value("${frontPath}")
	protected String frontPath;
	
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

	/**
	 * 创建菜单
	 * 
	 * @return
	 */
	@RequestMapping(value="createMenu", method = RequestMethod.GET)
	@ResponseBody
	public String createMenu() {
		Menu menu = getWeiXinMenus();

		// 从缓存中取accessToken
		AccessToken at = (AccessToken) CacheUtils.get("accessToken");
		if (StringUtils.isNotBlank(at.getToken())) {
			// 更新微信菜单列表
			int resultCode = WXUtil.createMenu(menu,
					at.getToken()/* sa.getToken() */);
			// 判断菜单创建结果
			if (0 == resultCode) {
				logger.info("菜单创建成功！");
				return "菜单创建成功！";
			} else {
				logger.info("菜单创建失败，错误码：" + resultCode);
				return "菜单创建失败，错误码：" + resultCode;
			}
		}
		return "未获取到accessToken！";
	}

	/**
	 * 构造菜单Menu
	 * 
	 * @return
	 */
	private Menu getWeiXinMenus() {
		// 定义总菜单
		Menu menu = new Menu();
		// 定义一级菜单列表
		List<Button> firstLevelButtons = new ArrayList<Button>();

		// 一级菜单中包含二级菜单
		FirstLevelButton firstLevelButton = new FirstLevelButton();
		firstLevelButton.setName("知识点");
		// 二级菜单列表
		List<SecondLevelButton> secondLevelButtons = new ArrayList<SecondLevelButton>();
		SecondLevelButton secondLevelButton1 = new SecondLevelButton();
		secondLevelButton1.setName("数学");
		secondLevelButton1.setType("view");
		secondLevelButton1.setKey("11");
		secondLevelButton1.setUrl("http://www.baidu.com");
		secondLevelButtons.add(secondLevelButton1);
		SecondLevelButton secondLevelButton2 = new SecondLevelButton();
		secondLevelButton2.setName("语文");
		secondLevelButton2.setType("view");
		secondLevelButton2.setKey("12");
		secondLevelButton2.setUrl("http://www.baidu.com");
		secondLevelButtons.add(secondLevelButton2);
		// 将二级菜单列表加入一级菜单中，并将该一级菜单加入一级菜单列表中
		firstLevelButton.setSub_button(secondLevelButtons);
		firstLevelButtons.add(firstLevelButton);

		// 一级菜单中不包含二级菜单
		SecondLevelButton secondLevelButton3 = new SecondLevelButton();
		secondLevelButton3.setName("视频教程");
		secondLevelButton3.setType("view");
		secondLevelButton3.setKey("2");
		secondLevelButton3.setUrl("http://www.baidu.com");
		firstLevelButtons.add(secondLevelButton3);

		// 往总菜单中加入所有一级菜单
		menu.setButton(firstLevelButtons);
		return menu;
	}
	
	/**
	 * 微信网页授权，用于获取code，并返回获取openId的结果信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "obtainCodeForOpenId")
	@ResponseBody
	public String obtainCode(HttpServletRequest request, HttpServletResponse response, Model model) {
		String ret = "400";
        String retMsg = "操作失败";
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		String redirect = request.getParameter("redirect");
		OAuth2AccessToken oauth2AccessToken = null;
		String openId = StringUtils.EMPTY;
		try {
			//获取openId和accessToken
			oauth2AccessToken = WXOAuth2AuthorizeUtil.getOpenIdAndAccessToken(Global.getConfig("weixin.appId"), Global.getConfig("weixin.secret"), code);
			openId = oauth2AccessToken.getOpenid();
			
			//获取用户信息
			WXOAuthUserInfo userInfo = WXOAuth2AuthorizeUtil.getUserInfo(oauth2AccessToken.getAccess_token(), oauth2AccessToken.getOpenid());
			logger.info(userInfo.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(openId.equals(StringUtils.EMPTY)) {
			retMap.put("ret", ret);
			retMap.put("retMsg", retMsg);
		} else {
			//retMap.put("ret", "200");
			//retMap.put("retMsg", "获取成功！");
			retMap.put("openId", openId);
			String redirectUrl = getBasePath(request) + redirect;
			//System.err.println(redirectUrl);
			try {
				if(redirectUrl.contains("?")) {
					response.sendRedirect(redirectUrl + "&openId=" + openId);
				} else {
					response.sendRedirect(redirectUrl + "?openId=" + openId);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return new Gson().toJson(retMap);
	}
	
	/**
	 * 用于获取当前openId接口，需要传参serviceId。redirect。
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getOpenId")
	@ResponseBody
	public String getOpenId(HttpServletRequest request, HttpServletResponse response) {
		String ret = "400";
        String retMsg = "操作失败";
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		String state = "1";
		String redirect = request.getParameter("redirect");
		//获取snsapi_userinfo方式回调授权页面的url，用于重定向到微信授权。
		String url = WXOAuth2AuthorizeUtil.getBaseAuthorizeUrl(Global.getConfig("weixin.appId"), 
				getBasePath(request) + "/" + frontPath + "/wx/core/obtainCodeForOpenId?redirect=" + redirect, /////////////
				state);
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
			retMap.put("ret", ret);
			retMap.put("retMsg", retMsg);
			return new Gson().toJson(retMap);
		}
		return "";
	}
	
	/**
	 * 根据request获取基础路径basePath
	 * @param request
	 * @return
	 */
	protected String getBasePath(HttpServletRequest request) {
		String basePath = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
		return basePath;
	}
}