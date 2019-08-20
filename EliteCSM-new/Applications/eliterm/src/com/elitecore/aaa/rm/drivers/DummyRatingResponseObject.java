package com.elitecore.aaa.rm.drivers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DummyRatingResponseObject implements IRatingResponseObject{

	private int responseCode;
	private String responseMessage;
	private Object responseObject;
	private Map<String,String> responseMap;
	
	public DummyRatingResponseObject() {
		responseCode = 0;
		responseMessage = "";
		responseObject = new Object();
		responseMap = new HashMap<String, String>();
	}
	
	public String get(String key) {
		return responseMap.get(key);
	}
	public int getResponseCode() {
		return this.responseCode;
	}
	public String getResponseMessage() {
		return this.responseMessage;		
	}
	public Object getResponseObject() {
		return this.responseObject;
	}
	public Set<String> keySet() {
		return this.responseMap.keySet();
	}	
	public String put(String key, String value) {
		return responseMap.put(key, value);
	}	
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;		
	}	
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;	
	}	
	public void setResponseObject(Object arg0) {
		this.responseObject = arg0;
	}

}
