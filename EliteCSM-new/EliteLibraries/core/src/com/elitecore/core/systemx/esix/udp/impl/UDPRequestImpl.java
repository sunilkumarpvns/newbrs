package com.elitecore.core.systemx.esix.udp.impl;

import com.elitecore.core.systemx.esix.udp.UDPRequest;
import com.elitecore.core.systemx.esix.udp.UDPResponse;
import com.elitecore.core.systemx.esix.udp.UDPResponseListener;

public abstract class UDPRequestImpl implements UDPRequest {

	private byte[] requestBytes;
	private UDPResponse response;
	private UDPResponseListener responseListener;
	private long requestSentTime;
	private long lastUpdateTime;
	private int retryCount;
	
	public UDPRequestImpl(byte[] requestBytes,UDPResponseListener responseListener) {
		this.requestBytes = requestBytes;
		this.responseListener = responseListener;
	}
	
	public UDPRequestImpl(byte[] requestBytes) {
		this.requestBytes = requestBytes;
	}
	
	@Override
	public byte[] getBytes() {
		return requestBytes;
	}

	@Override
	public UDPResponse getResponse() {
		return response;
	}

	@Override
	public void setResponse(UDPResponse response) {
		this.response = response;
	}
	
	@Override
	public UDPResponseListener getResponseListener() {
		return responseListener;
	}
	
	@Override
	public void setRequestSentTime(long lTimeInMilli) {
		this.requestSentTime = lTimeInMilli;
		this.setLastUpdateTime(lTimeInMilli);
	}
	
	@Override
	public long getRequestSentTime() {
		return requestSentTime;
	}
	
	@Override
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	@Override
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public void incrementRetryCount() {
		this.retryCount++;
	}
	
	@Override
	public int getRetryCount() {
		return retryCount;
	}
	
	@Override
	public void resetRetryCount() {
		retryCount = 0;
	}

	@Override
	public boolean validate(UDPResponse response) {
		return true;
	}
}
