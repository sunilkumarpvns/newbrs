package com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data;

import java.io.Serializable;

import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;

public class PCRFServicePolicySyGatewayRelData implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long pcrfPolicyId;
	private Long syGatewayId;
	private Long weightage;
	private Integer orderNo;
	private GatewayData gatewayData;
	
	public Long getPcrfPolicyId() {
		return pcrfPolicyId;
	}
	public void setPcrfPolicyId(Long pcrfPolicyId) {
		this.pcrfPolicyId = pcrfPolicyId;
	}
	public Long getSyGatewayId() {
		return syGatewayId;
	}
	public void setSyGatewayId(Long syGatewayId) {
		this.syGatewayId = syGatewayId;
	}
	public Long getWeightage() {
		return weightage;
	}
	public void setWeightage(Long weightage) {
		this.weightage = weightage;
	}
	public GatewayData getGatewayData() {
		return gatewayData;
	}
	public void setGatewayData(GatewayData gatewayData) {
		this.gatewayData = gatewayData;
	}
	public Integer getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
}