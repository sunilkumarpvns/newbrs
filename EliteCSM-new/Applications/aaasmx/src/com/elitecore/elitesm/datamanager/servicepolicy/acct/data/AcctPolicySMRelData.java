package com.elitecore.elitesm.datamanager.servicepolicy.acct.data;

import java.io.PrintWriter;
import java.io.StringWriter;
// default package

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;




public class AcctPolicySMRelData extends BaseData implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private Long  acctPolicyId; 
	private String sessionManagerInstanceId;
	

	public Long getAcctPolicyId() {
		return acctPolicyId;
	}
	public void setAcctPolicyId(Long acctPolicyId) {
		this.acctPolicyId = acctPolicyId;
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
		writer.println("------------ AcctPolicySMRelData ------------");
		writer.println("acctPolicyId 	 		 :"+acctPolicyId);
		writer.println("sessionManagerInstanceId :"+sessionManagerInstanceId);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}

}