package com.elitecore.core.servicex.base;

import java.net.InetAddress;

import com.elitecore.core.servicex.UDPServiceRequest;

public abstract class BaseUDPServiceRequest extends BaseServiceRequest implements
		UDPServiceRequest {
	private byte[] requestBytes;
	private InetAddress sourceAddress;
	private int sourcePort;

	public BaseUDPServiceRequest(byte[] requestBytes,
			InetAddress sourceAddress, int sourcePort) {
		super();
		this.requestBytes = requestBytes;
		this.sourceAddress = sourceAddress;
		this.sourcePort = sourcePort;
	}
	
	public byte[] getRequestBytes() {
		return requestBytes;
	}

	protected void setRequestBytes(byte[] requestBytes){
		this.requestBytes = requestBytes;
	}

	public InetAddress getSourceAddress() {
		return sourceAddress;
	}

	public int getSourcePort() {
		return sourcePort;
	}

}
