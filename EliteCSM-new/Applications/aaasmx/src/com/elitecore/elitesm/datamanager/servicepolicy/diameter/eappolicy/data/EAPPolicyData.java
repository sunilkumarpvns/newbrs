package com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data;

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
import com.elitecore.aaa.util.constants.EAPServicePolicyConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.CaseAdapter;
import com.elitecore.elitesm.ws.rest.adapter.DiameterConcurrecyNameByIDAdapter;
import com.elitecore.elitesm.ws.rest.adapter.EAPConfigNameByIdAdapter;
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.adapter.RequestModeAdapter;
import com.elitecore.elitesm.ws.rest.adapter.StatusAdapter;
import com.elitecore.elitesm.ws.rest.adapter.WimaxValueAdapater;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Depends;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "diameter-eap-policy")
@Depends(field = "defaultResponseBehaviorArgument", dependsOn = "defaultResponseBehaviour" , message = "")
@ValidObject
@XmlType(propOrder = {"name", "description", "status", "ruleSet", "sessionManagement", "requestType",
		"defaultResponseBehaviour", "defaultResponseBehaviorArgument", "eapConfigId", "multipleUserIdentity",
		"caseSensitiveUserIdentity", "stripUserIdentity", "trimUserIdentity", "realmSeparator", "trimPassword",
		"realmPattern", "anonymousProfileIdentity", "wimax", "rejectOnCheckItemNotFound", "rejectOnRejectItemNotFound",
		"actionOnPolicyNotFound", "gracePolicy", "diameterConcurrency", "additionalDiameterConcurrency", 
		"defaultSessionTimeout", "cui", "advancedCuiExpression", "cuiResponseAttributes", "driverList",
		"eapAdditionalDriverRelDataList", "script", "prePlugins", "postPlugins", "eapResponseAttributesSet"})
public class EAPPolicyData extends BaseData implements Differentiable, Validator {
	
	private String eapPolicyId;
	
	@NotEmpty(message = "Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
    private String name;
	
    private String description;
    
    @NotEmpty(message = "Ruleset must be specified")
    private String ruleSet;
    private Long orderNumber;
    
    @NotEmpty(message = "Status must be specified")
    private String status;
    
    private Integer caseSensitiveUserIdentity;
    
    @NotEmpty(message = "User Identity Attribute must be specified")
    private String multipleUserIdentity;
    private String stripUserIdentity;
    private String realmPattern;
    private String realmSeparator;
    private String trimUserIdentity;
    private String trimPassword;
    private String eapConfigId;
    
    @Valid
    @NotEmpty(message = "Authentication Driver must be specified")
    private List<EAPPolicyAuthDriverRelationData> driverList;
    
    @Valid
    private List<EAPPolicyAdditionalDriverRelData> eapAdditionalDriverRelDataList = new ArrayList<EAPPolicyAdditionalDriverRelData>();
    
    private Integer requestType ;
	private String rejectOnCheckItemNotFound;
	private String rejectOnRejectItemNotFound;
	private String actionOnPolicyNotFound;
	private String script;
	private String auditUId;
	private Set<EAPResponseAttributes> eapResponseAttributesSet;
	private String wimax;
	private String gracePolicy;
	private String anonymousProfileIdentity;
	private String sessionManagement;
	private String diameterConcurrency;
	private String additionalDiameterConcurrency;
	private Long defaultSessionTimeout;
	private String cui;
	private String cuiResponseAttributes;
	private String advancedCuiExpression;
	
	private List<EAPPolicyPluginConfig> eapPolicyPluginConfigList;
	
	@Valid
	private List<EAPPolicyPluginConfig> prePlugins;
	@Valid
	private List<EAPPolicyPluginConfig> postPlugins;
	
	private String defaultResponseBehaviorArgument;
	private String defaultResponseBehaviour;
	
	public EAPPolicyData() {
		this.description = RestUtitlity.getDefaultDescription();
		this.eapPolicyPluginConfigList = new ArrayList<EAPPolicyPluginConfig>();
		this.prePlugins = new ArrayList<EAPPolicyPluginConfig>();
		this.postPlugins = new ArrayList<EAPPolicyPluginConfig>();
		
		this.defaultSessionTimeout = EAPServicePolicyConstants.DEFAULT_SESSION_TIMEOUT;
		this.trimUserIdentity = String.valueOf(EAPServicePolicyConstants.TRIM_USER_IDENTITY);
		this.requestType = EAPServicePolicyConstants.REQUEST_TYPE;
		this.realmPattern = EAPServicePolicyConstants.REALM_PATTERN;
		this.caseSensitiveUserIdentity = EAPServicePolicyConstants.CASE_SENSITIVITY;
		this.defaultResponseBehaviour = EAPServicePolicyConstants.DEFAULT_RESPONSE_BEHAVIOUR;
		this.cui = EAPServicePolicyConstants.CUI;
	}
	
	@XmlTransient
	public String getEapPolicyId() {
		return eapPolicyId;
	}
	
	public void setEapPolicyId(String eapPolicyId) {
		this.eapPolicyId = eapPolicyId;
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
	
	@XmlElement(name = "ruleset")
	public String getRuleSet() {
		return ruleSet;
	}
	
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}
	
	@XmlTransient
	public Long getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@XmlElement(name = "status")
	@XmlJavaTypeAdapter(value = StatusAdapter.class)
	@Pattern(regexp = "CST01|CST02", message = "Status value must be ACTIVE or INACTIVE")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@XmlElement(name = "case")
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
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Supported value of Trim User Identity are true and false")
	public String getTrimPassword() {
		return trimPassword;
	}
	
	public void setTrimPassword(String trimPassword) {
		this.trimPassword = trimPassword;
	}

	@XmlElement(name = "eap-config")
	@XmlJavaTypeAdapter(value = EAPConfigNameByIdAdapter.class)
	@NotEmpty(message = "Eap configuration must be specified")
	public String getEapConfigId() {
		return eapConfigId;
	}
	
	public void setEapConfigId(String eapConfigId) {
		this.eapConfigId = eapConfigId;
	}
	
	@XmlElementWrapper(name = "primary-driver-group")
	@XmlElement(name = "driver-group")
	@NotEmpty(message = "Authentication Driver must be specified")
	public List<EAPPolicyAuthDriverRelationData> getDriverList() {
		return driverList;
	}
	
	public void setDriverList(List<EAPPolicyAuthDriverRelationData> driverList) {
		this.driverList = driverList;
	}
	
	@XmlElementWrapper(name = "additional-driver-group")
	@XmlElement(name = "driver-group")
	public List<EAPPolicyAdditionalDriverRelData> getEapAdditionalDriverRelDataList() {
		return eapAdditionalDriverRelDataList;
	}
	
	public void setEapAdditionalDriverRelDataList(
			List<EAPPolicyAdditionalDriverRelData> eapAdditionalDriverRelDataList) {
		this.eapAdditionalDriverRelDataList = eapAdditionalDriverRelDataList;
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
	
	@XmlElement(name = "action-on-policy-not-found")
	public String getActionOnPolicyNotFound() {
		return actionOnPolicyNotFound;
	}
	
	public void setActionOnPolicyNotFound(String actionOnPolicyNotFound) {
		this.actionOnPolicyNotFound = actionOnPolicyNotFound;
	}
	
	@XmlElement(name = "script")
	public String getScript() {
		return script;
	}
	
	public void setScript(String script) {
		this.script = script;
	}
	
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();
		
		writer.println("Eap PolicyId :" + eapPolicyId);
		writer.println("Name :" + name);
		writer.println("Description :" + description);
		writer.println("RuleSet :" + ruleSet);
		writer.println("Order Number :" + orderNumber);
		writer.println("Status :" + status);
		writer.println("Case Sensitive User Identity :" + caseSensitiveUserIdentity);
		writer.println("Multiple User Identity :" + multipleUserIdentity);
		writer.println("Strip User Identity :" + stripUserIdentity);
		writer.println("Realm Pattern :" + realmPattern);
		writer.println("Realm Separator :" + realmSeparator);
		writer.println("Trim User Identity :" + trimUserIdentity);
		writer.println("Trim Password :" + trimPassword);
		writer.println("Eap Config Id :" + eapConfigId);
		writer.println("Driver List :" + driverList);
		writer.println("Eap Additional Driver RelData List :" + eapAdditionalDriverRelDataList);
		writer.println("Request Type :" + requestType);
		writer.println("Reject On Check Item Not Found :" + rejectOnCheckItemNotFound);
		writer.println("Reject On Reject Item Not Found :" + rejectOnRejectItemNotFound);
		writer.println("Action On Policy Not Found :" + actionOnPolicyNotFound);
		writer.println("Script :" + script);
		
		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
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
		object.put("Request Mode", requestType);
		object.put("Default Response Behaviour Argument", defaultResponseBehaviorArgument);
		object.put("Default Response Behaviour", defaultResponseBehaviour);
		//Update Authentication Parameters
		object.put("Authentication Parameters", getAuthenticationParameters());
		
		//Update Authorization Parameters
		object.put("Authorization Parameters", getAuthorizationParameters());
		
		//RFC-4372-CUI Parameters 
		object.put("RFC-4372-CUI", getRFC4372CUIParameters());
		
		//Update Profile Driver
		object.put("Profile Driver", getProfileDriver());
		
		//Response Attribute
		object.put("Response Attributes", getResponseAttributeParameters());
		
		return object;
	}
	
	private JSONObject getAuthenticationParameters() {
		JSONObject object = new JSONObject();
		object.put("EAP Config", EliteSMReferencialDAO.fetchEAPConfigurationDetails(eapConfigId));
		object.put("User Identity Attribute", multipleUserIdentity);
		object.put("Select Case", caseSensitiveUserIdentity);
		
		if(caseSensitiveUserIdentity == 1){
			object.put("Select Case", "No Change");
		}else if(caseSensitiveUserIdentity == 2){
			object.put("Select Case", "Upper Case");
		}else if(caseSensitiveUserIdentity == 3){
			object.put("Select Case", "Lower Case");
		}
		
		
		JSONObject updateUserIdentity = new JSONObject();
		updateUserIdentity.put("Strip User Identity", stripUserIdentity);
		updateUserIdentity.put("Separator", realmSeparator);
		updateUserIdentity.put("Realm Pattern", realmPattern);
		updateUserIdentity.put("Trim User Identity", trimUserIdentity);
		updateUserIdentity.put("Trim Password", trimPassword);
		object.put("Update User Identity", updateUserIdentity);
		object.put("Anonymous Profile Identity", anonymousProfileIdentity);
		return object;
	}
	
	private JSONObject getAuthorizationParameters(){
		JSONObject object = new JSONObject();
		object.put("Wimax", wimax);
		object.put("Reject On Check Item Not Found", rejectOnCheckItemNotFound);
		object.put("Reject On Reject Item Not Found", rejectOnRejectItemNotFound);
		object.put("Accept On Policy Not Found", actionOnPolicyNotFound);
		object.put("Grace Policy", gracePolicy);
		object.put("Default Session Timeout (Sec)", defaultSessionTimeout);
		return object;
	}
	
	private JSONObject getRFC4372CUIParameters(){
		JSONObject object = new JSONObject();
		object.put("CUI", cui);
		object.put("Reject On Reject Item Not Found", rejectOnRejectItemNotFound);
		object.put("CUI Response Attributes", cuiResponseAttributes);
		return object;
	}
	
	private JSONObject getProfileDriver(){
		JSONObject object = new JSONObject();
		if(driverList!=null){
			JSONArray array = new JSONArray();
			for (EAPPolicyAuthDriverRelationData element : driverList) {
				if(element.getDriverData()!=null && element.getDriverData().getName()!=null && element.getWeightage()!=null){
					array.add(element.getDriverData().getName() + "-W-" + element.getWeightage());
				}
			}
			object.put("Driver", array);
		}
		if(eapAdditionalDriverRelDataList!=null){
			JSONArray array = new JSONArray();
			for (EAPPolicyAdditionalDriverRelData element : eapAdditionalDriverRelDataList) {
				if(element.getDriverInstanceData()!=null){
					array.add(element.getDriverInstanceData().getName());
				}
			}
			object.put("Additional Driver", array);
		}
		object.put("Driver Script", script);
		
		if(eapPolicyPluginConfigList != null){
            JSONArray pre_Array = new  JSONArray();
            JSONArray post_Array = new  JSONArray();
            for(EAPPolicyPluginConfig element : eapPolicyPluginConfigList){
                if(element.getPluginName().isEmpty() == false ){
                    if( element.getPluginType().equals(PolicyPluginConstants.IN_PLUGIN))
                        pre_Array.add(element.toJson());
                    else if( element.getPluginType().equals(PolicyPluginConstants.OUT_PLUGIN))
                        post_Array.add(element.toJson());
                }
            }
            object.put("Pre Plugin Details", pre_Array);
            object.put("Post Plugin Details", post_Array);
        }
		return object;
	}
	
	private Object getResponseAttributeParameters() {
		JSONObject object = new JSONObject();
		
		if(eapResponseAttributesSet!=null){
			JSONArray array = new JSONArray();
			for (EAPResponseAttributes element : eapResponseAttributesSet) {
				array.add(element.toJson());
			}
			object.put("Response Attributes Mappings", array);
		}
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
	@XmlElement(name = "command-code-wise-response-attribute")
	public Set<EAPResponseAttributes> getEapResponseAttributesSet() {
		return eapResponseAttributesSet;
	}
	
	public void setEapResponseAttributesSet(Set<EAPResponseAttributes> eapResponseAttributesSet) {
		this.eapResponseAttributesSet = eapResponseAttributesSet;
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
	
	@XmlElement(name = "cui")
	@Pattern(regexp = "NONE|Authenticated-Username|Authenticated-Identity|Group|Profile-CUI|Advanced",
			message = "Cui value must be NONE, Authenticated-Username, Authenticated-Identity, Group, Profile-CUI, Advanced")
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
	
	@XmlElement(name = "advanced-cui-expression")
	public String getAdvancedCuiExpression() {
		return advancedCuiExpression;
	}
	
	public void setAdvancedCuiExpression(String advancedCuiExpression) {
		this.advancedCuiExpression = advancedCuiExpression;
	}
	
	@XmlTransient
	public List<EAPPolicyPluginConfig> getEapPolicyPluginConfigList() {
		return eapPolicyPluginConfigList;
	}
	
	public void setEapPolicyPluginConfigList(
			List<EAPPolicyPluginConfig> eapPolicyPluginConfigList) {
		this.eapPolicyPluginConfigList = eapPolicyPluginConfigList;
	}
	
	@XmlElementWrapper(name = "pre-plugins")
	@XmlElement(name = "pre-plugin")
	public List<EAPPolicyPluginConfig> getPrePlugins() {
		return prePlugins;
	}
	
	public void setPrePlugins(List<EAPPolicyPluginConfig> prePlugins) {
		this.prePlugins = prePlugins;
	}
	
	@XmlElementWrapper(name = "post-plugins")
	@XmlElement(name = "post-plugin")
	public List<EAPPolicyPluginConfig> getPostPlugins() {
		return postPlugins;
	}
	
	public void setPostPlugins(List<EAPPolicyPluginConfig> postPlugins) {
		this.postPlugins = postPlugins;
	}
	
	@XmlElement(name = "default-response-behaviour-argument")
	public String getDefaultResponseBehaviorArgument() {
		return defaultResponseBehaviorArgument;
	}
	
	public void setDefaultResponseBehaviorArgument(String defaultResponseBehaviorArgument) {
		this.defaultResponseBehaviorArgument = defaultResponseBehaviorArgument;
	}
	
	@XmlElement(name = "default-response-behaviour")
	public String getDefaultResponseBehaviour() {
		return defaultResponseBehaviour;
	}
	
	public void setDefaultResponseBehaviour(String defaultResponseBehaviour) {
		this.defaultResponseBehaviour = defaultResponseBehaviour;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if(RestValidationMessages.INVALID.equals(this.eapConfigId)){
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Invalid EAP configuration name");
		}
		
		if (Collectionz.isNullOrEmpty(getDriverList()) == false &&
				Collectionz.isNullOrEmpty(getEapAdditionalDriverRelDataList()) == false) {
			for (EAPPolicyAuthDriverRelationData primaryDriver : getDriverList()) {
				for (EAPPolicyAdditionalDriverRelData additionalDriver : getEapAdditionalDriverRelDataList()) {
					
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
		
		if (Strings.isNullOrBlank(getAdditionalDiameterConcurrency()) == false && ("-1L".equals(getAdditionalDiameterConcurrency()) == false) && (getDiameterConcurrency() == null)) {
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
		
		if(Collectionz.isNullOrEmpty(getPrePlugins()) == false){
			List<EAPPolicyPluginConfig> eapPolicyPrePluginLst = getPrePlugins();
			for(EAPPolicyPluginConfig eapPolicyPrePlugin : eapPolicyPrePluginLst){
				eapPolicyPrePlugin.setPluginType(PolicyPluginConstants.IN_PLUGIN);
				this.eapPolicyPluginConfigList.add(eapPolicyPrePlugin);
			}
		}
		
		if(Collectionz.isNullOrEmpty(getPostPlugins()) == false){
			List<EAPPolicyPluginConfig> eapPolicyPostPluginLst = getPostPlugins();
			for(EAPPolicyPluginConfig eapPolicyPrePlugin : eapPolicyPostPluginLst){
				eapPolicyPrePlugin.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
				this.eapPolicyPluginConfigList.add(eapPolicyPrePlugin);
			}
		}
		return isValid;
	}
}