package com.elitecore.netvertexsm.datamanager.gateway.gateway.data;

public class RadiusGatewayData implements IRadiusGatewayData {
	private long gatewayId;
	private String sharedSecret;
	private Long minLocalPort;
	
	
	public long getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(long gatewayId) {
		this.gatewayId = gatewayId;
	}
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}
	public Long getMinLocalPort() {
		return minLocalPort;
	}
	public void setMinLocalPort(Long minLocalPort) {
		this.minLocalPort = minLocalPort;
	}
	
	
	
}
