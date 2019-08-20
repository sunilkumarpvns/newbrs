package com.elitecore.elitesm.web.core.system.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HttpRequestWrapper extends HttpServletRequestWrapper{

	static final String MODULE = "[HTTP-REQ-WRAPPER] ";
	
	public HttpRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		if (name != null && (name.toLowerCase().startsWith("action:")||name.toLowerCase().startsWith("redirect:")||name.toLowerCase().startsWith("redirectaction:"))) {
			return null;
		}
		return super.getParameter(name);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getParameterMap() {
		Map map = super.getParameterMap();
		Map finalMap = new HashMap();
		Set<String> keyset = map.keySet();
		for (String name : keyset) {
			if(!(name.toLowerCase().startsWith("action:")||name.toLowerCase().startsWith("redirect:")||name.toLowerCase().startsWith("redirectaction:"))) {
				finalMap.put(name,map.get(name));
			}
		}
		return finalMap;
	}

	@Override
	public String[] getParameterValues(String name) {
		if (name != null && (name.toLowerCase().startsWith("action:")||name.toLowerCase().startsWith("redirect:")||name.toLowerCase().startsWith("redirectaction:"))) {
			return null;
		}
		return super.getParameterValues(name);
	}
}
