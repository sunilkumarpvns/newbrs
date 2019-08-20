package com.elitecore.elitesm.datamanager.servicepolicy.acct.data;

import java.io.PrintWriter;
import java.io.StringWriter;
// default package

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;

/**
 * AcctFallBackDriverRelData entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class AcctPolicyAdditionalDriverRelData extends BaseData implements java.io.Serializable {

	// Fields
	private static final long serialVersionUID = 1L;
	private Long  acctPolicyId; 
	private Long driverInstanceId;
	private Long id;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

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
		writer.println("------------ AcctPolicyAdditionalDriverRelData ------------");
		writer.println("acctPolicyId 	 :"+acctPolicyId);
		writer.println("driverInstanceId :"+driverInstanceId);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}
	

}