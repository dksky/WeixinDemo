package com.ldk.wx.util;

import java.util.Map;
import com.google.common.collect.Maps;

public class CacheUtils {
	
	/**
	 * 保存全局属性值
	 */
	private static Map<String, Object> map = Maps.newHashMap();
	
	/**
	 * 获取配置
	 * @see 
	 */
	public static Object get(String key) {
		return map.get(key);
	}

	public static void put(String key, Object value) {
		map.put(key, value);
	}
}
