package com.elitecore.elitesm.datamanager.servicepolicy.auth.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class AuthPolicySMRelData extends BaseData implements Serializable{
	private static final long serialVersionUID = 1L;
	private String  authPolicyId; 
	private String sessionManagerInstanceId;
	
	public String getAuthPolicyId() {
		return authPolicyId;
	}
	public void setAuthPolicyId(String authPolicyId) {
		this.authPolicyId = authPolicyId;
	}
	public String getSessionManagerInstanceId() {
		return sessionManagerInstanceId;
	}
	public void setSessionManagerInstanceId(String sessionManagerInstanceId) {
		this.sessionManagerInstanceId = sessionManagerInstanceId;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ AuthPolicySMRelData ------------");
		writer.println("authPolicyId 	 		 :"+authPolicyId);
		writer.println("sessionManagerInstanceId :"+sessionManagerInstanceId);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}
}
