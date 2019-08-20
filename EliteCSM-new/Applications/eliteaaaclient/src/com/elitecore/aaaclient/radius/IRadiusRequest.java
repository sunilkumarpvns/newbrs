package com.elitecore.aaaclient.radius;

public interface IRadiusRequest {

	public String getServerAddress();
	
	public int getRequestPort();
	public int getRetryCount();
	public int getRequestTimeout();
	
	public String getSharedSecret();
	
	public int getRequestTypeId();
	public String getRequestTypeString();
	
	public String getTestCaseName();
	
	public String getExecutionStatusText();
	
	public int getExecutionStatus();
	
	public int getFinalResult();
	
	public String getFinalResultText() ;
	
	
	
}
