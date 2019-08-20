package com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data;

import java.io.Serializable;

import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;

public class RoutingTableGatewayRelData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long routingTableId;
	private long gatewayId;
	private long weightage;
	
	private GatewayData gatewayData;
	
	public long getRoutingTableId() {
		return routingTableId;
	}
	public void setRoutingTableId(long routingTableId) {
		this.routingTableId = routingTableId;
	}
	public long getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(long gatewayId) {
		this.gatewayId = gatewayId;
	}
	public GatewayData getGatewayData() {
		return gatewayData;
	}
	public void setGatewayData(GatewayData gatewayData) {
		this.gatewayData = gatewayData;
	}
	public long getWeightage() {
		return weightage;
	}
	public void setWeightage(long weightage) {
		this.weightage = weightage;
	}
	
}
