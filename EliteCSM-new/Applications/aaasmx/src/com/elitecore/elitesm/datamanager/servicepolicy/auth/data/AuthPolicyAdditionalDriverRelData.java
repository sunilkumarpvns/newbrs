package com.elitecore.elitesm.datamanager.servicepolicy.auth.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;

public class AuthPolicyAdditionalDriverRelData extends BaseData implements Serializable {


	private static final long serialVersionUID = 1L;
	private String  authPolicyId; 
	private Long driverInstanceId;
	private Long orderNumber;
	private DriverInstanceData driverInstanceData; 
	
	public String getAuthPolicyId() {
		return authPolicyId;
	}
	public void setAuthPolicyId(String authPolicyId) {
		this.authPolicyId = authPolicyId;
	}
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public DriverInstanceData getDriverInstanceData() {
		return driverInstanceData;
	}
	public void setDriverInstanceData(DriverInstanceData driverInstanceData) {
		this.driverInstanceData = driverInstanceData;
	}
    
	
}
