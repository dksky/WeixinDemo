package com.ldk.wx.core.inputHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ldk.wx.core.pojo.ReceiveXmlEntity;

/**
 * 链接输入处理类
 * @author ldk
 *
 */
@Component
public class LinkInputHandler implements WXInputHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public String handleWithReceive(ReceiveXmlEntity receiveXmlEntity) {
		String openId = receiveXmlEntity.getFromUserName();
		logger.info("----------------收到用户：openId=" + openId + " 的链接消息----------------");
		return null;
	}

}
