package com.elitecore.netvertexsm.datamanager.gateway.gateway.data;

public interface IRadiusGatewayData {
	public long getGatewayId();
	public void setGatewayId(long gatewayId);
	public String getSharedSecret();
	public void setSharedSecret(String sharedSecret);
	public Long getMinLocalPort();
	public void setMinLocalPort(Long minLocalPort);
}
