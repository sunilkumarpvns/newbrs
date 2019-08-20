package com.elitecore.elitesm.datamanager.servicepolicy.auth.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class AuthPolicyDigestConfRelData implements  Serializable{
	
	private static final long serialVersionUID = 1L;
	private String digestConfId; 
	private String authPolicyId;
	
	
	public String getDigestConfId() {
		return digestConfId;
	}
	public void setDigestConfId(String digestConfId) {
		this.digestConfId = digestConfId;
	}
	public String getAuthPolicyId() {
		return authPolicyId;
	}
	public void setAuthPolicyId(String authPolicyId) {
		this.authPolicyId = authPolicyId;
	}
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ AuthPolicyDigestConfRelData ------------");
		writer.println("authPolicyId 	 :"+authPolicyId);
		writer.println("digestConfId    :"+digestConfId);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}
}
