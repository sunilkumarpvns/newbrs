package com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.aaa.util.constants.NasServicePolicyConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.rest.adapter.CaseAdapter;
import com.elitecore.elitesm.ws.rest.adapter.DiameterConcurrecyNameByIDAdapter;
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.adapter.RequestModeAdapter;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.adapter.WimaxValueAdapater;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Depends;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "diameter-nas-policy")
@Depends(field = "defaultResponseBehaviourArgument", dependsOn = "defaultResponseBehaviour" , message = "")
@ValidObject
@XmlType(propOrder = {"name", "description", "status", "ruleSet", "sessionManagement", "requestType",
		"defaultResponseBehaviour", "defaultResponseBehaviourArgument", "nasPolicyAuthMethodRelList",
		"multipleUserIdentity", "caseSensitiveUserIdentity", "stripUserIdentity", "trimUserIdentity", 
		"realmSeparator", "trimPassword", "realmPattern", "userName", "userNameResonseAttributes", 
		"anonymousProfileIdentity", "wimax", "rejectOnCheckItemNotFound", "rejectOnRejectItemNotFound",
		"actionOnPolicyNotFound", "gracePolicy", "diameterConcurrency", "additionalDiameterConcurrency",
		"defaultSessionTimeout", "cui", "advancedCuiExpression", "cuiResponseAttributes", "nasResponseAttributesSet",
		"nasPolicyAuthDriverRelList", "nasPolicyAdditionalDriverRelDataList", "authScript", "authPrePlugins",
		"authPostPlugins", "nasPolicyAcctDriverRelList", "acctScript", "acctPrePlugins", "acctPostPlugins"})
public class NASPolicyInstData extends BaseData implements  Serializable,Differentiable, Validator {

	private static final long serialVersionUID = 1L;
	private String nasPolicyId;
	
	@NotEmpty(message = "Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	private String description;
	
	@NotEmpty(message = "Status must be specified")
	@Pattern(regexp = "CST01|CST02", message = "Status value must be ACTIVE or INACTIVE")
	private String status;
	private Integer orderNumber;
	
	@NotEmpty(message = "Ruleset must be specified")
	private String ruleSet;

	private Integer caseSensitiveUserIdentity;
	
	@NotEmpty(message = "User Identity Attribute must be specified")
	private String multipleUserIdentity;
	private String stripUserIdentity;
	private String realmPattern;	
	private String realmSeparator;
	private String trimUserIdentity;	
	private String trimPassword;

	private String cui;
	private String cuiResponseAttributes;
	private String advancedCuiExpression;
	private String userName;
	private String userNameResonseAttributes;
	
	// plugin related 

	private Integer requestType ;
	private String rejectOnCheckItemNotFound;
	private String rejectOnRejectItemNotFound;
	private String actionOnPolicyNotFound;
	
	private String authScript;
	private String acctScript;
	private String auditUId;
	
	private List<NASPolicyAuthMethodRelData> nasPolicyAuthMethodRelList = new ArrayList<NASPolicyAuthMethodRelData>();
	
	@NotEmpty(message = "Authentication Driver must be specified")
	@Valid
	private List<NASPolicyAuthDriverRelData> nasPolicyAuthDriverRelList= new ArrayList<NASPolicyAuthDriverRelData>();

	@NotEmpty(message = "Accounting Driver must be specified")
	@Valid
	private List<NASPolicyAcctDriverRelData> nasPolicyAcctDriverRelList= new ArrayList<NASPolicyAcctDriverRelData>();
	
	@Valid
	private List<NASPolicyAdditionalDriverRelData> nasPolicyAdditionalDriverRelDataList = new ArrayList<NASPolicyAdditionalDriverRelData>();
	
	private Set<NASResponseAttributes> nasResponseAttributesSet;
	
	private String wimax;
	private String gracePolicy;
	private String anonymousProfileIdentity;
	private String sessionManagement;
	
	private String diameterConcurrency;
	private String additionalDiameterConcurrency;
	private Long defaultSessionTimeout;

	
	private List<NASPolicyAuthPluginConfig> nasPolicyAuthPluginConfigList;
	private List<NASPolicyAcctPluginConfig> nasPolicyAcctPluginConfigList;
	
	private String flowType;
	private String defaultResponseBehaviour;
	private String defaultResponseBehaviourArgument;
	
	@Valid
	private List<NASPolicyAuthPluginConfig> authPrePlugins;
	@Valid
	private List<NASPolicyAuthPluginConfig> authPostPlugins;
	
	@Valid
	private List<NASPolicyAcctPluginConfig> acctPrePlugins;
	@Valid
	private List<NASPolicyAcctPluginConfig> acctPostPlugins;
	
	public NASPolicyInstData() {
		description = RestUtitlity.getDefaultDescription();
		this.defaultSessionTimeout = NasServicePolicyConstants.DEFAULT_SESSION_TIMEOUT;
		this.trimUserIdentity = String.valueOf(NasServicePolicyConstants.TRIM_USER_IDENTITY);
		this.requestType = NasServicePolicyConstants.REQUEST_TYPE;
		this.realmPattern = NasServicePolicyConstants.REALM_PATTERN;
		this.caseSensitiveUserIdentity = NasServicePolicyConstants.CASE_SENSITIVITY;
		this.defaultResponseBehaviour = NasServicePolicyConstants.DEFAULT_RESPONSE_BEHAVIOUR;
		this.cui = NasServicePolicyConstants.CUI;
		this.nasPolicyAuthPluginConfigList = new ArrayList<NASPolicyAuthPluginConfig>();
		this.nasPolicyAcctPluginConfigList = new ArrayList<NASPolicyAcctPluginConfig>();
		this.authPrePlugins = new ArrayList<NASPolicyAuthPluginConfig>();
		this.authPostPlugins = new ArrayList<NASPolicyAuthPluginConfig>();
		this.acctPrePlugins = new ArrayList<NASPolicyAcctPluginConfig>();
		this.acctPostPlugins = new ArrayList<NASPolicyAcctPluginConfig>();
	}
	
	@XmlTransient
	public String getNasPolicyId() {
		return nasPolicyId;
	}

	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
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

	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(value = StatusAdapter.class)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	@XmlElement(name = "ruleset")
	public String getRuleSet() {
		return ruleSet;
	}

	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}

	@XmlElement(name = "user-identity-case-sensitivity")
	@XmlJavaTypeAdapter(value = CaseAdapter.class)
	@NotNull(message = "Supported value of select case are No Change, Lower Case and Upper Case only")
	public Integer getCaseSensitiveUserIdentity() {
		return caseSensitiveUserIdentity;
	}

	public void setCaseSensitiveUserIdentity(Integer caseSensitiveUserIdentity) {
		this.caseSensitiveUserIdentity = caseSensitiveUserIdentity;
	}

	@XmlElement(name = "user-identity-attribute")
	public String getMultipleUserIdentity() {
		return multipleUserIdentity;
	}

	public void setMultipleUserIdentity(String multipleUserIdentity) {
		this.multipleUserIdentity = multipleUserIdentity;
	}

	@XmlElement(name = "strip-user-identity")
	@XmlJavaTypeAdapter(value = LowerCaseConvertAdapter.class)
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Supported value of Strip User Identity are true and false")
	public String getStripUserIdentity() {
		return stripUserIdentity;
	}

	public void setStripUserIdentity(String stripUserIdentity) {
		this.stripUserIdentity = stripUserIdentity;
	}

	@XmlElement(name = "realm-pattern")
	@Pattern(regexp = "prefix|suffix", message = "Supported value of Realm Pattern are Prefix and Suffix")
	@XmlJavaTypeAdapter(value = LowerCaseConvertAdapter.class)
	public String getRealmPattern() {
		return realmPattern;
	}
	
	public void setRealmPattern(String realmPattern) {
		this.realmPattern = realmPattern;
	}
	
	@XmlElement(name = "seperator")
	@Length(min = 0, max = 1, message = "Length of Separator must not more than one character")
	public String getRealmSeparator() {
		return realmSeparator;
	}

	public void setRealmSeparator(String realmSeparator) {
		this.realmSeparator = realmSeparator;
	}

	@XmlElement(name = "trim-user-identity")
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Supported value of Trim User Identity are true and false")
	public String getTrimUserIdentity() {
		return trimUserIdentity;
	}

	public void setTrimUserIdentity(String trimUserIdentity) {
		this.trimUserIdentity = trimUserIdentity;
	}

	@XmlElement(name = "trim-password")
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Supported value of Trim Password are true and false")
	public String getTrimPassword() {
		return trimPassword;
	}

	public void setTrimPassword(String trimPassword) {
		this.trimPassword = trimPassword;
	}

	@XmlElement(name = "cui")
	@Pattern(regexp = "NONE|Authenticated-Username|Authenticated-Identity|Group|Profile-CUI|Advanced",
			message = "Supported values of Cui are NONE, Authenticated-Username, Authenticated-Identity, Group, Profile-CUI, Advanced")
	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}

	@XmlElement(name = "cui-response-attributes")
	public String getCuiResponseAttributes() {
		return cuiResponseAttributes;
	}

	public void setCuiResponseAttributes(String cuiResponseAttributes) {
		this.cuiResponseAttributes = cuiResponseAttributes;
	}

	@XmlElement(name = "user-name")
	@Pattern(regexp = "NONE|Authenticated-Username|Request", message = "Supported values of User Name are NONE, Authenticated-Username, Request")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XmlElement(name = "user-name-response-attributes")
	public String getUserNameResonseAttributes() {
		return userNameResonseAttributes;
	}

	public void setUserNameResonseAttributes(String userNameResonseAttributes) {
		this.userNameResonseAttributes = userNameResonseAttributes;
	}

	@XmlElementWrapper(name = "supported-authentication-methods")
	@XmlElement(name = "suppoted-method")
	public List<NASPolicyAuthMethodRelData> getNasPolicyAuthMethodRelList() {
		return nasPolicyAuthMethodRelList;
	}

	public void setNasPolicyAuthMethodRelList(
			List<NASPolicyAuthMethodRelData> nasPolicyAuthMethodRelList) {
		this.nasPolicyAuthMethodRelList = nasPolicyAuthMethodRelList;
	}
	
	@XmlElement(name = "request-mode")
	@XmlJavaTypeAdapter(value = RequestModeAdapter.class)
	@NotNull(message = "Supported value of Request Mode are Authenticate-Only, Authorize-Only and Authenticate and Authorize only")
	public Integer getRequestType() {
		return requestType;
	}

	public void setRequestType(Integer requestType) {
		this.requestType = requestType;
	}
	
	@XmlElement(name = "reject-on-check-item-not-found")
	public String getRejectOnCheckItemNotFound() {
		return rejectOnCheckItemNotFound;
	}

	public void setRejectOnCheckItemNotFound(String rejectOnCheckItemNotFound) {
		this.rejectOnCheckItemNotFound = rejectOnCheckItemNotFound;
	}

	@XmlElement(name = "reject-on-reject-item-not-found")
	public String getRejectOnRejectItemNotFound() {
		return rejectOnRejectItemNotFound;
	}

	public void setRejectOnRejectItemNotFound(String rejectOnRejectItemNotFound) {
		this.rejectOnRejectItemNotFound = rejectOnRejectItemNotFound;
	}

	@XmlElement(name = "accept-on-policy-not-found")
	public String getActionOnPolicyNotFound() {
		return actionOnPolicyNotFound;
	}

	public void setActionOnPolicyNotFound(String actionOnPolicyNotFound) {
		this.actionOnPolicyNotFound = actionOnPolicyNotFound;
	}
	
	@XmlElementWrapper(name = "primary-auth-driver-group")
	@XmlElement(name = "driver-detail")
	public List<NASPolicyAuthDriverRelData> getNasPolicyAuthDriverRelList() {
		return nasPolicyAuthDriverRelList;
	}

	public void setNasPolicyAuthDriverRelList(
			List<NASPolicyAuthDriverRelData> nasPolicyAuthDriverRelList) {
		this.nasPolicyAuthDriverRelList = nasPolicyAuthDriverRelList;
	}

	@XmlElementWrapper(name = "acct-driver-group")
	@XmlElement(name = "driver-detail")
	public List<NASPolicyAcctDriverRelData> getNasPolicyAcctDriverRelList() {
		return nasPolicyAcctDriverRelList;
	}

	public void setNasPolicyAcctDriverRelList(
			List<NASPolicyAcctDriverRelData> nasPolicyAcctDriverRelList) {
		this.nasPolicyAcctDriverRelList = nasPolicyAcctDriverRelList;
	}

	@XmlElementWrapper(name = "additional-auth-driver-group")
	@XmlElement(name = "additional-driver-detail")
	public List<NASPolicyAdditionalDriverRelData> getNasPolicyAdditionalDriverRelDataList() {
		return nasPolicyAdditionalDriverRelDataList;
	}

	public void setNasPolicyAdditionalDriverRelDataList(
			List<NASPolicyAdditionalDriverRelData> nasPolicyAdditionalDriverRelDataList) {
		this.nasPolicyAdditionalDriverRelDataList = nasPolicyAdditionalDriverRelDataList;
	}
	
	@XmlElement(name = "auth-driver-script")
	public String getAuthScript() {
		return authScript;
	}

	public void setAuthScript(String authScript) {
		this.authScript = authScript;
	}

	@XmlElement(name = "acct-driver-script")
	public String getAcctScript() {
		return acctScript;
	}

	public void setAcctScript(String acctScript) {
		this.acctScript = acctScript;
	}

	@Override
	public void setOrderNumber(Long orderNumber){
		int order = orderNumber.intValue();
		this.orderNumber = order;
	}
	
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ NAS PolicyInstData --------------");
		writer.println("Nas Policy Id :"+nasPolicyId);
		writer.println("Name :"+name);           
		writer.println("Description :"+description);
		writer.println("Status :"+status);         
		writer.println("Order Number :"+orderNumber);
		writer.println("RuleSet :"+ruleSet);
		writer.println("Session Management :"+sessionManagement);
		writer.println("Case Sensitive User Identity :"+caseSensitiveUserIdentity);
		writer.println("Multiple User Identity :"+multipleUserIdentity);
		writer.println("Strip User Identity :"+stripUserIdentity);          
		writer.println("Realm Pattern :"+realmPattern);	
		writer.println("Realm Separator :"+realmSeparator);	
		writer.println("Trim User Identity :"+trimUserIdentity);	
		writer.println("Trim Password :"+trimPassword);
		writer.println("Cui :"+cui);
		writer.println("Cui Response Attributes :"+cuiResponseAttributes);
		writer.println("User Name :"+userName);
		writer.println("User Name Resonse Attributes :"+userNameResonseAttributes);
		writer.println("Request Type :"+rejectOnCheckItemNotFound );
		writer.println("Reject On Check Item Not Found :"+requestType );
		writer.println("Reject On Reject Item Not Found :"+rejectOnRejectItemNotFound);
		writer.println("Action On Policy Not Found :"+actionOnPolicyNotFound );
		writer.println("Auth Script :"+authScript );
		writer.println("AcctS cript :"+acctScript );
		writer.println("----------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("Active", status);
		object.put("RuleSet", ruleSet);
		object.put("Session Management", sessionManagement);
		object.put("Request Mode", requestType);
		object.put("Default Response Behaviour", defaultResponseBehaviour);
		object.put("Default Response Behaviour Argument", defaultResponseBehaviourArgument);
		
		//Authentication Parameters
		object.put("Authentication Parameters", getAuthenticationParameters());
		
		//Authorization Parameters 
		object.put("Authorization Parameters", getAuthorizationParameters());
		
		//RFC-4372-CUI Parameters 
		object.put("RFC-4372-CUI", getRFC4372CUIParameters());
		
		//Accounting Parameters
		object.put("Accounting Parameters", getAccountingParameters());
		
		//Response Attribute
		object.put("Response Attributes", getResponseAttributeParameters());
		
		return object;
	}

	private Object getResponseAttributeParameters() {
		JSONObject object = new JSONObject();
		
		if(nasResponseAttributesSet!=null){
			JSONArray array = new JSONArray();
			for (NASResponseAttributes element : nasResponseAttributesSet) {
				array.add(element.toJson());
			}
			object.put("Response Attributes Mappings", array);
		}
		return object;
	}

	private JSONObject getAuthenticationParameters() {
		JSONObject object = new JSONObject();
		
		if(nasPolicyAuthMethodRelList!=null){
			JSONArray array = new JSONArray();
			for (NASPolicyAuthMethodRelData element : nasPolicyAuthMethodRelList) {
				if(element.getAuthMethodTypeId() == 1){
					array.add("PAP");
				}else if(element.getAuthMethodTypeId() == 2){
					array.add("CHAP");
				}
			}
			object.put("Supported Authentication Method", array);
		}
		
		object.put("User Identity Attribute", multipleUserIdentity);
		
		if(caseSensitiveUserIdentity == 1){
			object.put("User Identity Case Sensitivity", "No Change");
		}else if(caseSensitiveUserIdentity == 2){
			object.put("User Identity Case Sensitivity", "Upper Case");
		}else if(caseSensitiveUserIdentity == 3){
			object.put("User Identity Case Sensitivity", "Lower Case");
		}
		
		JSONObject updateUserIdentity = new JSONObject();
		updateUserIdentity.put(" Strip User Identity", stripUserIdentity);
		updateUserIdentity.put("Separator", realmSeparator);
		updateUserIdentity.put("Realm Pattern", realmPattern);
		updateUserIdentity.put("Trim User Identity", trimUserIdentity);
		updateUserIdentity.put("Trim Password", trimPassword);
		object.put("Update User Identity", updateUserIdentity);
		object.put("Anonymous Profile Identity", anonymousProfileIdentity);
		object.put("User Name", userName);
		object.put("User Name Response Attributes", userNameResonseAttributes);
		
		if(nasPolicyAuthDriverRelList!=null){
			JSONArray array = new JSONArray();
			for (NASPolicyAuthDriverRelData element : nasPolicyAuthDriverRelList) {
				if(element.getDriverData()!=null && element.getDriverData().getName()!= null && element.getWeightage()!=null){
					array.add(element.getDriverData().getName() + "-W-" + element.getWeightage());
				}
			}
			object.put("Driver Detail", array);
		}
		
		if(nasPolicyAdditionalDriverRelDataList!=null){
			JSONArray array = new JSONArray();
			for (NASPolicyAdditionalDriverRelData element : nasPolicyAdditionalDriverRelDataList) {
				if(element.getDriverInstanceData()!=null){
					if(! element.getDriverInstanceData().getName().isEmpty() ){
						array.add(element.getDriverInstanceData().getName());
					}
				}
			}
			object.put("Additional Driver", array);
		}
		
		if( flowType != null && flowType.equalsIgnoreCase(PolicyPluginConstants.AUTH_FLOW)){
			if(nasPolicyAuthPluginConfigList != null){
				JSONArray auth_pre_Array = new  JSONArray();
				JSONArray auth_post_Array = new  JSONArray();
				for(NASPolicyAuthPluginConfig element : nasPolicyAuthPluginConfigList){
					if(element.getPluginName().isEmpty() == false ){
						if( element.getPluginType().equals(PolicyPluginConstants.IN_PLUGIN))
							auth_pre_Array.add(element.toJson());
						else if( element.getPluginType().equals(PolicyPluginConstants.OUT_PLUGIN))
							auth_post_Array.add(element.toJson());
					}
				}
				object.put("Pre Plugin Details", auth_pre_Array);
				object.put("Post Plugin Details", auth_post_Array);
			}
		}
		
		object.put("Driver Script", authScript);
		return object;
	}

	private JSONObject getAuthorizationParameters(){
		JSONObject object = new JSONObject();
		object.put("Wimax", wimax);
		object.put("Reject On Check Item Not Found", rejectOnCheckItemNotFound);
		object.put("Reject On Reject Item Not Found", rejectOnRejectItemNotFound);
		object.put("Accept On Policy Not Found", actionOnPolicyNotFound);
		object.put("Grace Policy", gracePolicy);
		object.put("Default Session Timeout(Sec)", defaultSessionTimeout);
		return object;
	}
	
	private JSONObject getRFC4372CUIParameters(){
		JSONObject object = new JSONObject();
		object.put("CUI", cui);
		object.put("Reject On Reject Item Not Found", rejectOnRejectItemNotFound);
		object.put("CUI Response Attributes", cuiResponseAttributes);
		return object;
	}
	
	private JSONObject getAccountingParameters(){
		JSONObject object = new JSONObject();
		if(nasPolicyAcctDriverRelList!=null){
			JSONArray array = new JSONArray();
			for (NASPolicyAcctDriverRelData element : nasPolicyAcctDriverRelList) {
				if(element.getDriverData()!=null && element.getDriverData().getName()!=null && element.getWeightage()!=null){
					array.add(element.getDriverData().getName() + "-W-" + element.getWeightage());
				}
			}
			object.put("Driver Detail", array);
		}
		
		if( flowType != null && flowType.equalsIgnoreCase(PolicyPluginConstants.ACCT_FLOW)){
			if(nasPolicyAuthPluginConfigList != null){
				JSONArray acct_pre_Array = new  JSONArray();
				JSONArray acct_post_Array = new  JSONArray();
				for(NASPolicyAuthPluginConfig element : nasPolicyAuthPluginConfigList){
					if(element.getPluginName().isEmpty() == false ){
						if( element.getPluginType().equals(PolicyPluginConstants.IN_PLUGIN))
							acct_pre_Array.add(element.toJson());
						else if( element.getPluginType().equals(PolicyPluginConstants.OUT_PLUGIN))
							acct_post_Array.add(element.toJson());
					}
				}
				object.put("Pre Plugin Details", acct_pre_Array);
				object.put("Post Plugin Details", acct_post_Array);
			}
		}
		
		object.put("Driver Script", acctScript);
		return object;
	}

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	@XmlElementWrapper(name = "command-code-wise-response-attribute-list")
	@XmlElement(name = "command-code-wise-response-attribute", type = NASResponseAttributes.class)
	public Set<NASResponseAttributes> getNasResponseAttributesSet() {
		return nasResponseAttributesSet;
	}

	public void setNasResponseAttributesSet(Set<NASResponseAttributes> nasResponseAttributesSet) {
		this.nasResponseAttributesSet = nasResponseAttributesSet;
	}

	@XmlElement(name = "wimax")
	@XmlJavaTypeAdapter(value = WimaxValueAdapater.class)
	@NotNull(message = "Supported value for wimax are enable and disable only with case insensitive")
	public String getWimax() {
		return wimax;
	}

	public void setWimax(String wimax) {
		this.wimax = wimax;
	}

	@XmlElement(name = "grace-policy")
	public String getGracePolicy() {
		return gracePolicy;
	}

	public void setGracePolicy(String gracePolicy) {
		this.gracePolicy = gracePolicy;
	}

	@XmlElement(name = "annonymous-profile-identity")
	public String getAnonymousProfileIdentity() {
		return anonymousProfileIdentity;
	}

	public void setAnonymousProfileIdentity(String anonymousProfileIdentity) {
		this.anonymousProfileIdentity = anonymousProfileIdentity;
	}

	@XmlElement(name = "session-management")
	@Pattern(regexp = "false|true", message = "session manager must be true or false")
	@XmlJavaTypeAdapter(value = LowerCaseConvertAdapter.class)
	public String getSessionManagement() {
		return sessionManagement;
	}

	public void setSessionManagement(String sessionManagement) {
		this.sessionManagement = sessionManagement;
	}

	@XmlElement(name = "diameter-concurrency")
	@XmlJavaTypeAdapter(value = DiameterConcurrecyNameByIDAdapter.class)
	public String getDiameterConcurrency() {
		return diameterConcurrency;
	}

	public void setDiameterConcurrency(String diameterConcurrency) {
		this.diameterConcurrency = diameterConcurrency;
	}

	@XmlElement(name = "additional-diameter-concurrency")
	@XmlJavaTypeAdapter(value = DiameterConcurrecyNameByIDAdapter.class)
	public String getAdditionalDiameterConcurrency() {
		return additionalDiameterConcurrency;
	}

	public void setAdditionalDiameterConcurrency(
			String additionalDiameterConcurrency) {
		this.additionalDiameterConcurrency = additionalDiameterConcurrency;
	}
	
	@XmlElement(name = "default-session-timeout")
	public Long getDefaultSessionTimeout() {
		return defaultSessionTimeout;
	}

	public void setDefaultSessionTimeout(Long defaultSessionTimeout) {
		this.defaultSessionTimeout = defaultSessionTimeout;
	}

	@XmlElement(name = "advance-cui-expression")
	public String getAdvancedCuiExpression() {
		return advancedCuiExpression;
	}

	public void setAdvancedCuiExpression(String advancedCuiExpression) {
		this.advancedCuiExpression = advancedCuiExpression;
	}

	@XmlTransient
	public List<NASPolicyAuthPluginConfig> getNasPolicyAuthPluginConfigList() {
		return nasPolicyAuthPluginConfigList;
	}

	public void setNasPolicyAuthPluginConfigList(
			List<NASPolicyAuthPluginConfig> nasPolicyAuthPluginConfigList) {
		this.nasPolicyAuthPluginConfigList = nasPolicyAuthPluginConfigList;
	}

	@XmlTransient
	public List<NASPolicyAcctPluginConfig> getNasPolicyAcctPluginConfigList() {
		return nasPolicyAcctPluginConfigList;
	}

	public void setNasPolicyAcctPluginConfigList(
			List<NASPolicyAcctPluginConfig> nasPolicyAcctPluginConfigList) {
		this.nasPolicyAcctPluginConfigList = nasPolicyAcctPluginConfigList;
	}

	@XmlTransient
	public String getFlowType() {
		return flowType;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	@XmlElement(name = "default-response-behaviour")
	public String getDefaultResponseBehaviour() {
		return defaultResponseBehaviour;
	}

	public void setDefaultResponseBehaviour(String defaultResponseBehaviour) {
		this.defaultResponseBehaviour = defaultResponseBehaviour;
	}

	@XmlElement(name = "default-response-behaviour-argument")
	public String getDefaultResponseBehaviourArgument() {
		return defaultResponseBehaviourArgument;
	}

	public void setDefaultResponseBehaviourArgument(String defaultResponseBehaviourArgument) {
		this.defaultResponseBehaviourArgument = defaultResponseBehaviourArgument;
	}
	
	@XmlElementWrapper(name = "auth-pre-plugins")
	@XmlElement(name = "pre-plugin")
	public List<NASPolicyAuthPluginConfig> getAuthPrePlugins() {
		return authPrePlugins;
	}

	@XmlElementWrapper(name = "auth-post-plugins")
	@XmlElement(name = "post-plugin")
	public List<NASPolicyAuthPluginConfig> getAuthPostPlugins() {
		return authPostPlugins;
	}
	
	@XmlElementWrapper(name = "acct-pre-plugins")
	@XmlElement(name = "pre-plugin")
	public List<NASPolicyAcctPluginConfig> getAcctPrePlugins() {
		return acctPrePlugins;
	}

	@XmlElementWrapper(name = "acct-post-plugins")
	@XmlElement(name = "post-plugin")
	public List<NASPolicyAcctPluginConfig> getAcctPostPlugins() {
		return acctPostPlugins;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {

		boolean isValid = true;
		if (Collectionz.isNullOrEmpty(getNasPolicyAuthDriverRelList()) == false && 
				Collectionz.isNullOrEmpty(getNasPolicyAdditionalDriverRelDataList()) == false) {
			
			for (NASPolicyAuthDriverRelData primaryDriver : getNasPolicyAuthDriverRelList()) {
				for (NASPolicyAdditionalDriverRelData additionalDriver : getNasPolicyAdditionalDriverRelDataList()) {

					String primaryDriverName = primaryDriver.getDriverName();
					String additionalDriverName = additionalDriver.getDriverName();
					if (Strings.isNullOrBlank(primaryDriverName) == false && Strings.isNullOrBlank(additionalDriverName) == false &&
							primaryDriverName.equals(additionalDriverName)) {
						RestUtitlity.setValidationMessage(context, "Primary Group and Additional Group does not have same driver");
						isValid = false;
					}
				}
			}
		}
		
		if (Strings.isNullOrBlank(getDefaultResponseBehaviour()) == false &&
				DefaultResponseBehaviorType.REJECT.name().equalsIgnoreCase(getDefaultResponseBehaviour()) == false &&
				DefaultResponseBehaviorType.DROP.name().equalsIgnoreCase(getDefaultResponseBehaviour()) == false &&
				DefaultResponseBehaviorType.HOTLINE.name().equalsIgnoreCase(getDefaultResponseBehaviour()) == false) {
			
			RestUtitlity.setValidationMessage(context, "Supported values for default response behaviour is REJECT, DROP and HOTLINE only");
			isValid = false;
		}
		
		if ("true".equalsIgnoreCase(getStripUserIdentity()) && Strings.isNullOrBlank(getRealmPattern())) {
			RestUtitlity.setValidationMessage(context, "Realm Pattern must be specified Suffix or Prefix");
			isValid = false;
		}
		
		if ("Advanced".equalsIgnoreCase(getCui()) &&  Strings.isNullOrBlank(getAdvancedCuiExpression())) {
			RestUtitlity.setValidationMessage(context, "Advanced CUI Expression must be specified");
			isValid = false;
		}
		
		if (Strings.isNullOrBlank(getDiameterConcurrency()) == false && "-1L".equals(getDiameterConcurrency())) {
			RestUtitlity.setValidationMessage(context, "Invalid diameter concurrency specified");
			isValid = false;
		}
		
		if (Strings.isNullOrBlank(getAdditionalDiameterConcurrency()) == false && "-1L".equals(getAdditionalDiameterConcurrency())) {
			RestUtitlity.setValidationMessage(context, "Invalid Additional diameter concurrency specified");
			isValid = false;
		}
		
		if (Strings.isNullOrBlank(getAdditionalDiameterConcurrency()) == false && ("-1L".equals(getAdditionalDiameterConcurrency())) == false && (getDiameterConcurrency() == null)) {
			RestUtitlity.setValidationMessage(context, "Diameter Concurrency must be specified");
			isValid = false;
		}
		
		if (Strings.isNullOrBlank(getGracePolicy()) == false) {
			try {
				new GracePolicyBLManager().getGracePolicyByName(getGracePolicy(), ConfigManager.chekForCaseSensitivity());
			} catch (DataManagerException e) {
				RestUtitlity.setValidationMessage(context, "Invalid grace policy specified");
				isValid = false;
			}
		}
		
		if ((Collectionz.isNullOrEmpty(getAuthPrePlugins()) == false)) {
			List<NASPolicyAuthPluginConfig> nasPolicyAuthPrePluginLst = getAuthPrePlugins();
			for (NASPolicyAuthPluginConfig nasPolicyPluginConfig : nasPolicyAuthPrePluginLst) {
				nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.IN_PLUGIN);
				this.nasPolicyAuthPluginConfigList.add(nasPolicyPluginConfig);
			}
		}
		
		if (Collectionz.isNullOrEmpty(getAuthPostPlugins()) == false){
			List<NASPolicyAuthPluginConfig> nasPolicyAuthPostPluginLst = getAuthPostPlugins();
			for (NASPolicyAuthPluginConfig nasPolicyPluginConfig : nasPolicyAuthPostPluginLst) {
				nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
				this.nasPolicyAuthPluginConfigList.add(nasPolicyPluginConfig);
			}
		}
		
		if ((Collectionz.isNullOrEmpty(getAcctPrePlugins()) == false)) {
			List<NASPolicyAcctPluginConfig> nasPolicyAcctPrePluginLst = getAcctPrePlugins();
			for(NASPolicyAcctPluginConfig nasPolicyPluginConfig : nasPolicyAcctPrePluginLst){
				nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.IN_PLUGIN);
				this.nasPolicyAcctPluginConfigList.add(nasPolicyPluginConfig);
			}
		}
		
		if (Collectionz.isNullOrEmpty(getAcctPostPlugins()) == false) {
			List<NASPolicyAcctPluginConfig> nasPolicyAcctPostPluginLst = getAcctPostPlugins();
			for(NASPolicyAcctPluginConfig nasPolicyPluginConfig : nasPolicyAcctPostPluginLst){
				nasPolicyPluginConfig.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
				this.nasPolicyAcctPluginConfigList.add(nasPolicyPluginConfig);
			}
		}

		return isValid;
	}
}