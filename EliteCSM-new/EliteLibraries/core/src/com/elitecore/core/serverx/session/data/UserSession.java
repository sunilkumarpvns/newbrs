package com.elitecore.core.serverx.session.data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class UserSession implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String sessionId;
	private String sessionGroupKey;
	
	private String userIdentity;
	
	private Date sessionStartTime;
	
	private Date sessionCreateTime;
	private Date sessionUpdateTime;
	
	private Map<String, Object> additionalFields; //NOSONAR
	
	public UserSession() {
		additionalFields = new HashMap<String, Object>();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionGroupKey() {
		return sessionGroupKey;
	}

	public void setSessionGroupKey(String sessionGroupKey) {
		this.sessionGroupKey = sessionGroupKey;
	}

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public Date getSessionStartTime() {
		return sessionStartTime;
	}

	public void setSessionStartTime(Date sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}

	public Date getSessionCreateTime() {
		return sessionCreateTime;
	}

	public void setSessionCreateTime(Date sessionCreateTime) {
		this.sessionCreateTime = sessionCreateTime;
	}

	public Date getSessionUpdateTime() {
		return sessionUpdateTime;
	}

	public void setSessionUpdateTime(Date sessionUpdateTime) {
		this.sessionUpdateTime = sessionUpdateTime;
	}

	public Map<String, Object> getAdditionalFields() {
		return additionalFields;
	}

	public void setAdditionalFields(Map<String, Object> additionalFields) {
		this.additionalFields = additionalFields;
	}
	
	public void addAdditionalFields(String key,Object value){
		this.additionalFields.put(key, value);
	}
	
	public Object getAttributeValue(String key){
		return additionalFields.get(key);
	}
	
	
}
