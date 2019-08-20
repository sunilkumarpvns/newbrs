package com.elitecore.elitesm.datamanager.servicepolicy.acct.data;

import java.io.PrintWriter;
import java.io.StringWriter;
// default package

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;

/**
 * acctPolicyEsiRel entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class AcctPolicyExternalSystemRelData extends BaseData implements java.io.Serializable {

	// Fields


	private static final long serialVersionUID = 1L;
	private Long  acctPolicyId; 
	private Long esiInstanceId;
	private Integer weightage;
	private ExternalSystemInterfaceInstanceData	externalSystemData;
	
	public Long getAcctPolicyId() {
		return acctPolicyId;
	}
	public void setAcctPolicyId(Long acctPolicyId) {
		this.acctPolicyId = acctPolicyId;
	}
	public Long getEsiInstanceId() {
		return esiInstanceId;
	}
	public void setEsiInstanceId(Long esiInstanceId) {
		this.esiInstanceId = esiInstanceId;
	}
	public Integer getWeightage() {
		return weightage;
	}
	public void setWeightage(Integer weightage) {
		this.weightage = weightage;
	}
	
	public ExternalSystemInterfaceInstanceData getExternalSystemData() {
		return externalSystemData;
	}
	public void setExternalSystemData(
			ExternalSystemInterfaceInstanceData externalSystemData) {
		this.externalSystemData = externalSystemData;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ AcctPolicyExternalSystemRelData ------------");
		writer.println("acctPolicyId 	 :"+acctPolicyId);
		writer.println("esiInstanceId    :"+esiInstanceId);
		writer.println("weightage        :"+weightage);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}

}