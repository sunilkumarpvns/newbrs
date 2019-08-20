package com.elitecore.aaa.radius.policies.servicepolicy.conf;

import static com.elitecore.aaa.util.constants.AAAServerConstants.NONE;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.base.Strings;

@XmlRootElement(name = "chargeable-user-identity")
public class ChargeableUserIdentityConfiguration {
	private @Nullable String authenticationCuiAttribute;
	private String cui;
	private @Nullable String accountingCuiAttribute;
	
	// this will only be available in case when advanced CUI expression is enabled 
	@Nullable private String expression;
	
	/* Transient properties */
	private List<String> authenticationCUIAttributes;
	
	public ChargeableUserIdentityConfiguration() {
		authenticationCuiAttribute = "";
		accountingCuiAttribute = "";
		cui = NONE;
		this.authenticationCUIAttributes = new ArrayList<String>();
	}

	@XmlElement(name = "authentication-attributes")
	public @Nullable String getAuthenticationCuiAttribute() {
		return authenticationCuiAttribute;
	}
	
	public void setAuthenticationCuiAttribute(String authenticationCUIAttribute) {
		this.authenticationCuiAttribute = authenticationCUIAttribute;
	}
	
	@XmlElement(name = "accounting-attributes")
	public @Nullable String getAccountingCuiAttribute() {
		return accountingCuiAttribute;
	}
	
	public void setAccountingCuiAttribute(String accountingCUIAttribute) {
		this.accountingCuiAttribute = accountingCUIAttribute;
	}
	
	@XmlElement(name = "cui")
	public String getCui() {
		return cui;
	}

	public void setCui(String cuiConfiguration) {
		this.cui = cuiConfiguration;
	}

	@XmlElement(name = "expression")
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public void postRead() {
		if (Strings.isNullOrBlank(authenticationCuiAttribute) == false) {
			this.authenticationCUIAttributes = Strings.splitter(',').trimTokens().split(authenticationCuiAttribute);
		}
	}
	
	@XmlTransient
	public List<String> getAuthenticationCuiAttributes() {
		return authenticationCUIAttributes;
	}
}
