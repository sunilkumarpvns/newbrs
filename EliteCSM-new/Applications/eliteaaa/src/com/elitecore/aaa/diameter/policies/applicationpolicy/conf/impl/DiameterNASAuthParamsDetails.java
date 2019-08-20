package com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.impl.UpdateIdentityParamsDetail;

@XmlType(propOrder={})
public class DiameterNASAuthParamsDetails {
	
	private String userIdentityStr;
	private int iCaseSensitivity ;
	private UpdateIdentityParamsDetail identityParamsDetail;
	
	private ArrayList<Integer> authMethodHandlerTypes;
	
	private String strUserNameConfiguration;
	private String strUserNameResponseAttrs;
	private String anonymousProfileIdentity;
	
	public DiameterNASAuthParamsDetails() {
		this.identityParamsDetail = new UpdateIdentityParamsDetail();
		this.authMethodHandlerTypes = new ArrayList<Integer>();
	}
	
	@XmlElementWrapper(name ="supported-methods")
	@XmlElement(name = "method")
	public ArrayList<Integer> getAuthMethodHandlerTypes() {
		return authMethodHandlerTypes;
	}


	public void setAuthMethodHandlerTypes(ArrayList<Integer> authMethodHandlerTypes) {
		this.authMethodHandlerTypes = authMethodHandlerTypes;
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
	
	@XmlElement(name = "user-name",type = String.class)
	public String getStrUserNameConfiguration() {
		return strUserNameConfiguration;
	}


	public void setStrUserNameConfiguration(String strUserNameConfiguration) {
		this.strUserNameConfiguration = strUserNameConfiguration;
	}


	@XmlElement(name="user-name-response-attribute",type=String.class)
	public String getStrUserNameResponseAttrs() {
		return strUserNameResponseAttrs;
	}


	public void setStrUserNameResponseAttrs(String strUserNameResponseAttrs) {
		this.strUserNameResponseAttrs = strUserNameResponseAttrs;
	}

	@XmlElement(name = "anonymous-identity", type = String.class)
	public String getAnonymousProfileIdentity() {
		return anonymousProfileIdentity;
	}
	
	public void setAnonymousProfileIdentity(String annonymousProfileIdentity) {
		this.anonymousProfileIdentity = annonymousProfileIdentity;
	}
	
}
