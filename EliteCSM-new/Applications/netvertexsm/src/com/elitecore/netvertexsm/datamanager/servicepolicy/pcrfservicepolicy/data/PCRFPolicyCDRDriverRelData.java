package com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data;

import java.io.Serializable;

import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;

public class PCRFPolicyCDRDriverRelData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long pcrfPolicyId;
	private Long driverInstanceId;
	private Long weightage;
	private DriverInstanceData driverInstanceData;
	
	public Long getPcrfPolicyId() {
		return pcrfPolicyId;
	}
	
	public void setPcrfPolicyId(Long pcrfPolicyId) {
		this.pcrfPolicyId = pcrfPolicyId;
	}
	
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	public Long getWeightage() {
		return weightage;
	}
	
	public void setWeightage(Long weightage) {
		this.weightage = weightage;
	}
	
	public DriverInstanceData getDriverInstanceData() {
		return driverInstanceData;
	}
	
	public void setDriverInstanceData(DriverInstanceData driverInstanceData) {
		this.driverInstanceData = driverInstanceData;
	}
}
