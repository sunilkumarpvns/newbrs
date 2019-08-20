package com.elitecore.aaa.radius.policies.servicepolicy.conf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.policies.servicepolicy.AuthResponseBehaviors;
import com.elitecore.aaa.radius.policies.servicepolicy.AuthResponseBehaviors.AuthResponseBehaviorsXMLAdapter;
import com.elitecore.aaa.radius.service.acct.policy.AcctResponseBehaviors;
import com.elitecore.aaa.radius.service.acct.policy.conf.AccountingPolicyData;
import com.elitecore.aaa.radius.service.auth.policy.conf.AuthenticationPolicyData;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;

@XmlRootElement(name = "radius-policy")
public class RadiusServicePolicyData implements Differentiable{
	private static final char MULTI_USER_IDENTITY_SEPARATOR = ',';

	private String name;
	private String description;
	private SupportedMessages supportedMessages;
	private String authenticationRuleset;
	private String accountingRuleset;
	private boolean validatePacket;

	private String authResponseAttributes;
	private String acctResponseAttributes;

	private AuthenticationPolicyData authenticationPolicyData;
	private AuthResponseBehaviors defaultAuthResponseBehavior = AuthResponseBehaviors.REJECT;
	private AcctResponseBehaviors defaultAcctResponseBehavior = AcctResponseBehaviors.DROP;
	private String sessionManagerId;
	private String hotlinePolicy;
	private AccountingPolicyData accountingPolicyData;
	private String userIdentity;
	private ChargeableUserIdentityConfiguration cuiConfiguration;
	
	/* Transient properties */
	private List<String> userIdentities = new ArrayList<String>(0);
	private Set<String> requiredDriversIds = new HashSet<String>();
	private AAAServerContext aaaServerContext;

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
	@XmlJavaTypeAdapter(value = AuthResponseBehaviorsXMLAdapter.class)
	public AuthResponseBehaviors getDefaultAuthResponseBehavior() {
		return defaultAuthResponseBehavior;
	}

	public void setDefaultAuthResponseBehavior(AuthResponseBehaviors defaultResponseBehaviour) {
		this.defaultAuthResponseBehavior = defaultResponseBehaviour;
	}
	
	@XmlElement(name = "default-acct-response-behavior")
	public AcctResponseBehaviors getDefaultAcctResponseBehavior() {
		return defaultAcctResponseBehavior;
	}

	public void setDefaultAcctResponseBehavior(AcctResponseBehaviors defaultAcctResponseBehavior) {
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
	public boolean isValidatePacket() {
		return validatePacket;
	}

	public void setValidatePacket(boolean validatePacket) {
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
	
	@Nullable
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
		postReadProcessingForUserIdentities();
		postReadProcessingForCUIAttributes();
		
		if (authenticationPolicyData != null) {
			authenticationPolicyData.setRadiusServicePolicyData(this);
			authenticationPolicyData.postRead();
		}
		
		if (accountingPolicyData != null) {
			accountingPolicyData.setRadiusServicePolicyData(this);
			accountingPolicyData.postRead();
		}
	}

	private void postReadProcessingForCUIAttributes() {
		cuiConfiguration.postRead();
	}

	private void postReadProcessingForUserIdentities() {
		userIdentities = Strings.splitter(MULTI_USER_IDENTITY_SEPARATOR).trimTokens().split(userIdentity); 
	} 
	
	public void registerRequiredDriver(String driverId) {
		requiredDriversIds.add(driverId);
	}

	@XmlTransient
	public Set<String> getRequiredDriverIds() {
		return requiredDriversIds;
	}

	@XmlTransient
	public AAAServerContext getServerContext() {
		return this.aaaServerContext;
	}
	public void setServerContext(AAAServerContext aaaServerContext) {
		this.aaaServerContext = aaaServerContext;
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
}

