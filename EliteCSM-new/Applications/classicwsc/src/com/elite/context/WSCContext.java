package com.elite.context;

import java.util.HashMap;

import com.elite.config.AAAConfig;
import com.elite.config.LDAPConfig;
import com.elite.config.WSCConfig;

public class WSCContext {
	private HashMap<String, Object> wsc_context = new HashMap<String, Object>();
	public Object getAttribute(String key)
	{
		return wsc_context.get(key);
	}
	public void setAttribute(String key, Object obj)
	{
		wsc_context.put(key, obj);
	}
	public Object removeAttribute(String key)
	{
		return wsc_context.remove(key);
	}
}
