package com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class DiameterChargingDriverVendorData extends BaseData{
	private Long vendorRelId;
	private Long vendorId;
	private Long authApplicationId;
	private Long acctApplicationId;
	private Long realmRelId;
	
	public Long getVendorRelId() {
		return vendorRelId;
	}
	
	public void setVendorRelId(Long vendorRelId) {
		this.vendorRelId = vendorRelId;
	}
	
	public Long getVendorId() {
		return vendorId;
	}
	
	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}
	
	public Long getAuthApplicationId() {
		return authApplicationId;
	}
	
	public void setAuthApplicationId(Long authApplicationId) {
		this.authApplicationId = authApplicationId;
	}
	
	public Long getAcctApplicationId() {
		return acctApplicationId;
	}
	
	public void setAcctApplicationId(Long acctApplicationId) {
		this.acctApplicationId = acctApplicationId;
	}
	
	public Long getRealmRelId() {
		return realmRelId;
	}
	
	public void setRealmRelId(Long realmRelId) {
		this.realmRelId = realmRelId;
	}
}
