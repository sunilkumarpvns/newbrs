package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form;

import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.*;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.*;

import java.util.ArrayList;
import java.util.List;

public class CreateRadiusServicePolicyForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private boolean authentication=false;
	private boolean accounting=false;
	private String ruleSetAuth;
	private String ruleSetAcct;
	private boolean validatePacket = false;
	private String authResponseBehavior;
	private String hotlinePolicy;
	private String acctResponseBehavior;
	private String sessionManagerId;
	private String authResponseAttributes;
	private String acctResponseAttributes;
	private String status;
	private boolean proxyCommunication;
	private String cui;
	private String advancedCuiExpression;
	private String acctAttributes;
	private String authAttributes;
	private String userIdentity;
	private boolean imdgEnable;
	
	private List<AuthMethodTypeData> authMethodTypeDataList;
	private List<DigestConfigInstanceData> digestConfigInstanceDataList;
	private List<ISessionManagerInstanceData> sessionManagerInstanceDataList;
	private List<EAPConfigData> eapConfigurationList;
	private List<GracepolicyData> gracePolicyList;
	private List<ExternalSystemInterfaceInstanceData> authBroadcastServerList ;
	private List<ExternalSystemInterfaceInstanceData> dynaAuthRelDataList;
	private List<ExternalSystemInterfaceInstanceData> acctESIList;
	private List<String> radiusEsiGroupNames;

	private List<Long> authMethods;

	private String[] selectedAuthMethodTypes;
	
	private List<AuthPolicyMainDriverRelData> mainDriverList;
	private List<DriverInstanceData> 	      driverInstanceList;
	private List<AuthPolicyAdditionalDriverRelData> additionalDriverList;
	
	private List<AuthPolicyExternalSystemRelData> ipPoolServerRelList;
	private List<AuthPolicyExternalSystemRelData> proxyServerRelList;
	private List<AuthPolicyExternalSystemRelData> prepaidServerRelList;
	private List<AuthPolicyExternalSystemRelData> chargingGatewayServerRelList;
	private List<AuthPolicyBroadcastESIRelData>   broadcastingServerRelList;

	private String prePluginsList;
	private String postPluginsList;
	private Long esiInstanceId;
	private String trueOnAttributeNotFound;
	private String isResponseMandatory;
	private long secDriverInstanceId;
	private long cacheDriverInstId;
	private List<DriverInstanceData> driversList;
	private List<DriverInstanceData> cacheableDriverList;
	private List<AuthPolicySecDriverRelData> authPolicySecDriverRelDataList;
	
	private long ipPoolTranslationMapConfigId;
	private String ipPoolScript;
	private long prepaidTranslationMapConfigId;
	private String prepaidScript;
	private long chargingGatewayTranslationMapConfigId;
	private String chargingGatewayScript;
	private List<TranslationMappingConfData> translationMappingConfDataList;
	private List<CopyPacketTranslationConfData> copyPacketMappingConfDataList;
	
	private String driverScript;
	private String serviceHandlerDetails;
	private String authHandlerJson;
	private String profileLookupDriverJson;
	private String authorizationHandlerJson;
	private String concurrencyHandlerJson;
	private String cdrGenerationJson;
	private String pluginHandlerJson;
	private String coaDMGenerationJson;
	private String proxyCommunicationJson;
	private String broadcastCommunicationJson;
	private String concurrencyIMDGHandlerJson;
	private String statefulProxySequentialHandlerJson;
	private String statefulProxyBroadcastHandlerAuthJson;

	/* Auth handler list*/
	private List<AuthenticationHandler> authHandlerList;
	private List<ProfileLookupDriver> profileLookupList;
	private List<AuthorizationHandler> authorizationList;
	private List<ConcurrencyHandler> concurrencyHandlerList;
	private List<CDRGeneration> authCDRGenerationList;
	private List<PluginHandler> authPluginHandlerList;
	private List<COADMGeneration> authCOADMGenList;
	private List<ProxyCommunication> authProxyCommunicationList;
	private List<BroadcastCommunication> authBroadcastCommunicationList;
	private List<ConcurrencyIMDGHandler> concurrencyIMDGHandlerList;
	private List<StatefulProxySequentialHandler> statefulProxySequentialHandlerList;
	private List<StatefulProxyBroadcastHandler> statefulProxyBroadcastHandlersList;

	/* Acct handler list*/
	private List<CDRGeneration> acctCDRGenerationList;
	private List<PluginHandler> acctPluginHandlerList;
	private List<COADMGeneration> acctCOADMGenList;
	private List<ProxyCommunication> acctProxyCommunicationList;
	private List<BroadcastCommunication> acctBroadcastCommunicationList;
	private List<ProfileLookupDriver> acctProfileLookupDriver;
	private List<StatefulProxySequentialHandler> statefulProxySequentialHandlerAcctList;
	private List<StatefulProxyBroadcastHandler> statefulProxyBroadcastAcctHandlersList;
	
	/* Pre and Post Plugin List */
	private List<PolicyPluginData> authPrePluginData;
	private List<PolicyPluginData> authPostPluginData;
	private List<PolicyPluginData> acctPrePluginData;
	private List<PolicyPluginData> acctPostPluginData;
	
	private String acctCDRGenerationJson;
	private String acctPluginHandlerJson;
	private String acctCOADMGenerationJson;
	private String acctProxyCommunicationJson;
	private String acctBroadcastCommunicationJson;
	private String acctHandlerJson;
	
	private String authPrePluginJson;
	private String authPostPluginJson;
	
	private String acctPrePluginJson;
	private String acctPostPluginJson;
	private String statefulProxySequentialHandlerAcctJson;
    private String statefulProxyBroadcastHandlerAcctJson;
	
	private List<PluginData> authPrePluginDataList;
	private List<PluginData> authPostPluginDataList;
	private List<PluginData> acctPrePluginDataList;
	private List<PluginData> acctPostPluginDataList;
	
	private String[] authPrePluginStr;
	private String[] authPostpluginStr;
	private String[] acctPrePluginStr;
	private String[] acctPostpluginStr;
	
	private List<ScriptInstanceData> driverScriptList;
	private List<ScriptInstanceData> externalScriptList;

	private List<String> imdgFieldNames = new ArrayList<>();
	
	public List<AuthPolicySecDriverRelData> getAuthPolicySecDriverRelDataList() {
		return authPolicySecDriverRelDataList;
	}

	public void setAuthPolicySecDriverRelDataList(
			List<AuthPolicySecDriverRelData> authPolicySecDriverRelDataList) {
		this.authPolicySecDriverRelDataList = authPolicySecDriverRelDataList;
	}

	public List<DriverInstanceData> getCacheableDriverList() {
		return cacheableDriverList;
	}

	public void setCacheableDriverList(List<DriverInstanceData> cacheableDriverList) {
		this.cacheableDriverList = cacheableDriverList;
	}

	public long getSecDriverInstanceId() {
		return secDriverInstanceId;
	}

	public void setSecDriverInstanceId(long secDriverInstanceId) {
		this.secDriverInstanceId = secDriverInstanceId;
	}

	public long getCacheDriverInstId() {
		return cacheDriverInstId;
	}

	public void setCacheDriverInstId(long cacheDriverInstId) {
		this.cacheDriverInstId = cacheDriverInstId;
	}

	public String getIsResponseMandatory() {
		return isResponseMandatory;
	}

	public void setIsResponseMandatory(String isResponseMandatory) {
		this.isResponseMandatory = isResponseMandatory;
	}

	public String getTrueOnAttributeNotFound() {
		return trueOnAttributeNotFound;
	}

	public void setTrueOnAttributeNotFound(String trueOnAttributeNotFound) {
		this.trueOnAttributeNotFound = trueOnAttributeNotFound;
	}

	public boolean getValidatePacket() {
		return validatePacket;
	}

	public void setValidatePacket(boolean validatePacket) {
		this.validatePacket = validatePacket;
	}

	public List<ExternalSystemInterfaceInstanceData> getAuthBroadcastServerList() {
		return authBroadcastServerList;
	}

	public void setAuthBroadcastServerList(
			List<ExternalSystemInterfaceInstanceData> authBroadcastServerList) {
		this.authBroadcastServerList = authBroadcastServerList;
	}

	public Long getEsiInstanceId() {
		return esiInstanceId;
	}

	public void setEsiInstanceId(Long esiInstanceId) {
		this.esiInstanceId = esiInstanceId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public List<ISessionManagerInstanceData> getSessionManagerInstanceDataList() {
		return sessionManagerInstanceDataList;
	}

	public void setSessionManagerInstanceDataList(
			List<ISessionManagerInstanceData> sessionManagerInstanceDataList) {
		this.sessionManagerInstanceDataList = sessionManagerInstanceDataList;
	}

	public List<Long> getAuthMethods() {
		return authMethods;
	}

	public void setAuthMethods(List<Long> authMethods) {
		this.authMethods = authMethods;
	}
	public List<EAPConfigData> getEapConfigurationList() {
		return eapConfigurationList;
	}
	
	public String[] getSelectedAuthMethodTypes() {
		return selectedAuthMethodTypes;
	}

	public void setSelectedAuthMethodTypes(String[] selectedAuthMethodTypes) {
		this.selectedAuthMethodTypes = selectedAuthMethodTypes;
	}
	public void setEapConfigurationList(List<EAPConfigData> eapConfigurationList) {
		this.eapConfigurationList = eapConfigurationList;
	}

	public List<DigestConfigInstanceData> getDigestConfigInstanceDataList() {
		return digestConfigInstanceDataList;
	}
	
	public void setDigestConfigInstanceDataList(
			List<DigestConfigInstanceData> digestConfigInstanceDataList) {
		this.digestConfigInstanceDataList = digestConfigInstanceDataList;
	}
	

	public List<GracepolicyData> getGracePolicyList() {
		return gracePolicyList;
	}

	public void setGracePolicyList(List<GracepolicyData> gracePolicyList) {
		this.gracePolicyList = gracePolicyList;
	}

	public List<AuthMethodTypeData> getAuthMethodTypeDataList() {
		return authMethodTypeDataList;
	}
	
	public void setAuthMethodTypeDataList(
			List<AuthMethodTypeData> authMethodTypeDataList) {
		this.authMethodTypeDataList = authMethodTypeDataList;
	}	
	public List<AuthPolicyMainDriverRelData> getMainDriverList() {
		return mainDriverList;
	}
	public void setMainDriverList(List<AuthPolicyMainDriverRelData> mainDriverList) {
		this.mainDriverList = mainDriverList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSessionManagerId() {
		return sessionManagerId;
	}
	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}
	
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public List<AuthPolicyExternalSystemRelData> getIpPoolServerRelList() {
		return ipPoolServerRelList;
	}

	public void setIpPoolServerRelList(
			List<AuthPolicyExternalSystemRelData> ipPoolServerRelList) {
		this.ipPoolServerRelList = ipPoolServerRelList;
	}

	public List<AuthPolicyExternalSystemRelData> getProxyServerRelList() {
		return proxyServerRelList;
	}

	public void setProxyServerRelList(
			List<AuthPolicyExternalSystemRelData> proxyServerRelList) {
		this.proxyServerRelList = proxyServerRelList;
	}

	public List<AuthPolicyExternalSystemRelData> getPrepaidServerRelList() {
		return prepaidServerRelList;
	}

	public void setPrepaidServerRelList(
			List<AuthPolicyExternalSystemRelData> prepaidServerRelList) {
		this.prepaidServerRelList = prepaidServerRelList;
	}

	public List<AuthPolicyExternalSystemRelData> getChargingGatewayServerRelList() {
		return chargingGatewayServerRelList;
	}

	public void setChargingGatewayServerRelList(
			List<AuthPolicyExternalSystemRelData> chargingGatewayServerRelList) {
		this.chargingGatewayServerRelList = chargingGatewayServerRelList;
	}
	
	public String getAuthResponseAttributes() {
		return authResponseAttributes;
	}

	public void setAuthResponseAttributes(String authResponseAttributes) {
		this.authResponseAttributes = authResponseAttributes;
	}
	
	public String getAcctResponseAttributes() {
		return acctResponseAttributes;
	}
	
	public void setAcctResponseAttributes(String acctResponseAttributes) {
		this.acctResponseAttributes = acctResponseAttributes;
	}
	
	
	public String getHotlinePolicy() {
		return hotlinePolicy;
	}

	public void setHotlinePolicy(String hotlinePolicy) {
		this.hotlinePolicy = hotlinePolicy;
	}

	public List<AuthPolicyBroadcastESIRelData> getBroadcastingServerRelList() {
		return broadcastingServerRelList;
	}

	public void setBroadcastingServerRelList(
			List<AuthPolicyBroadcastESIRelData> broadcastingServerRelList) {
		this.broadcastingServerRelList = broadcastingServerRelList;
	}


	public List<DriverInstanceData> getDriverInstanceList() {
		return driverInstanceList;
	}

	public void setDriverInstanceList(List<DriverInstanceData> driverInstanceList) {
		this.driverInstanceList = driverInstanceList;
	}		
	
	public String getPrePluginsList() {
		return prePluginsList;
	}

	public void setPrePluginsList(String prePluginsList) {
		this.prePluginsList = prePluginsList;
	}

	public String getPostPluginsList() {
		return postPluginsList;
	}

	public void setPostPluginsList(String postPluginsList) {
		this.postPluginsList = postPluginsList;
	}
	
	
	public List<DriverInstanceData> getDriversList() {
		return driversList;
	}

	public void setDriversList(List<DriverInstanceData> driversList) {
		this.driversList = driversList;
	}
	
	public List<AuthPolicyAdditionalDriverRelData> getAdditionalDriverList() {
		return additionalDriverList;
	}

	public void setAdditionalDriverList(
			List<AuthPolicyAdditionalDriverRelData> additionalDriverList) {
		this.additionalDriverList = additionalDriverList;
	}

	public long getIpPoolTranslationMapConfigId() {
		return ipPoolTranslationMapConfigId;
	}

	public void setIpPoolTranslationMapConfigId(long ipPoolTranslationMapConfigId) {
		this.ipPoolTranslationMapConfigId = ipPoolTranslationMapConfigId;
	}

	public String getIpPoolScript() {
		return ipPoolScript;
	}

	public void setIpPoolScript(String ipPoolScript) {
		this.ipPoolScript = ipPoolScript;
	}
	
	public long getPrepaidTranslationMapConfigId() {
		return prepaidTranslationMapConfigId;
	}

	public void setPrepaidTranslationMapConfigId(long prepaidTranslationMapConfigId) {
		this.prepaidTranslationMapConfigId = prepaidTranslationMapConfigId;
	}

	public String getPrepaidScript() {
		return prepaidScript;
	}

	public void setPrepaidScript(String prepaidScript) {
		this.prepaidScript = prepaidScript;
	}
    
	public long getChargingGatewayTranslationMapConfigId() {
		return chargingGatewayTranslationMapConfigId;
	}

	public void setChargingGatewayTranslationMapConfigId(
			long chargingGatewayTranslationMapConfigId) {
		this.chargingGatewayTranslationMapConfigId = chargingGatewayTranslationMapConfigId;
	}

	public String getChargingGatewayScript() {
		return chargingGatewayScript;
	}

	public void setChargingGatewayScript(String chargingGatewayScript) {
		this.chargingGatewayScript = chargingGatewayScript;
	}

	public List<TranslationMappingConfData> getTranslationMappingConfDataList() {
		return translationMappingConfDataList;
	}

	public void setTranslationMappingConfDataList(
			List<TranslationMappingConfData> translationMappingConfDataList) {
		this.translationMappingConfDataList = translationMappingConfDataList;
	}

	public String getDriverScript() {
		return driverScript;
	}

	public void setDriverScript(String driverScript) {
		this.driverScript = driverScript;
	}

	public boolean isAuthentication() {
		return authentication;
	}

	public void setAuthentication(boolean authentication) {
		this.authentication = authentication;
	}

	public boolean isAccounting() {
		return accounting;
	}

	public void setAccounting(boolean accounting) {
		this.accounting = accounting;
	}

	public String getAuthResponseBehavior() {
		return authResponseBehavior;
	}

	public void setAuthResponseBehavior(String authResponseBehavior) {
		this.authResponseBehavior = authResponseBehavior;
	}

	public String getAcctResponseBehavior() {
		return acctResponseBehavior;
	}

	public void setAcctResponseBehavior(String acctResponseBehavior) {
		this.acctResponseBehavior = acctResponseBehavior;
	}

	public List<ExternalSystemInterfaceInstanceData> getDynaAuthRelDataList() {
		return dynaAuthRelDataList;
	}

	public void setDynaAuthRelDataList(List<ExternalSystemInterfaceInstanceData> dynaAuthRelDataList) {
		this.dynaAuthRelDataList = dynaAuthRelDataList;
	}

	public String getServiceHandlerDetails() {
		return serviceHandlerDetails;
	}

	public void setServiceHandlerDetails(String serviceHandlerDetails) {
		this.serviceHandlerDetails = serviceHandlerDetails;
	}

	public String getAuthHandlerJson() {
		return authHandlerJson;
	}

	public void setAuthHandlerJson(String authHandlerJson) {
		this.authHandlerJson = authHandlerJson;
	}

	public String getProfileLookupDriverJson() {
		return profileLookupDriverJson;
	}

	public void setProfileLookupDriverJson(String profileLookupDriverJson) {
		this.profileLookupDriverJson = profileLookupDriverJson;
	}

	public String getAuthorizationHandlerJson() {
		return authorizationHandlerJson;
	}

	public void setAuthorizationHandlerJson(String authorizationHandlerJson) {
		this.authorizationHandlerJson = authorizationHandlerJson;
	}

	public String getConcurrencyHandlerJson() {
		return concurrencyHandlerJson;
	}

	public void setConcurrencyHandlerJson(String concurrencyHandlerJson) {
		this.concurrencyHandlerJson = concurrencyHandlerJson;
	}

	public String getCdrGenerationJson() {
		return cdrGenerationJson;
	}

	public void setCdrGenerationJson(String cdrGenerationJson) {
		this.cdrGenerationJson = cdrGenerationJson;
	}

	public String getPluginHandlerJson() {
		return pluginHandlerJson;
	}

	public void setPluginHandlerJson(String pluginHandlerJson) {
		this.pluginHandlerJson = pluginHandlerJson;
	}

	public String getCoaDMGenerationJson() {
		return coaDMGenerationJson;
	}

	public void setCoaDMGenerationJson(String coaDMGenerationJson) {
		this.coaDMGenerationJson = coaDMGenerationJson;
	}

	public String getProxyCommunicationJson() {
		return proxyCommunicationJson;
	}

	public void setProxyCommunicationJson(String proxyCommunicationJson) {
		this.proxyCommunicationJson = proxyCommunicationJson;
	}

	public String getBroadcastCommunicationJson() {
		return broadcastCommunicationJson;
	}

	public void setBroadcastCommunicationJson(String broadcastCommunicationJson) {
		this.broadcastCommunicationJson = broadcastCommunicationJson;
	}

	public String getConcurrencyIMDGHandlerJson() {
		return concurrencyIMDGHandlerJson;
	}

	public void setConcurrencyIMDGHandlerJson(String concurrencyIMDGHandlerJson) {
		this.concurrencyIMDGHandlerJson = concurrencyIMDGHandlerJson;
	}

	public String getStatefulProxySequentialHandlerJson() {
		return statefulProxySequentialHandlerJson;
	}

	public void setStatefulProxySequentialHandlerJson(String statefulProxySequentialHandlerJson) {
		this.statefulProxySequentialHandlerJson = statefulProxySequentialHandlerJson;
	}

	public String getAcctCDRGenerationJson() {
		return acctCDRGenerationJson;
	}

	public void setAcctCDRGenerationJson(String acctCDRGenerationJson) {
		this.acctCDRGenerationJson = acctCDRGenerationJson;
	}

	public String getAcctPluginHandlerJson() {
		return acctPluginHandlerJson;
	}

	public void setAcctPluginHandlerJson(String acctPluginHandlerJson) {
		this.acctPluginHandlerJson = acctPluginHandlerJson;
	}

	public String getAcctCOADMGenerationJson() {
		return acctCOADMGenerationJson;
	}

	public void setAcctCOADMGenerationJson(String acctCOADMGenerationJson) {
		this.acctCOADMGenerationJson = acctCOADMGenerationJson;
	}

	public String getAcctProxyCommunicationJson() {
		return acctProxyCommunicationJson;
	}

	public void setAcctProxyCommunicationJson(String acctProxyCommunicationJson) {
		this.acctProxyCommunicationJson = acctProxyCommunicationJson;
	}

	public String getAcctBroadcastCommunicationJson() {
		return acctBroadcastCommunicationJson;
	}

	public void setAcctBroadcastCommunicationJson(
			String acctBroadcastCommunicationJson) {
		this.acctBroadcastCommunicationJson = acctBroadcastCommunicationJson;
	}

	public List<AuthenticationHandler> getAuthHandlerList() {
		return authHandlerList;
	}

	public void setAuthHandlerList(List<AuthenticationHandler> authHandlerList) {
		this.authHandlerList = authHandlerList;
	}

	public List<ProfileLookupDriver> getProfileLookupList() {
		return profileLookupList;
	}

	public void setProfileLookupList(List<ProfileLookupDriver> profileLookupList) {
		this.profileLookupList = profileLookupList;
	}

	public List<AuthorizationHandler> getAuthorizationList() {
		return authorizationList;
	}

	public void setAuthorizationList(List<AuthorizationHandler> authorizationList) {
		this.authorizationList = authorizationList;
	}

	public List<ConcurrencyHandler> getConcurrencyHandlerList() {
		return concurrencyHandlerList;
	}

	public void setConcurrencyHandlerList(
			List<ConcurrencyHandler> concurrencyHandlerList) {
		this.concurrencyHandlerList = concurrencyHandlerList;
	}

	public List<CDRGeneration> getAuthCDRGenerationList() {
		return authCDRGenerationList;
	}

	public void setAuthCDRGenerationList(List<CDRGeneration> authCDRGenerationList) {
		this.authCDRGenerationList = authCDRGenerationList;
	}

	public List<PluginHandler> getAuthPluginHandlerList() {
		return authPluginHandlerList;
	}

	public void setAuthPluginHandlerList(List<PluginHandler> authPluginHandlerList) {
		this.authPluginHandlerList = authPluginHandlerList;
	}

	public List<COADMGeneration> getAuthCOADMGenList() {
		return authCOADMGenList;
	}

	public void setAuthCOADMGenList(List<COADMGeneration> authCOADMGenList) {
		this.authCOADMGenList = authCOADMGenList;
	}

	public List<ProxyCommunication> getAuthProxyCommunicationList() {
		return authProxyCommunicationList;
	}

	public void setAuthProxyCommunicationList(
			List<ProxyCommunication> authProxyCommunicationList) {
		this.authProxyCommunicationList = authProxyCommunicationList;
	}

	public List<BroadcastCommunication> getAuthBroadcastCommunicationList() {
		return authBroadcastCommunicationList;
	}

	public void setAuthBroadcastCommunicationList(
			List<BroadcastCommunication> authBroadcastCommunicationList) {
		this.authBroadcastCommunicationList = authBroadcastCommunicationList;
	}

	public List<CDRGeneration> getAcctCDRGenerationList() {
		return acctCDRGenerationList;
	}

	public void setAcctCDRGenerationList(List<CDRGeneration> acctCDRGenerationList) {
		this.acctCDRGenerationList = acctCDRGenerationList;
	}

	public List<PluginHandler> getAcctPluginHandlerList() {
		return acctPluginHandlerList;
	}

	public void setAcctPluginHandlerList(List<PluginHandler> acctPluginHandlerList) {
		this.acctPluginHandlerList = acctPluginHandlerList;
	}

	public List<COADMGeneration> getAcctCOADMGenList() {
		return acctCOADMGenList;
	}

	public void setAcctCOADMGenList(List<COADMGeneration> acctCOADMGenList) {
		this.acctCOADMGenList = acctCOADMGenList;
	}

	public List<ProxyCommunication> getAcctProxyCommunicationList() {
		return acctProxyCommunicationList;
	}

	public void setAcctProxyCommunicationList(
			List<ProxyCommunication> acctProxyCommunicationList) {
		this.acctProxyCommunicationList = acctProxyCommunicationList;
	}

	public List<BroadcastCommunication> getAcctBroadcastCommunicationList() {
		return acctBroadcastCommunicationList;
	}

	public void setAcctBroadcastCommunicationList(
			List<BroadcastCommunication> acctBroadcastCommunicationList) {
		this.acctBroadcastCommunicationList = acctBroadcastCommunicationList;
	}

	public String getRuleSetAuth() {
		return ruleSetAuth;
	}

	public void setRuleSetAuth(String ruleSetAuth) {
		this.ruleSetAuth = ruleSetAuth;
	}

	public String getRuleSetAcct() {
		return ruleSetAcct;
	}

	public void setRuleSetAcct(String ruleSetAcct) {
		this.ruleSetAcct = ruleSetAcct;
	}

	public boolean isProxyCommunication() {
		return proxyCommunication;
	}

	public void setProxyCommunication(boolean proxyCommunication) {
		this.proxyCommunication = proxyCommunication;
	}

	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}

	public String getAcctAttributes() {
		return acctAttributes;
	}

	public void setAcctAttributes(String acctAttributes) {
		this.acctAttributes = acctAttributes;
	}

	public String getAuthAttributes() {
		return authAttributes;
	}

	public void setAuthAttributes(String authAttributes) {
		this.authAttributes = authAttributes;
	}

	public String getAuthPrePluginJson() {
		return authPrePluginJson;
	}

	public void setAuthPrePluginJson(String authPrePluginJson) {
		this.authPrePluginJson = authPrePluginJson;
	}

	public String getAuthPostPluginJson() {
		return authPostPluginJson;
	}

	public void setAuthPostPluginJson(String authPostPluginJson) {
		this.authPostPluginJson = authPostPluginJson;
	}

	public String getAcctPrePluginJson() {
		return acctPrePluginJson;
	}

	public void setAcctPrePluginJson(String acctPrePluginJson) {
		this.acctPrePluginJson = acctPrePluginJson;
	}

	public String getAcctPostPluginJson() {
		return acctPostPluginJson;
	}

	public void setAcctPostPluginJson(String acctPostPluginJson) {
		this.acctPostPluginJson = acctPostPluginJson;
	}

	public List<PluginData> getAuthPrePluginDataList() {
		return authPrePluginDataList;
	}

	public void setAuthPrePluginDataList(List<PluginData> authPrePluginDataList) {
		this.authPrePluginDataList = authPrePluginDataList;
	}

	public List<PluginData> getAuthPostPluginDataList() {
		return authPostPluginDataList;
	}

	public void setAuthPostPluginDataList(List<PluginData> authPostPluginDataList) {
		this.authPostPluginDataList = authPostPluginDataList;
	}

	public List<PluginData> getAcctPrePluginDataList() {
		return acctPrePluginDataList;
	}

	public void setAcctPrePluginDataList(List<PluginData> acctPrePluginDataList) {
		this.acctPrePluginDataList = acctPrePluginDataList;
	}

	public List<PluginData> getAcctPostPluginDataList() {
		return acctPostPluginDataList;
	}

	public void setAcctPostPluginDataList(List<PluginData> acctPostPluginDataList) {
		this.acctPostPluginDataList = acctPostPluginDataList;
	}

	public String[] getAuthPrePluginStr() {
		return authPrePluginStr;
	}

	public void setAuthPrePluginStr(String[] authPrePluginStr) {
		this.authPrePluginStr = authPrePluginStr;
	}

	public String[] getAuthPostpluginStr() {
		return authPostpluginStr;
	}

	public void setAuthPostpluginStr(String[] authPostpluginStr) {
		this.authPostpluginStr = authPostpluginStr;
	}

	public String[] getAcctPrePluginStr() {
		return acctPrePluginStr;
	}

	public void setAcctPrePluginStr(String[] acctPrePluginStr) {
		this.acctPrePluginStr = acctPrePluginStr;
	}

	public String[] getAcctPostpluginStr() {
		return acctPostpluginStr;
	}

	public void setAcctPostpluginStr(String[] acctPostpluginStr) {
		this.acctPostpluginStr = acctPostpluginStr;
	}

	public String getAcctHandlerJson() {
		return acctHandlerJson;
	}

	public void setAcctHandlerJson(String acctHandlerJson) {
		this.acctHandlerJson = acctHandlerJson;
	}

	public List<ProfileLookupDriver> getAcctProfileLookupDriver() {
		return acctProfileLookupDriver;
	}

	public void setAcctProfileLookupDriver(List<ProfileLookupDriver> acctProfileLookupDriver) {
		this.acctProfileLookupDriver = acctProfileLookupDriver;
	}

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public boolean isImdgEnable() {
		return imdgEnable;
	}

	public void setImdgEnable(boolean imdgEnable) {
		this.imdgEnable = imdgEnable;
	}

	public List<CopyPacketTranslationConfData> getCopyPacketMappingConfDataList() {
		return copyPacketMappingConfDataList;
	}

	public void setCopyPacketMappingConfDataList(
			List<CopyPacketTranslationConfData> copyPacketMappingConfDataList) {
		this.copyPacketMappingConfDataList = copyPacketMappingConfDataList;
	}

	public List<ExternalSystemInterfaceInstanceData> getAcctESIList() {
		return acctESIList;
	}

	public void setAcctESIList(List<ExternalSystemInterfaceInstanceData> acctESIList) {
		this.acctESIList = acctESIList;
	}

	public List<String> getRadiusEsiGroupNames() {
		return radiusEsiGroupNames;
	}

	public void setRadiusEsiGroupNames(List<String> radiusEsiGroupNames) {
		this.radiusEsiGroupNames = radiusEsiGroupNames;
	}

	public String getAdvancedCuiExpression() {
		return advancedCuiExpression;
	}

	public void setAdvancedCuiExpression(String advancedCuiExpression) {
		this.advancedCuiExpression = advancedCuiExpression;
	}

	public List<PolicyPluginData> getAuthPrePluginData() {
		return authPrePluginData;
	}

	public void setAuthPrePluginData(List<PolicyPluginData> authPrePluginData) {
		this.authPrePluginData = authPrePluginData;
	}

	public List<PolicyPluginData> getAuthPostPluginData() {
		return authPostPluginData;
	}

	public void setAuthPostPluginData(List<PolicyPluginData> authPostPluginData) {
		this.authPostPluginData = authPostPluginData;
	}

	public List<PolicyPluginData> getAcctPrePluginData() {
		return acctPrePluginData;
	}

	public void setAcctPrePluginData(List<PolicyPluginData> acctPrePluginData) {
		this.acctPrePluginData = acctPrePluginData;
	}

	public List<PolicyPluginData> getAcctPostPluginData() {
		return acctPostPluginData;
	}

	public void setAcctPostPluginData(List<PolicyPluginData> acctPostPluginData) {
		this.acctPostPluginData = acctPostPluginData;
	}

	public List<ScriptInstanceData> getExternalScriptList() {
		return externalScriptList;
	}

	public void setExternalScriptList(List<ScriptInstanceData> externalScriptList) {
		this.externalScriptList = externalScriptList;
	}

	public List<ScriptInstanceData> getDriverScriptList() {
		return driverScriptList;
	}

	public void setDriverScriptList(List<ScriptInstanceData> driverScriptList) {
		this.driverScriptList = driverScriptList;
	}

	public List<ConcurrencyIMDGHandler> getConcurrencyIMDGHandlerList() {
		return concurrencyIMDGHandlerList;
	}

	public void setConcurrencyIMDGHandlerList(List<ConcurrencyIMDGHandler> concurrencyIMDGHandlerList) {
		this.concurrencyIMDGHandlerList = concurrencyIMDGHandlerList;
	}

	public List<String> getImdgFieldNames() {
		return imdgFieldNames;
	}

	public void setImdgFieldNames(List<String> imdgFieldNames) {
		this.imdgFieldNames = imdgFieldNames;
	}

	public List<StatefulProxySequentialHandler> getStatefulProxySequentialHandlerList() {
		return statefulProxySequentialHandlerList;
	}

	public void setStatefulProxySequentialHandlerList(List<StatefulProxySequentialHandler> statefulProxySequentialHandlerList) {
        this.statefulProxySequentialHandlerList = statefulProxySequentialHandlerList;
    }

	public String getStatefulProxySequentialHandlerAcctJson() {
		return statefulProxySequentialHandlerAcctJson;
	}

	public void setStatefulProxySequentialHandlerAcctJson(String statefulProxySequentialHandlerAcctJson) {
		this.statefulProxySequentialHandlerAcctJson = statefulProxySequentialHandlerAcctJson;
	}

	public List<StatefulProxySequentialHandler> getStatefulProxySequentialHandlerAcctList() {
		return statefulProxySequentialHandlerAcctList;
	}

	public void setStatefulProxySequentialHandlerAcctList(List<StatefulProxySequentialHandler> statefulProxySequentialHandlerAcctList) {
		this.statefulProxySequentialHandlerAcctList = statefulProxySequentialHandlerAcctList;
	}

	public List<StatefulProxyBroadcastHandler> getStatefulProxyBroadcastHandlersList() {
		return statefulProxyBroadcastHandlersList;
	}

	public void setStatefulProxyBroadcastHandlersList(List<StatefulProxyBroadcastHandler> statefulProxyBroadcastHandlersList) {
		this.statefulProxyBroadcastHandlersList = statefulProxyBroadcastHandlersList;
	}

    public String getStatefulProxyBroadcastHandlerAuthJson() {
        return statefulProxyBroadcastHandlerAuthJson;
    }

    public void setStatefulProxyBroadcastHandlerAuthJson(String statefulProxyBroadcastHandlerAuthJson) {
        this.statefulProxyBroadcastHandlerAuthJson = statefulProxyBroadcastHandlerAuthJson;
    }

    public String getStatefulProxyBroadcastHandlerAcctJson() {
        return statefulProxyBroadcastHandlerAcctJson;
    }

    public void setStatefulProxyBroadcastHandlerAcctJson(String statefulProxyBroadcastHandlerAcctJson) {
        this.statefulProxyBroadcastHandlerAcctJson = statefulProxyBroadcastHandlerAcctJson;
    }

    public List<StatefulProxyBroadcastHandler> getStatefulProxyBroadcastAcctHandlersList() {
        return statefulProxyBroadcastAcctHandlersList;
    }

    public void setStatefulProxyBroadcastAcctHandlersList(List<StatefulProxyBroadcastHandler> statefulProxyBroadcastAcctHandlersList) {
        this.statefulProxyBroadcastAcctHandlersList = statefulProxyBroadcastAcctHandlersList;
    }
}
