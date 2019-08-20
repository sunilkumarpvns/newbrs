package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static com.elitecore.aaa.util.constants.AAAServerConstants.NONE;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "chargeable-user-identity")
@XmlType(propOrder = {"cui", "expression", "authenticationCuiAttribute", "accountingCuiAttribute"})
@ValidObject
public class ChargeableUserIdentityConfiguration implements Validator{
	private String authenticationCuiAttribute;
	private String cui;
	private String accountingCuiAttribute;
	
	// this will only be available in case when advanced CUI expression is enabled 
	private String expression;
	
	/* Transient properties */
	private List<String> authenticationCUIAttributes;
	
	public ChargeableUserIdentityConfiguration() {
		authenticationCuiAttribute = "";
		accountingCuiAttribute = "";
		cui = NONE;
		this.authenticationCUIAttributes = new ArrayList<String>();
	}

	@XmlElement(name = "authentication-attributes")
	public String getAuthenticationCuiAttribute() {
		return authenticationCuiAttribute;
	}
	
	public void setAuthenticationCuiAttribute(String authenticationCUIAttribute) {
		this.authenticationCuiAttribute = authenticationCUIAttribute;
	}
	
	@XmlElement(name = "accounting-attributes")
	public String getAccountingCuiAttribute() {
		return accountingCuiAttribute;
	}
	
	public void setAccountingCuiAttribute(String accountingCUIAttribute) {
		this.accountingCuiAttribute = accountingCUIAttribute;
	}
	
	@XmlElement(name = "cui")
	@Pattern(regexp = "NONE|Authenticated-Identity|Group|Profile-CUI|Advanced", 
		message = "Supported values of  RFC-4372-CUI configuration are NONE, Authenticated-Identity, Group, Profile-CUI and Advanced only")
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
	}
	
	@XmlTransient
	public List<String> getAuthenticationCuiAttributes() {
		return authenticationCUIAttributes;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if (AAAServerConstants.ADVANCED.equalsIgnoreCase(this.cui) && Strings.isNullOrBlank(getExpression())) {
			RestUtitlity.setValidationMessage(context, "Advanced CUI Expression must be specified When CUI attribute value is 'Advanced' in chargeable user identity configuration of Radius server policy's basic detail");
			isValid = false;
		}
		return isValid;
	}
}
