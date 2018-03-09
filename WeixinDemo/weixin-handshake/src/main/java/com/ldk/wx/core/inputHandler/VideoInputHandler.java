package com.ldk.wx.core.inputHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ldk.wx.core.pojo.ReceiveXmlEntity;

/**
 * 视频输入处理类
 * @author ldk
 *
 */
@Component
public class VideoInputHandler implements WXInputHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public String handleWithReceive(ReceiveXmlEntity receiveXmlEntity) {
		String openId = receiveXmlEntity.getFromUserName();
		logger.info("----------------收到用户：openId=" + openId + " 的视频消息----------------");
		return null;
	}

}
