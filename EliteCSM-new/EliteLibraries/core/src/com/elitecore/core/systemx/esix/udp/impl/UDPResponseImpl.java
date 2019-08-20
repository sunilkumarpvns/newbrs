package com.elitecore.core.systemx.esix.udp.impl;

import com.elitecore.core.systemx.esix.udp.UDPResponse;

public abstract class UDPResponseImpl implements UDPResponse {
	
	private byte[] responseBytes;
	private String esiName;
	
	public UDPResponseImpl(byte[] responseBytes,String esiName) {
		this.responseBytes = responseBytes;
		this.esiName = esiName;
	}
	
	@Override
	public byte[] getBytes() {
		return responseBytes;
	}
	
	@Override
	public String getCommunicatorName() {
		return esiName;
	}
}
