package com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data;

import java.io.PrintWriter;
import java.io.StringWriter;
// default package

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;

/**
 * 
 * 
 * @author MyEclipse Persistence Tools
 */

public class DynaAuthPolicyESIRelData extends BaseData implements java.io.Serializable {

	// Fields


	private static final long serialVersionUID = 1L;
	private Long  dynaAuthPolicyId; 
	private String esiInstanceId;
	private ExternalSystemInterfaceInstanceData	externalSystemData;
	
	
	public Long getDynaAuthPolicyId() {
		return dynaAuthPolicyId;
	}
	public void setDynaAuthPolicyId(Long dynaAuthPolicyId) {
		this.dynaAuthPolicyId = dynaAuthPolicyId;
	}
	public String getEsiInstanceId() {
		return esiInstanceId;
	}
	public void setEsiInstanceId(String esiInstanceId) {
		this.esiInstanceId = esiInstanceId;
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
		writer.println("------------ DynaAuthESIRelData ------------");
		writer.println("dynaAuthPolicyId 	 :"+dynaAuthPolicyId);
		writer.println("esiInstanceId    :"+esiInstanceId);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}

}