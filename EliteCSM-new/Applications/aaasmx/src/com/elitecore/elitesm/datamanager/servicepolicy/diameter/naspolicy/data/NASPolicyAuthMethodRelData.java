package com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.ws.rest.adapter.SupportedAuthMethodAdapter;

@XmlRootElement(name = "supported-method")
public class NASPolicyAuthMethodRelData extends BaseData implements  Serializable{

	private static final long serialVersionUID = 1L;
	private String nasPolicyId;
	private Long authMethodTypeId;
	
	@XmlTransient
	public String getNasPolicyId() {
		return nasPolicyId;
	}
	
	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
	}
	
	@XmlElement(name = "name")
	@XmlJavaTypeAdapter(value = SupportedAuthMethodAdapter.class)
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
		writer.println("nasPolicyId 	 :"+nasPolicyId);
		writer.println("authMethodTypeId :"+authMethodTypeId);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}
}
