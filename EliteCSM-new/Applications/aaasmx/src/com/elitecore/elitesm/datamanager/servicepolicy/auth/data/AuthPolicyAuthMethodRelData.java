package com.elitecore.elitesm.datamanager.servicepolicy.auth.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class AuthPolicyAuthMethodRelData extends BaseData implements  Serializable{

	
	private static final long serialVersionUID = 1L;
	private String authPolicyId;
	private Long authMethodTypeId;
	public String getAuthPolicyId() {
		return authPolicyId;
	}
	public void setAuthPolicyId(String authPolicyId) {
		this.authPolicyId = authPolicyId;
	}
	public Long getAuthMethodTypeId() {
		return authMethodTypeId;
	}
	public void setAuthMethodTypeId(Long authMethodTypeId) {
		this.authMethodTypeId = authMethodTypeId;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ AuthPolicyDigestConfRelData ------------");
		writer.println("authPolicyId 	 :"+authPolicyId);
		writer.println("authMethodTypeId :"+authMethodTypeId);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}
}
