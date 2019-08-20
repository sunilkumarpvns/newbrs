/*
 * @(#)DigestSession	04/08/2008
 * Elitecore Technologies Ltd.
 */
package com.elitecore.aaa.core.digest.session;

import java.util.HashMap;

public class DigestSession {
	
	private int identifier;
	private String userName;
	private String digestMethod;
	private boolean sessionToBeClosed;
	private String failureReason;
	
	private int radiusPacketType;
	private boolean removeSession = false;
	
	private HashMap<String, Object> parameters;
	
	public DigestSession(String username){
		this.userName = username;
		this.identifier =0;
		this.userName = null;
		this.digestMethod = null;
		this.sessionToBeClosed = false;
		this.removeSession = false;
		this.parameters = new HashMap<String,Object>();
	}

	public String getDigestMethod() {
		return digestMethod;
	}

	public void setDigestMethod(String digestMethod) {
		this.digestMethod = digestMethod;
	}

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public Object getParameter(String parameterName){
		return parameters.get(parameterName);
	}
	
	public void setParameter(String parameterName, Object parameterValue){
		parameters.put(parameterName, parameterValue);
	}
	public int getRadiusPacketType() {
		return radiusPacketType;
	}

	public void setRadiusPacketType(int radiusPacketType) {
		this.radiusPacketType = radiusPacketType;
	}

	public boolean isRemoveSession() {
		return removeSession;
	}

	public void setRemoveSession(boolean removeSession) {
		this.removeSession = removeSession;
	}

	public boolean isSessionToBeClosed() {
		return sessionToBeClosed;
	}

	public void setSessionToBeClosed(boolean sessionToBeClosed) {
		this.sessionToBeClosed = sessionToBeClosed;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	
}
