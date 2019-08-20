package com.elitecore.elitesm.datamanager.servicepolicy.auth.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;

public class AuthPolicySecDriverRelData extends BaseData implements Serializable {


	private static final long serialVersionUID = 1L;
	private String  authPolicyId; 
	private Long cacheDriverInstId;
	private Long secondaryDriverInstId;
	private DriverInstanceData secondaryDriverData;
	private DriverInstanceData cacheDriverData;
	
	public DriverInstanceData getCacheDriverData() {
		return cacheDriverData;
	}
	public void setCacheDriverData(DriverInstanceData cacheDriverData) {
		this.cacheDriverData = cacheDriverData;
	}
	public String getAuthPolicyId() {
		return authPolicyId;
	}
	public void setAuthPolicyId(String authPolicyId) {
		this.authPolicyId = authPolicyId;
	}
	
	public Long getSecondaryDriverInstId() {
		return secondaryDriverInstId;
	}
	
	public void setSecondaryDriverInstId(Long secondaryDriverInstId) {
		this.secondaryDriverInstId = secondaryDriverInstId;
	}
	
	public Long getCacheDriverInstId() {
		return cacheDriverInstId;
	}
	public void setCacheDriverInstId(Long cacheDriverInstId) {
		this.cacheDriverInstId = cacheDriverInstId;
	}
	
	public DriverInstanceData getSecondaryDriverData() {
		return secondaryDriverData;
	}
	public void setSecondaryDriverData(DriverInstanceData secondaryDriverData) {
		this.secondaryDriverData = secondaryDriverData;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ AuthPolicySecDriverRelData ------------");
		writer.println("authPolicyId 	 :"+authPolicyId);
		writer.println("SecondaryDriverInstanceId :"+secondaryDriverInstId);
		writer.println("cashDriverInstId :"+cacheDriverInstId);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}
}
