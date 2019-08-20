package com.elitecore.aaa.radius.service.handlers;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author narendra.pathai
 *
 */
public class HandlerSession {
	private Map<String, Object> sessionData = new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key){
		return (T) sessionData.get(key);
	}
	
	public HandlerSession put(String key, Object value){
		sessionData.put(key, value);
		return this;
	}
}
