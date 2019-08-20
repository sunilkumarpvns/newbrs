package com.elitecore.elitesm.datamanager.servicepolicy.acct.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;


public class AcctPolicyMainDriverRelData extends BaseData implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Long  acctPolicyId; 
	private Long driverInstanceId;
	private Integer weightage;
	private DriverInstanceData driverData;
	

	public Long getAcctPolicyId() {
		return acctPolicyId;
	}
	public void setAcctPolicyId(Long acctPolicyId) {
		this.acctPolicyId = acctPolicyId;
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
		writer.println("------------ AcctPolicyMainDriverRelData ------------");
		writer.println("acctPolicyId 	 :"+acctPolicyId);
		writer.println("driverInstanceId :"+driverInstanceId);
		writer.println("weightage:"+weightage);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}

	

}