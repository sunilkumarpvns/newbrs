package com.elitecore.elitesm.datamanager.servicepolicy.auth.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;

public class AuthPolicyExternalSystemRelData extends BaseData implements  Serializable{

	private static final long serialVersionUID = 1L;
	private String  authPolicyId; 
	private String esiInstanceId;
	private Integer weightage;
	private ExternalSystemInterfaceInstanceData externalSystemData;
	
	public String getAuthPolicyId() {
		return authPolicyId;
	}
	public void setAuthPolicyId(String authPolicyId) {
		this.authPolicyId = authPolicyId;
	}
	public String getEsiInstanceId() {
		return esiInstanceId;
	}
	public void setEsiInstanceId(String esiInstanceId) {
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
		writer.println("------------ AuthPolicyExternalSystemRelData ------------");
		writer.println("authPolicyId 	 :"+authPolicyId);
		writer.println("esiInstanceId    :"+esiInstanceId);
		writer.println("weightage        :"+weightage);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}
}
