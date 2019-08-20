package com.elitecore.elitesm.datamanager.servicepolicy.auth.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;

public class AuthPolicyMainDriverRelData extends BaseData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String  authPolicyId; 
	private Long driverInstanceId;
	private Integer weightage;
	private DriverInstanceData driverData;
	
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
	public Integer getWeightage() {
		return weightage;
	}
	public void setWeightage(Integer weightage) {
		this.weightage = weightage;
	}	
	public DriverInstanceData getDriverData() {
		return driverData;
	}
	public void setDriverData(DriverInstanceData driverData) {
		this.driverData = driverData;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ AuthPolicyMainDriverRelData ------------");
		writer.println("authPolicyId 	 :"+authPolicyId);
		writer.println("driverInstanceId :"+driverInstanceId);
		writer.println("weightage :"+weightage);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}
	
}
