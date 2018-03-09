package com.ldk.wx.core.inputHandler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ldk.wx.core.WXBeanFactory;
import com.ldk.wx.core.pojo.ReceiveXmlEntity;

/**
 * 事件输入处理类
 * @author ldk
 *
 */
@Component
public class EventInputHandler implements WXInputHandler {
	@Autowired
	private WXBeanFactory wxBeanFactory;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public String handleWithReceive(ReceiveXmlEntity receiveXmlEntity) {
		String event = receiveXmlEntity.getEvent();
		String content = StringUtils.EMPTY;
		//订阅
    	if (event.equals("subscribe")) {
    		content = subscribe(receiveXmlEntity);
    	}
    	//取消订阅
    	else if (event.equals("unsubscribe")) {
    		content = unsubscribe(receiveXmlEntity);
    	}
    	//点击菜单
    	else if (event.equals("CLICK")) {
    		content = clickMenu(receiveXmlEntity);
    	}
		
		return content;
	}
	
	/**
	 * 关注事件
	 * @param receiveXmlEntity
	 * @return
	 */
	private String subscribe(ReceiveXmlEntity receiveXmlEntity) {
		String openId = receiveXmlEntity.getFromUserName();
		logger.info("----------------用户：openId=" + openId + " 已关注!-----------------");
		return null;
	}
	
	/**
	 * 取消关注事件
	 * @param receiveXmlEntity
	 * @return
	 */
	private String unsubscribe(ReceiveXmlEntity receiveXmlEntity) {
		String openId = receiveXmlEntity.getFromUserName();
		logger.info("----------------用户：openId=" + openId + " 取消关注!-----------------");
		return null;
	}
	
	/**
	 * 菜单点击事件
	 * @param receiveXmlEntity
	 * @return
	 */
	private String clickMenu(ReceiveXmlEntity receiveXmlEntity) {
		//根据接收的事件key值确定回复消息内容。
		String eventKey = receiveXmlEntity.getEventKey();
		StringBuffer content = new StringBuffer();
		
		int keyValue = Integer.parseInt(eventKey);
		logger.info("----------------eventKey:" + eventKey + "----------------");
		return content.toString();
	}
}
