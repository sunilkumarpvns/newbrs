package com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data;

import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class DiameterChargingDriverRealmsData extends BaseData{
	private Long realmRelId;
	private String realmName;
	private Long routingAction;
	private String authApplicationId;
	private String acctApplicationId;
	private Long dcDriverId;
	private Set<DiameterChargingDriverVendorData> realmVendorRelSet;
	private Set<DiameterChargingDriverPeerData> realmPeerRelSet;
	
	public Long getRealmRelId() {
		return realmRelId;
	}
	
	public void setRealmRelId(Long realmRelId) {
		this.realmRelId = realmRelId;
	}
	
	public String getRealmName() {
		return realmName;
	}
	
	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}
	
	public Long getRoutingAction() {
		return routingAction;
	}
	
	public void setRoutingAction(Long routingAction) {
		this.routingAction = routingAction;
	}
	
	public String getAuthApplicationId() {
		return authApplicationId;
	}
	
	public void setAuthApplicationId(String authApplicationId) {
		this.authApplicationId = authApplicationId;
	}
	
	public String getAcctApplicationId() {
		return acctApplicationId;
	}
	
	public void setAcctApplicationId(String acctApplicationId) {
		this.acctApplicationId = acctApplicationId;
	}
	
	public Long getDcDriverId() {
		return dcDriverId;
	}
	
	public void setDcDriverId(Long dcDriverId) {
		this.dcDriverId = dcDriverId;
	}

	public Set<DiameterChargingDriverVendorData> getRealmVendorRelSet() {
		return realmVendorRelSet;
	}

	public void setRealmVendorRelSet(
			Set<DiameterChargingDriverVendorData> realmVendorRelSet) {
		this.realmVendorRelSet = realmVendorRelSet;
	}

	public Set<DiameterChargingDriverPeerData> getRealmPeerRelSet() {
		return realmPeerRelSet;
	}

	public void setRealmPeerRelSet(
			Set<DiameterChargingDriverPeerData> realmPeerRelSet) {
		this.realmPeerRelSet = realmPeerRelSet;
	}
}
