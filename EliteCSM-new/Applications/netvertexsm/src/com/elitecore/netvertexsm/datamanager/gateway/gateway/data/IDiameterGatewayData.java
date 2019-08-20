package com.elitecore.netvertexsm.datamanager.gateway.gateway.data;

public interface IDiameterGatewayData {
	public long getGatewayId();
	public void setGatewayId(long gatewayId);
	public String getHostId();
	public void setHostId(String hostId);
	public String getRealm();
	public void setRealm(String realm);
	public String getLocalAddress();
	public void setLocalAddress(String localAddress);
}
