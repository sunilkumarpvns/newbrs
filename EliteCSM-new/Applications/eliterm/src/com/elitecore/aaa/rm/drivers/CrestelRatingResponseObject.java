package com.elitecore.aaa.rm.drivers;

import java.util.Set;

import com.elitecore.ratingapi.util.IResponseObject;

public class CrestelRatingResponseObject implements IRatingResponseObject{
	
	IResponseObject responseObject;
	
	public CrestelRatingResponseObject(IResponseObject responseObject) {
		this.responseObject = responseObject;
	}
	
	public String get(String arg) {
		return responseObject.get(arg);
	}

	
	public int getResponseCode() {
		return responseObject.getResponseCode();
	}

	
	public String getResponseMessage() {
		return responseObject.getResponseMessage();
	}

	
	public Object getResponseObject() {
		return responseObject.getResponseObject();
	}

	
	public Set keySet() {
		return responseObject.keySet();
	}

	
	public String put(String key, String value) {
		return responseObject.put(key, value);
	}

	
	public void setResponseCode(int responseCode) {
		responseObject.setResponseCode(responseCode);
		
	}

	
	public void setResponseMessage(String responseMessage) {
		responseObject.setResponseMessage(responseMessage);
	}

	
	public void setResponseObject(Object respObject) {
		this.responseObject.setResponseObject(respObject);
	}
	
	public void setRatingResponseObject(IResponseObject responseObject){
		this.responseObject = responseObject;
	}

}
