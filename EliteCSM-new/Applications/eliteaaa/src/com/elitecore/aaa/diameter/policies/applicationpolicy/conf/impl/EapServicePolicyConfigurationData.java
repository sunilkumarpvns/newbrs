package com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.core.conf.impl.PrimaryAndAdditionalDriversDetails;
import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.EapServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType.DefaultResponseBehaviorTypeAdapter;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.RejectBehavior;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthorizationHandlerData;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.ChargeableUserIdentityConfiguration;
import com.elitecore.commons.config.BooleanAdapter;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

@XmlType(propOrder={})
public class EapServicePolicyConfigurationData implements EapServicePolicyConfiguration{
	
	private String policyRuleSet;	
	private String policyId;
	private String policyName;
	
	private DiameterEAPAuthParamsDetails diameterEAPAuthParamsDetails;
	private PrimaryAndAdditionalDriversDetails profileDrivers;

	private ArrayList<String> userIdentities;
	private Map<String,Integer>authDriverInstanceIdsMap;
	private List<String> additionalDriversList;
	
	private ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributesList;
	private Map<Integer, AdditionalResponseAttributes> commandCodeResponseAttributesMap;
	private Boolean sessionManagementEnabled = false;
	private int requestType;
	private String diameterConcurrencyConfigId ;
	private String additionalDiameterConcuurencyConfigId;
	private ChargeableUserIdentityConfiguration cuiConfiguration;
	
	private DiameterAuthorizationHandlerData authorizationHandlerData;

	private List<PluginEntryDetail> prePlugins;
	private List<PluginEntryDetail> postPlugins;

	private DefaultResponseBehaviorType responseBehaviorType = DefaultResponseBehaviorType.REJECT;
	private String responseBehaviorParameter = String.valueOf(RejectBehavior.DEFAULT_RESULT_CODE.code);

	public EapServicePolicyConfigurationData() {
		this.diameterEAPAuthParamsDetails = new DiameterEAPAuthParamsDetails();
		this.userIdentities = new ArrayList<String>();
		this.authDriverInstanceIdsMap = new HashMap<String, Integer>();
		this.additionalDriversList = new ArrayList<String>();
		this.profileDrivers = new PrimaryAndAdditionalDriversDetails();
		this.commandCodeResponseAttributesList = new ArrayList<CommandCodeResponseAttribute>();
		this.commandCodeResponseAttributesMap = new HashMap<Integer, AdditionalResponseAttributes>();
		cuiConfiguration = new ChargeableUserIdentityConfiguration();
		authorizationHandlerData = new DiameterAuthorizationHandlerData();
		
		this.prePlugins = new ArrayList<PluginEntryDetail>();
		this.postPlugins = new ArrayList<PluginEntryDetail>();
	}

	
	@XmlElement(name = "profile-drivers")
	public PrimaryAndAdditionalDriversDetails getProfileDrivers() {
		return profileDrivers;
	}

	public void setProfileDrivers(PrimaryAndAdditionalDriversDetails profileDrivers) {
		this.profileDrivers = profileDrivers;
	}
	
	@Override
	@XmlTransient
	public Map<String, Integer> getAuthDriverInstanceIdsMap() {
		return this.authDriverInstanceIdsMap;
	}
	
	public void setAuthDriverInstanceIdsMap(Map<String, Integer> authDriverInstanceIdsMap) {
		this.authDriverInstanceIdsMap = authDriverInstanceIdsMap;
	}


	@Override
	public int getCaseSensitivity() {
		return this.diameterEAPAuthParamsDetails.getiCaseSensitivity();
	}

	@Override
	@XmlTransient
	public String getEapConfId() {
		return this.diameterEAPAuthParamsDetails.getEapConfigId();
	}

	@Override
	@XmlElement(name="id",type=String.class)
	public String getId() {
		return this.policyId;
	}
	
	public void setId(String policyId) {
		this.policyId = policyId;
	}

	@Override
	public String getName() {
		return this.policyName;
	}
	
	public void setName(String policyName) {
		this.policyName = policyName;
	}

	@Override
	@XmlTransient
	public String getRealmSeparator() {
		return this.diameterEAPAuthParamsDetails.getIdentityParamsDetail().getSeparator();
	}

	@Override
	@XmlElement(name="ruleset",type=String.class)
	public String getRuleSet() {
		return this.policyRuleSet;
	}
	public void setRuleSet(String ruleset) {
		this.policyRuleSet = ruleset;
	}

	@Override
	@XmlTransient
	public String getStripUserIdentity() {
		return this.diameterEAPAuthParamsDetails.getIdentityParamsDetail().getStripIdentity();
	}

	@Override
	@XmlTransient
	public boolean getTrimUserIdentity() {
		return this.diameterEAPAuthParamsDetails.getIdentityParamsDetail().getIsTrimIdentity();
	}

	@Override
	@XmlTransient
	public ArrayList<String> getUserIdentities() {
		return this.userIdentities;
	}
	public void  setUserIdentities(ArrayList<String> userIdentities) {
		this.userIdentities = userIdentities;
	}

	@Override
	@XmlTransient
	public boolean isBTrimPassword() {
		return this.diameterEAPAuthParamsDetails.getIdentityParamsDetail().getIsTrimPassword();
	}

	@Override
	@XmlTransient
	public List<String> getAdditionalDrivers() {
		return additionalDriversList;
	}
	
	public void setAdditionalDrivers(List<String> additionalDriversList) {
		this.additionalDriversList = additionalDriversList;
	}
	

	@XmlElement(name="authentication-parameters")
	public DiameterEAPAuthParamsDetails getDiameterEAPAuthParamsDetails() {
		return diameterEAPAuthParamsDetails;
	}

	public void setDiameterEAPAuthParamsDetails(
			DiameterEAPAuthParamsDetails diameterEAPAuthParamsDetails) {
		this.diameterEAPAuthParamsDetails = diameterEAPAuthParamsDetails;
	}

	@XmlTransient
	@Override
	public String getDriverScript() {
		return this.profileDrivers.getDriverScript();
	}

	@Override
	@XmlElementWrapper(name = "command-code-wise-response-attribute-list")
	@XmlElement(name = "command-code-wise-response-attribute")
	public ArrayList<CommandCodeResponseAttribute> getCommandCodeResponseAttributesList() {
		return commandCodeResponseAttributesList;
	}

	public void setCommandCodeResponseAttributesList(
			ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributesList) {
		this.commandCodeResponseAttributesList = commandCodeResponseAttributesList;
	}

	@Override
	@XmlTransient
	public Map<Integer, AdditionalResponseAttributes> getCommandCodeResponseAttributesMap() {
		return commandCodeResponseAttributesMap;
	}

	public void setCommandCodeResponseAttributesMap(
			Map<Integer, AdditionalResponseAttributes> commandCodeResponseAttributesMap) {
		this.commandCodeResponseAttributesMap = commandCodeResponseAttributesMap;
	}
	
	@Override
	@XmlJavaTypeAdapter(value = BooleanAdapter.class)
	@XmlElement(name = "session-management")
	public Boolean isSessionManagementEnabled() {
		return sessionManagementEnabled;
	}
	
	public void setSessionManagementEnabled(Boolean sessionManagementEnabled) {
		this.sessionManagementEnabled  = sessionManagementEnabled;
	}

	@Override
	@XmlElement(name = "request-type", type = int.class)
	public int getRequestType() {
		return this.requestType;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	@Override
	@XmlElement(name = "dia-concurrency-id")
	public String getDiameterConcurrencyConfigId() {
		return diameterConcurrencyConfigId;
	}

	public void setDiameterConcurrencyConfigId(String diameterConcurrencyConfigId) {
		this.diameterConcurrencyConfigId = diameterConcurrencyConfigId;
	}

	@Override
	@XmlElement(name = "additional-dia-concurrency-id")
	public String getAdditionalDiameterConcuurencyConfigId() {
		return additionalDiameterConcuurencyConfigId;
	}

	public void setAdditionalDiameterConcuurencyConfigId(
			String additionalDiameterConcuurencyConfigId) {
		this.additionalDiameterConcuurencyConfigId = additionalDiameterConcuurencyConfigId;
	}

	@Override
	public ChargeableUserIdentityConfiguration getCuiConfiguration() {
		return cuiConfiguration;
	}

	public void setCuiConfiguration(ChargeableUserIdentityConfiguration cuiConfiguration) {
		this.cuiConfiguration = cuiConfiguration;
	}

	public DiameterAuthorizationHandlerData getAuthorizationHandlerData() {
		return authorizationHandlerData;
	}

	public void setAuthorizationHandlerData(DiameterAuthorizationHandlerData authorizationHandlerData) {
		this.authorizationHandlerData = authorizationHandlerData;
	}
	
	@Override
	@XmlElementWrapper(name = "in-plugins-list")
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getPrePlugins() {
		return prePlugins;
	}
	
	public void setPrePluginDataList(List<PluginEntryDetail> prePlugins) {
		this.prePlugins = prePlugins;
	}

	@Override
	@XmlElementWrapper(name = "out-plugins-list")
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getPostPlugins() {
		return postPlugins;
	}
	
	public void setPostPluginDataList(List<PluginEntryDetail> postPlugins) {
		this.postPlugins = postPlugins;
	}
	
	@Override
	@XmlElement(name = "default-response-behavior-type", type = DefaultResponseBehaviorType.class)
	@XmlJavaTypeAdapter(value = DefaultResponseBehaviorTypeAdapter.class)
	public DefaultResponseBehaviorType getDefaultResponseBehaviorType() {
		return responseBehaviorType;
	}


	public void setResponseBehaviorType(
			DefaultResponseBehaviorType responseBehaviorType) {
		this.responseBehaviorType = responseBehaviorType;
	}
	
	@Override
	@XmlElement(name = "default-response-behavior-parameter")
	@Nullable
	public String getDefaultResponseBehaviorParameter() {
		return responseBehaviorParameter;
	}

	public void setResponseBehaviorParameter(String responseBehaviorParameter) {
		this.responseBehaviorParameter = responseBehaviorParameter;
	}
}