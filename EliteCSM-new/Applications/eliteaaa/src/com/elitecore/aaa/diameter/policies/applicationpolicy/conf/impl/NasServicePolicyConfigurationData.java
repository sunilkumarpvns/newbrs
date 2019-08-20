package com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.core.conf.impl.RadiusPolicyDetail;
import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.NasServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.RejectBehavior;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType.DefaultResponseBehaviorTypeAdapter;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthenticationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthorizationHandlerData;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.ChargeableUserIdentityConfiguration;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.BooleanAdapter;

public class NasServicePolicyConfigurationData implements NasServicePolicyConfiguration {
	
	
	public static final int IGNORE_ON_POLICY_NOT_FOUND=1;
	public static final int REJECT_ON_POLICY_NOT_FOUND=2;

	private String diameterRuleSet;	
	private String policyId;
	private String policyName;
	private int requestType = AUTHENTICATE_AND_AUTHORIZE;
	
	private RadiusPolicyDetail authorizationPolicyDetail;
	private Boolean wimaxEnabled = false;
	private String gracePolicy;
	
	private NasAuthDetail nasAuthDetail;
	private NasAcctDetail nasAcctDetail;
	
	
	private ArrayList<String> userIdentities;
	private ArrayList<String> userNameRespAttrList;	
	private Map<String,Integer>authDriverInstanceIdsMap;
	private Map<String,String> secondaryAndCacheDriverRelMap;
	private List<String> additionalDriversList;
	
	private Map<String,Integer>acctDriverInstanceIdsMap;
	private ArrayList<CommandCodeResponseAttribute> commandCodeResponseAttributesList;
	private Map<Integer, AdditionalResponseAttributes> commandCodeResponseAttributesMap;
	
	private Boolean sessionManagementEnabled = false;
	private String diameterConcurrencyConfigId ;
	private String additionalDiameterConcurrencyConfigId;
	private ChargeableUserIdentityConfiguration cuiConfiguration;

	private DiameterAuthenticationHandlerData authenticationHandlerData;
	private DiameterAuthorizationHandlerData authorizationHandlerData;
	
 
	private DefaultResponseBehaviorType responseBehaviorType = DefaultResponseBehaviorType.REJECT;
	private String responseBehaviorParameter = String.valueOf(RejectBehavior.DEFAULT_RESULT_CODE.code);
	
	public NasServicePolicyConfigurationData() {		
		this.userIdentities = new ArrayList<String>();		
		this.userNameRespAttrList = new ArrayList<String>();		
		this.authDriverInstanceIdsMap = new HashMap<String, Integer>();
		this.secondaryAndCacheDriverRelMap = new HashMap<String,String>();
		this.acctDriverInstanceIdsMap = new HashMap<String, Integer>();
		this.additionalDriversList = new ArrayList<String>();
		this.authorizationPolicyDetail = new RadiusPolicyDetail();
		this.nasAuthDetail = new NasAuthDetail();
		this.nasAcctDetail = new NasAcctDetail();
		this.commandCodeResponseAttributesList = new ArrayList<CommandCodeResponseAttribute>();
		this.commandCodeResponseAttributesMap = new HashMap<Integer, AdditionalResponseAttributes>();
		cuiConfiguration = new ChargeableUserIdentityConfiguration();
		this.authenticationHandlerData = new DiameterAuthenticationHandlerData();
		this.authorizationHandlerData = new DiameterAuthorizationHandlerData();
	}

	public void setDiameterRuleSet(String diameterRuleSet) {
		this.diameterRuleSet = diameterRuleSet;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public void setUserIdentities(ArrayList<String> userIdentities) {
		this.userIdentities = userIdentities;
	}

	public void setUserNameRespAttrList(ArrayList<String> userNameRespAttrList) {
		this.userNameRespAttrList = userNameRespAttrList;
	}

	public void setAuthDriverInstanceIdsMap(
			Map<String, Integer> authDriverInstanceIdsMap) {
		this.authDriverInstanceIdsMap = authDriverInstanceIdsMap;
	}

	public void setSecondaryAndCacheDriverRelMap(
			Map<String, String> secondaryAndCacheDriverRelMap) {
		this.secondaryAndCacheDriverRelMap = secondaryAndCacheDriverRelMap;
	}

	public void setAdditionalDriversList(List<String> additionalDriversList) {
		this.additionalDriversList = additionalDriversList;
	}

	public void setAcctDriverInstanceIdsMap(
			Map<String, Integer> acctDriverInstanceIdsMap) {
		this.acctDriverInstanceIdsMap = acctDriverInstanceIdsMap;
	}
	

	@XmlElement(name="authentication-detail")
	public NasAuthDetail getNasAuthDetail() {
		return nasAuthDetail;
	}


	public void setNasAuthDetail(NasAuthDetail nasAuthDetail) {
		this.nasAuthDetail = nasAuthDetail;
	}

	@XmlElement(name="accounting-detail")
	public NasAcctDetail getNasAcctDetail() {
		return nasAcctDetail;
	}


	public void setNasAcctDetail(NasAcctDetail nasAcctDetail) {
		this.nasAcctDetail = nasAcctDetail;
	}

	@XmlElement(name="authorization-parameters")
	public RadiusPolicyDetail getAuthorizationPolicyDetail() {
		return authorizationPolicyDetail;
	}

	public void setAuthorizationPolicyDetail(
			RadiusPolicyDetail authorizationPolicyDetail) {
		this.authorizationPolicyDetail = authorizationPolicyDetail;
	}

	@Override
	@XmlElement(name="ruleset",type=String.class)
	public String getRuleSet() {
		return this.diameterRuleSet;
	}
	
	public void setRuleSet(String diameterRuleSet) {
		this.diameterRuleSet = diameterRuleSet;
	}
	
	
	@Override
	@XmlElement(name="name",type=String.class)
	public String getName() {
		return this.policyName;
	}
	public void setName(String policyName) {
		this.policyName = policyName;
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
	@XmlTransient
	public int getCaseSensitivity() {	
		return this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUpdateIdentity().getCase();
	}

	@Override
	@XmlTransient
	public String getRealmSeparator() {
		return this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUpdateIdentity().getSeparator();
	}
	
	@Override
	@XmlTransient
	public String getStripUserIdentity() {
		return this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUpdateIdentity().getStripIdentity();
	}

	@Override
	@XmlTransient
	public boolean getTrimUserIdentity() {		
		return this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUpdateIdentity().getIsTrimIdentity();
	}

	@Override
	@XmlTransient
	public ArrayList<String> getUserIdentities() {		
		return this.userIdentities;
	}

	@Override
	@XmlTransient
	public String getUserNameConfiguration() {		
		return this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUserName();
	}

	@Override
	@XmlTransient
	public ArrayList<String> getUserNameRespAttrList() {
		return this.userNameRespAttrList;
	}

	@Override
	@XmlTransient
	public Map<String,Integer> getAuthDriverInstanceIdsMap() {
		return this.authDriverInstanceIdsMap;
	}
	@Override
	@XmlTransient
	public Map<String,Integer> getAcctDriverInstanceIdsMap() {
		return this.acctDriverInstanceIdsMap;
	}
	
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println();
		out.println();
		out.println(" -- Nas Policy Configuration -- ");
		out.println("       Name                          = " + policyName);
		out.println(" 		Policy Id     				  = " + policyId);
		out.println("  		Rule Set                      = " + diameterRuleSet);
		out.println("		Default response behavior     = " + responseBehaviorType);
		out.println("		Response behavior parameter   = " + Strings.valueOf(responseBehaviorParameter, ""));
		out.println("    	Case Sensitivity              = " + ((this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUpdateIdentity().getCase() == 1)?"NONE":(this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUpdateIdentity().getCase()==2)?"LOWER":"UPPER"));
		out.println("    	User Identities               = " + userIdentities);
		out.println("    	Strip User Identity           = " + this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUpdateIdentity().getStripIdentity());
		out.println("    	Realm Separator           	  = " + this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUpdateIdentity().getSeparator());
		out.println("    	Trim User Identity            = " + this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUpdateIdentity().getIsTrimIdentity());
		out.println("    	CUI Configuration");
		out.println("    	CUI                           = " + getCuiConfiguration().getCui());
		out.println("    	Advanced CUI Expression       = " + getCuiConfiguration().getExpression() != null ? getCuiConfiguration().getExpression() : "");
		for (String cuiRespAttr : getCuiConfiguration().getAuthenticationCuiAttributes()) {
            out.println("			" + cuiRespAttr );
		}
		out.println("    	Username                      = " + this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUserName());
		out.println("    	Request Type                  = " + requestType);
		out.println("    	Anonymous Profile Identity    = " + this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getAnonymousProfileIdentity());
		out.println("    	Wimax enabled                 = " + this.wimaxEnabled);
		out.println("    	Reject on Check Item Not Found = " + authorizationPolicyDetail.isRejectOnCheckItemNotFound());
		out.println("    	Reject on Reject Item Not Found = " + authorizationPolicyDetail.isRejectOnRejectItemNotFound());
		out.println("    	Continue on Policy Not Found  = " + authorizationPolicyDetail.isAcceptOnPolicyOnFound());
		out.println("    	Grace policy  				  = " + this.gracePolicy);
		out.println("    	CUI Response Attribute List   :");
		
		out.println("    	User Name Response AttrList   :");
		final int userNameRespAttrListSize=userNameRespAttrList.size();
		for (int i=0; i< userNameRespAttrListSize; i++){
                out.println("			"+ userNameRespAttrList.get(i) );
        }
		out.println("    ");
		out.close();
		return stringBuffer.toString();
	}

	@Override
	@XmlTransient
	public boolean isBTrimPassword(){
		return this.authenticationHandlerData.getSubscriberProfileRepositoryDetails().getUpdateIdentity().getIsTrimPassword();
	}
	@Override
	@XmlTransient
	public Map<String, String> getSecondaryAndCacheDriverRelMap() {
		return secondaryAndCacheDriverRelMap;
	}
	
	@Override
	@XmlElement(name = "request-mode",type = int.class)
	public int getRequestType() {
		return this.requestType;
	}
	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	@Override
	@XmlTransient
	public List<String> getAdditionalDrivers() {
		return additionalDriversList;
	}
	@Override
	@XmlTransient
	public String getAuthDriverScript() {
		return this.nasAuthDetail.getScript();
	}
	@Override
	@XmlTransient
	public String getAcctDriverScript() {
		return this.nasAcctDetail.getScript();
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
		this.sessionManagementEnabled = sessionManagementEnabled;
	}

	@Override
	@XmlTransient
	public String getAnonymousProfileIdentity() {
		return authenticationHandlerData.getSubscriberProfileRepositoryDetails().getAnonymousProfileIdentity();
	}

	@XmlElement(name = "dia-concurrency-id")
	public String getDiameterConcurrencyConfigId() {
		return diameterConcurrencyConfigId;
	}

	public void setDiameterConcurrencyConfigId(String diameterConcurrencyConfigId) {
		this.diameterConcurrencyConfigId = diameterConcurrencyConfigId;
	}

	@XmlElement(name = "additional-dia-concurrency-id", defaultValue="-1")
	public String getAdditionalDiameterConcuurencyConfigId() {
		return additionalDiameterConcurrencyConfigId;
	}

	public void setAdditionalDiameterConcuurencyConfigId(
			String additionalDiameterConcuurencyConfigId) {
		this.additionalDiameterConcurrencyConfigId = additionalDiameterConcuurencyConfigId;
	}

	@Override
	@XmlElement(name = "chargeable-user-identity")
	public ChargeableUserIdentityConfiguration getCuiConfiguration() {
		return cuiConfiguration;
	}
	
	public void setCuiConfiguration(ChargeableUserIdentityConfiguration cuiConfiguration) {
		this.cuiConfiguration = cuiConfiguration;
	}

	public DiameterAuthenticationHandlerData getAuthenticationHandlerData() {
		return authenticationHandlerData;
	}

	public void setAuthenticationHandlerData(DiameterAuthenticationHandlerData authenticationHandlerData) {
		this.authenticationHandlerData = authenticationHandlerData;
	}

	public DiameterAuthorizationHandlerData getAuthorizationHandlerData() {
		return authorizationHandlerData;
	}

	public void setAuthorizationHandlerData(DiameterAuthorizationHandlerData authorizationHandlerData) {
		this.authorizationHandlerData = authorizationHandlerData;
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
