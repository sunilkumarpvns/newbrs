package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@ValidObject
@XmlRootElement(name = "radius-policy")
@XmlType(propOrder = {"name", "description", "status", "supportedMessages", "authenticationRuleset", "accountingRuleset",
		"userIdentity", "validatePacket", "defaultAuthResponseBehavior", "hotlinePolicy", "defaultAcctResponseBehavior", 
		"authResponseAttributes", "acctResponseAttributes", "sessionManagerId", "cuiConfiguration", "authenticationPolicyData",
		"accountingPolicyData"})
public class RadiusServicePolicyData implements Differentiable, Validator {
	
	private String policyId;
	
	@Pattern(regexp = RestValidationMessages.NAME_REGEX , message = RestValidationMessages.NAME_INVALID)
	@NotEmpty(message = "Policy Name must be specified")
	private String name;
	
	private String description;
	
	@Valid
	private SupportedMessages supportedMessages;
	
	private String authenticationRuleset;
	private String accountingRuleset;
	private String validatePacket;

	private String authResponseAttributes;
	private String acctResponseAttributes;

	@Valid
	private AuthenticationPolicyData authenticationPolicyData;
	private String defaultAuthResponseBehavior;
	private String defaultAcctResponseBehavior;
	private String sessionManagerId;
	private String hotlinePolicy;
	
	@Valid
	private AccountingPolicyData accountingPolicyData;
	private String userIdentity;
	
	@Valid
	private ChargeableUserIdentityConfiguration cuiConfiguration;
	
	/* Transient properties */
	private List<String> userIdentities = new ArrayList<String>(0);
	private Set<Integer> requiredDriversIds = new HashSet<Integer>();

	private String status;
	
	public RadiusServicePolicyData() {
		description = RestUtitlity.getDefaultDescription();
	}
	
	@XmlElement(name = "status")
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	@XmlElement(name = "user-identity")
	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}
	
	@XmlTransient
	public List<String> getUserIdentities() {
		return userIdentities;
	}

	@XmlElement(name = "hotline-policy")
	public String getHotlinePolicy() {
		return hotlinePolicy;
	}

	public void setHotlinePolicy(String hotlinePolicy) {
		this.hotlinePolicy = hotlinePolicy;
	}

	@XmlElement(name = "default-auth-response-behavior")
	public String getDefaultAuthResponseBehavior() {
		return defaultAuthResponseBehavior;
	}

	public void setDefaultAuthResponseBehavior(String defaultResponseBehaviour) {
		this.defaultAuthResponseBehavior = defaultResponseBehaviour;
	}
	
	
	@XmlElement(name = "default-acct-response-behavior")
	public String getDefaultAcctResponseBehavior() {
		return defaultAcctResponseBehavior;
	}

	public void setDefaultAcctResponseBehavior(String defaultAcctResponseBehavior) {
		this.defaultAcctResponseBehavior = defaultAcctResponseBehavior;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name = "supported-messages")
	public SupportedMessages getSupportedMessages() {
		return supportedMessages;
	}

	public void setSupportedMessages(SupportedMessages supportedMessages) {
		this.supportedMessages = supportedMessages;
	}

	@XmlElement(name = "authentication-ruleset")
	public String getAuthenticationRuleset() {
		return authenticationRuleset;
	}

	public void setAuthenticationRuleset(String authenticationRuleset) {
		this.authenticationRuleset = authenticationRuleset;
	}
	
	@XmlElement(name = "accounting-ruleset")
	public String getAccountingRuleset() {
		return accountingRuleset;
	}

	public void setAccountingRuleset(String accountingRuleset) {
		this.accountingRuleset = accountingRuleset;
	}	

	
	@XmlElement(name = "validate-packet")
	@Pattern(regexp = "true|false", message="Invalid Value of Validate Packet. Value could be 'true' and 'false'")
	public String getValidatePacket() {
		return validatePacket;
	}

	public void setValidatePacket(String validatePacket) {
		this.validatePacket = validatePacket;
	}

	@XmlElement(name = "auth-response-attributes")
	public String getAuthResponseAttributes() {
		return authResponseAttributes;
	}
	
	public void setAuthResponseAttributes(String authResponseAttributes) {
		this.authResponseAttributes = authResponseAttributes;
	}
	
	@XmlElement(name = "acct-response-attributes")
	public String getAcctResponseAttributes() {
		return acctResponseAttributes;
	}
	
	public void setAcctResponseAttributes(String acctResponseAttributes) {
		this.acctResponseAttributes = acctResponseAttributes;
	}
	
	@XmlElement(name = "authentication-policy")
	public AuthenticationPolicyData getAuthenticationPolicyData() {
		return authenticationPolicyData;
	}

	public void setAuthenticationPolicyData(AuthenticationPolicyData authenticationPolicyData) {
		this.authenticationPolicyData = authenticationPolicyData;
	}
	
	@XmlElement(name = "session-manager")
	public String getSessionManagerId() {
		return sessionManagerId;
	}

	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}

	@XmlElement(name = "accounting-policy")
	public AccountingPolicyData getAccountingPolicyData() {
		return accountingPolicyData;
	}

	public void setAccountingPolicyData(AccountingPolicyData accountingPolicyData) {
		this.accountingPolicyData = accountingPolicyData;
	}
	
	@XmlElement(name = "chargeable-user-identity")
	public ChargeableUserIdentityConfiguration getCuiConfiguration() {
		return cuiConfiguration;
	}

	public void setCuiConfiguration(ChargeableUserIdentityConfiguration cuiAttributes) {
		this.cuiConfiguration = cuiAttributes;
	}

	public void postRead() {
	}

	public void registerRequiredDriver(int driverId) {
		requiredDriversIds.add(driverId);
	}

	@XmlTransient
	public Set<Integer> getRequiredDriverIds() {
		return requiredDriversIds;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		if(authenticationPolicyData != null){
			object.put("Auth Service Flow", authenticationPolicyData.toJson());
		}
		
		if(accountingPolicyData != null){
			object.put("Acct Service Flow", accountingPolicyData.toJson());
		}
		return object;
	}

	@XmlTransient
	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {

		boolean isValid = true;

		if (Strings.isNullOrEmpty(status)) {
			RestUtitlity.setValidationMessage(context, "Status must be specified. It can be 'ACTIVE' or 'INACTIVE' only");
			isValid = false;
		} else if (RestValidationMessages.ACTIVE.equalsIgnoreCase(status) == false && RestValidationMessages.INACTIVE.equalsIgnoreCase(status) == false) {
			RestUtitlity.setValidationMessage(context, "Specify Valid Value of Status. It can be 'ACTIVE' or 'INACTIVE' only");
			isValid = false;
		}


		if( this.supportedMessages != null){

			if (RestValidationMessages.TRUE.equals(this.supportedMessages.getAuthenticationMessageEnabled()) && Strings.isNullOrBlank(getAuthenticationRuleset())) {
				RestUtitlity.setValidationMessage(context, "Authentication Ruleset must be specified");
				isValid = false;
			}

			if (RestValidationMessages.TRUE.equals(this.supportedMessages.getAccountingMessageEnabled()) && Strings.isNullOrBlank(getAccountingRuleset())) {
				RestUtitlity.setValidationMessage(context, "Accounting Ruleset must be specified");
				isValid = false;
			}

			if (RestValidationMessages.TRUE.equals(this.supportedMessages.getAuthenticationMessageEnabled()) && this.authenticationPolicyData == null){
				RestUtitlity.setValidationMessage(context, "Authentication Policy Flow must be specified when Authentication Message is enabled");
				isValid = false;
			}

			if (RestValidationMessages.TRUE.equals(this.supportedMessages.getAccountingMessageEnabled()) && this.accountingPolicyData == null){
				RestUtitlity.setValidationMessage(context, "Accounting Policy Flow must be specified when Accounting Message is enabled");
				isValid = false;
			}

			if(RestValidationMessages.FALSE.equals(this.supportedMessages.getAccountingMessageEnabled())){
				this.setAccountingPolicyData(null);
			}

			if(RestValidationMessages.FALSE.equals(this.supportedMessages.getAuthenticationMessageEnabled())){
				this.setAuthenticationPolicyData(null);
			}
		}


		if(Strings.isNullOrBlank(getDefaultAuthResponseBehavior()) == false){
			if (AuthResponseBehaviors.REJECT.name().equals(getDefaultAuthResponseBehavior()) == false &&
					AuthResponseBehaviors.DROP.name().equals(getDefaultAuthResponseBehavior()) == false &&
					AuthResponseBehaviors.HOTLINE.name().equals(getDefaultAuthResponseBehavior()) == false) {
				RestUtitlity.setValidationMessage(context, "Supported values for default auth response behaviour is REJECT, DROP and HOTLINE only");
				isValid = false;
			}
		}else{
			RestUtitlity.setValidationMessage(context, "Default Authentication Response Behavior must be specified");
			isValid = false;
		}


		if(Strings.isNullOrBlank(this.getDefaultAcctResponseBehavior()) == false){
			if (AcctResponseBehaviors.RESPONSE.name().equals(getDefaultAcctResponseBehavior()) == false &&
					AcctResponseBehaviors.DROP.name().equals(getDefaultAcctResponseBehavior()) == false) {
				RestUtitlity.setValidationMessage(context, "Supported values for default acct response behaviour is RESPONSE and DROP only");
				isValid = false;
			}
		}else{
			RestUtitlity.setValidationMessage(context, "Default Accounting Response Behavior must be specified");
			isValid = false;
		}

		if (AuthResponseBehaviors.HOTLINE.name().equals(getDefaultAuthResponseBehavior()) && Strings.isNullOrBlank(getHotlinePolicy())) {
			RestUtitlity.setValidationMessage(context, "Hotline Policy must be specified when Default Response Behaviour is HOTLINE");
			isValid = false;
		}


		if( Strings.isNullOrBlank(sessionManagerId) == false && "--None--".equals(sessionManagerId) == false ){
			try{
				SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
				sessionManagerBLManager.getSessionManagerDataByName(sessionManagerId);
			}catch(Exception e){
				RestUtitlity.setValidationMessage(context, "Invalid Session Manager value in basic details");
				isValid = false;
			}
		}

		return isValid;
	}
}