package com.elitecore.core.systemx.esix.udp;

public interface UDPRequest {
	int getIdentifier();
	byte[] getBytes();
	UDPResponse getResponse();
	void setResponse(UDPResponse response);
	UDPResponseListener getResponseListener();
	long getRequestSentTime();
	void setRequestSentTime(long lTimeInMilli);
	void setLastUpdateTime(long lastUpdateTime);
	long getLastUpdateTime();
	void incrementRetryCount();
	void resetRetryCount();
	int getRetryCount();
	boolean validate(UDPResponse response);
}
