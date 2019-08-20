package com.elitecore.netvertexsm.datamanager.gateway.gateway.data;

public class DiameterGatewayData implements IDiameterGatewayData {
	private long gatewayId;
	private String hostId;
	private String realm;
	private String localAddress;
	private Long requestTimeout;
	private Integer retransmissionCount; 
	private Long alternateHostId;
	private GatewayData alternateHostData;

	
	public String getLocalAddress() {
		return localAddress;
	}
	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}
	public long getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(long gatewayId) {
		this.gatewayId = gatewayId;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	public Long getRequestTimeout() {
		return requestTimeout;
	}
	public void setRequestTimeout(Long requestTimeout) {
		this.requestTimeout = requestTimeout;
	}
	public Integer getRetransmissionCount() {
		return retransmissionCount;
	}
	public void setRetransmissionCount(Integer retransmissionCount) {
		this.retransmissionCount = retransmissionCount;
	}
	public GatewayData getAlternateHostData() {
		return alternateHostData;
	}
	public void setAlternateHostData(GatewayData alternateHostData) {
		this.alternateHostData = alternateHostData;
	}
	public Long getAlternateHostId() {
		return alternateHostId;
	}
	public void setAlternateHostId(Long alternateHostId) {
		this.alternateHostId = alternateHostId;
	}
	
	
}