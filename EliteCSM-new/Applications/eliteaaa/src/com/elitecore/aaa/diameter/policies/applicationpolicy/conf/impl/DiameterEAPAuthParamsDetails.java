package com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.UpdateIdentityParamsDetail;

@XmlType(propOrder={})
public class DiameterEAPAuthParamsDetails {
	
	private String eapConfigId;
	private String userIdentityStr;
	private int iCaseSensitivity ;
	private UpdateIdentityParamsDetail identityParamsDetail;
	private String anonymousProfileIdentity;
	
	public DiameterEAPAuthParamsDetails() {
		this.identityParamsDetail = new UpdateIdentityParamsDetail();
	}

	@XmlElement(name = "eap-config",type = String.class)
	public String getEapConfigId() {
		return eapConfigId;
	}

	public void setEapConfigId(String eapConfigId) {
		this.eapConfigId = eapConfigId;
	}
	
	@XmlElement(name = "user-identity",type =String.class,defaultValue = "0:1")
	public String getUserIdentityStr() {
		return userIdentityStr;
	}

	public void setUserIdentityStr(String userIdentityStr) {
		this.userIdentityStr = userIdentityStr;
	}
	
	@XmlElement(name = "case",type = int.class)
	public int getiCaseSensitivity() {
		return iCaseSensitivity;
	}

	public void setiCaseSensitivity(int iCaseSensitivity) {
		this.iCaseSensitivity = iCaseSensitivity;
	}

	@XmlElement(name = "update-identity")
	public UpdateIdentityParamsDetail getIdentityParamsDetail() {
		return identityParamsDetail;
	}

	public void setIdentityParamsDetail(
			UpdateIdentityParamsDetail identityParamsDetail) {
		this.identityParamsDetail = identityParamsDetail;
	}

	public void setAnonymousProfileIdentity(String anonymousProfileIdentity) {
		this.anonymousProfileIdentity = anonymousProfileIdentity;
	}
	
	@XmlElement(name = "anonymous-identity", type = String.class)
	public String getAnonymousProfileIdentity() {
		return anonymousProfileIdentity;
	}
}
