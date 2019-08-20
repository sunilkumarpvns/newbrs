package com.elitecore.netvertexsm.util;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.core.servicex.ServiceRequest;

public class BoDServiceRequest implements ServiceRequest {
	
	private Map<String, String> serviceAttributeMap;
	private Map<String, Object> serviceParameterMap;
	
	public BoDServiceRequest() {
		serviceAttributeMap = new HashMap<String, String>();  
	    serviceParameterMap = new HashMap<String, Object>();
	}

	@Override
	public void setParameter(String key, Object object) {
		serviceParameterMap.put(key, object);
	}

	@Override
	public Object getParameter(String key) {
		return serviceParameterMap.get(key);
	}

	@Override
	public long getRequestReceivedNano() {
		return 0;
	}
	
	public String getAttribute(String key) {
		return serviceAttributeMap.get(key);
	}
	
	public void setAttribute(String key, String value) {
		serviceAttributeMap.put(key, value);
	}

}
