package com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form;

import java.util.List;

import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

/**
 * @author nayana.rathod
 *
 */
public class TGPPAAAPolicyForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	
	private String tgppAAAPolicyId;
	private String name;
	private String description;
	private String ruleset;
	private String userIdentity;
	private Long diaConConfigId;
	private String sessionManagement;
	private String cui;
	private String commandCodeWiseRespAttrib;
	private String status;
	private String action;
	private String auditUid;

	/* Searching Parameter */
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private List policyList;
	
	/* JSON Parameters */
	private String tgppCommandCodeJSON;
	private byte[] tgppAAAPolicyXml;
	private String defaultResponseBehaviourArgument;	
	private String defaultResponseBehaviour;
	

	/* List */
	private List<DiameterConcurrencyData> diaconcurrencyInstanceDataList;
	private List<AuthMethodTypeData> authMethodTypeDataList;
	private List<Long> authMethods;
	private String[] selectedAuthMethodTypes;
	private List<EAPConfigData> eapConfigurationList;
	private List<GracepolicyData> gracePolicyList;
	private List<DriverInstanceData> driversList;
	private List<DriverInstanceData> cacheableDriverList;
	private List<TranslationMappingConfData> diaToradiusTranslationMappingConfDataList;
	private List<TranslationMappingConfData> diaTodiaTranslationMappingConfDataList;
	private List<CopyPacketTranslationConfData>  diaToradiusCopyPacketMappingConfDataList;
	private List<CopyPacketTranslationConfData>  diaTodiaCopyPacketMappingConfDataList;
	private List<DiameterPeerGroup> diameterPeerGroupDataList;
	private List<ExternalSystemInterfaceInstanceData> externalSystemInterfaceInstanceDataList; 
	private List<RadiusESIGroupData> radiusESIGroupDataList;
	private List<PluginInstData> pluginInstDataList;
	
	private List<ScriptInstanceData> driverScriptList;
	private List<ScriptInstanceData> externalScriptList;

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

	public String getRuleset() {
		return ruleset;
	}

	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

		
	public String getsessionManagement() {
		return sessionManagement;
	}

	public void setsessionManagement(String sessionManagement) {
		this.sessionManagement = sessionManagement;
	}
	
	
	public List<DiameterConcurrencyData> getDiaconcurrencyInstanceDataList() {
		return diaconcurrencyInstanceDataList;
	}

	public void setDiaconcurrencyInstanceDataList(
			List<DiameterConcurrencyData> diaconcurrencyInstanceDataList) {
		this.diaconcurrencyInstanceDataList = diaconcurrencyInstanceDataList;
	}
	
	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}

	public String getCommandCodeWiseRespAttrib() {
		return commandCodeWiseRespAttrib;
	}

	public void setCommandCodeWiseRespAttrib(String commandCodeWiseRespAttrib) {
		this.commandCodeWiseRespAttrib = commandCodeWiseRespAttrib;
	}

	
	public Long getDiaConConfigId() {
		return diaConConfigId;
	}

	public void setDiaConConfigId(Long diaConConfigId) {
		this.diaConConfigId = diaConConfigId;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTgppAAAPolicyId() {
		return tgppAAAPolicyId;
	}

	public void setTgppAAAPolicyId(String tgppAAAPolicyId) {
		this.tgppAAAPolicyId = tgppAAAPolicyId;
	}

	public List<AuthMethodTypeData> getAuthMethodTypeDataList() {
		return authMethodTypeDataList;
	}

	public void setAuthMethodTypeDataList(List<AuthMethodTypeData> authMethodTypeDataList) {
		this.authMethodTypeDataList = authMethodTypeDataList;
	}

	public List<Long> getAuthMethods() {
		return authMethods;
	}

	public void setAuthMethods(List<Long> authMethods) {
		this.authMethods = authMethods;
	}

	public String[] getSelectedAuthMethodTypes() {
		return selectedAuthMethodTypes;
	}

	public void setSelectedAuthMethodTypes(String[] selectedAuthMethodTypes) {
		this.selectedAuthMethodTypes = selectedAuthMethodTypes;
	}

	public List<EAPConfigData> getEapConfigurationList() {
		return eapConfigurationList;
	}

	public void setEapConfigurationList(List<EAPConfigData> eapConfigurationList) {
		this.eapConfigurationList = eapConfigurationList;
	}

	public List<GracepolicyData> getGracePolicyList() {
		return gracePolicyList;
	}

	public void setGracePolicyList(List<GracepolicyData> gracePolicyList) {
		this.gracePolicyList = gracePolicyList;
	}

	public List<DriverInstanceData> getDriversList() {
		return driversList;
	}

	public void setDriversList(List<DriverInstanceData> driversList) {
		this.driversList = driversList;
	}

	public List<DriverInstanceData> getCacheableDriverList() {
		return cacheableDriverList;
	}

	public void setCacheableDriverList(List<DriverInstanceData> cacheableDriverList) {
		this.cacheableDriverList = cacheableDriverList;
	}

	public List<DiameterPeerGroup> getDiameterPeerGroupDataList() {
		return diameterPeerGroupDataList;
	}

	public void setDiameterPeerGroupDataList(
			List<DiameterPeerGroup> diameterPeerGroupDataList) {
		this.diameterPeerGroupDataList = diameterPeerGroupDataList;
	}

	public List<PluginInstData> getPluginInstDataList() {
		return pluginInstDataList;
	}

	public void setPluginInstDataList(List<PluginInstData> pluginInstDataList) {
		this.pluginInstDataList = pluginInstDataList;
	}

	public String getTgppCommandCodeJSON() {
		return tgppCommandCodeJSON;
	}

	public void setTgppCommandCodeJSON(String tgppCommandCodeJSON) {
		this.tgppCommandCodeJSON = tgppCommandCodeJSON;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public long getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}

	public long getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public List getPolicyList() {
		return policyList;
	}

	public void setPolicyList(List policyList) {
		this.policyList = policyList;
	}

	public String getAuditUid() {
		return auditUid;
	}

	public void setAuditUid(String auditUid) {
		this.auditUid = auditUid;
	}

	public byte[] getTgppAAAPolicyXml() {
		return tgppAAAPolicyXml;
	}

	public void setTgppAAAPolicyXml(byte[] tgppAAAPolicyXml) {
		this.tgppAAAPolicyXml = tgppAAAPolicyXml;
	}

	public List<TranslationMappingConfData> getDiaToradiusTranslationMappingConfDataList() {
		return diaToradiusTranslationMappingConfDataList;
	}

	public void setDiaToradiusTranslationMappingConfDataList(
			List<TranslationMappingConfData> diaToradiusTranslationMappingConfDataList) {
		this.diaToradiusTranslationMappingConfDataList = diaToradiusTranslationMappingConfDataList;
	}

	public List<TranslationMappingConfData> getDiaTodiaTranslationMappingConfDataList() {
		return diaTodiaTranslationMappingConfDataList;
	}

	public void setDiaTodiaTranslationMappingConfDataList(
			List<TranslationMappingConfData> diaTodiaTranslationMappingConfDataList) {
		this.diaTodiaTranslationMappingConfDataList = diaTodiaTranslationMappingConfDataList;
	}

	public List<CopyPacketTranslationConfData> getDiaToradiusCopyPacketMappingConfDataList() {
		return diaToradiusCopyPacketMappingConfDataList;
	}

	public void setDiaToradiusCopyPacketMappingConfDataList(
			List<CopyPacketTranslationConfData> diaToradiusCopyPacketMappingConfDataList) {
		this.diaToradiusCopyPacketMappingConfDataList = diaToradiusCopyPacketMappingConfDataList;
	}

	public List<CopyPacketTranslationConfData> getDiaTodiaCopyPacketMappingConfDataList() {
		return diaTodiaCopyPacketMappingConfDataList;
	}

	public void setDiaTodiaCopyPacketMappingConfDataList(
			List<CopyPacketTranslationConfData> diaTodiaCopyPacketMappingConfDataList) {
		this.diaTodiaCopyPacketMappingConfDataList = diaTodiaCopyPacketMappingConfDataList;
	}
	
	public List<ExternalSystemInterfaceInstanceData> getExternalSystemInterfaceInstanceDataList() {
		return externalSystemInterfaceInstanceDataList;
	}

	public void setExternalSystemInterfaceInstanceDataList(
			List<ExternalSystemInterfaceInstanceData> externalSystemInterfaceInstanceDataList) {
		this.externalSystemInterfaceInstanceDataList = externalSystemInterfaceInstanceDataList;
	}
	public String getDefaultResponseBehaviourArgument() {
		return defaultResponseBehaviourArgument;
	}

	public void setDefaultResponseBehaviourArgument(String defaultResponseBehaviourArgument) {
		this.defaultResponseBehaviourArgument = defaultResponseBehaviourArgument;
	}

	public String getDefaultResponseBehaviour() {
		return defaultResponseBehaviour;
	}

	public void setDefaultResponseBehaviour(String defaultResponseBehaviour) {
		this.defaultResponseBehaviour = defaultResponseBehaviour;
	}

	public List<RadiusESIGroupData> getRadiusESIGroupDataList() {
		return radiusESIGroupDataList;
	}

	public void setRadiusESIGroupDataList(List<RadiusESIGroupData> radiusESIGroupDataList) {
		this.radiusESIGroupDataList = radiusESIGroupDataList;
	}

	public List<ScriptInstanceData> getDriverScriptList() {
		return driverScriptList;
	}

	public void setDriverScriptList(List<ScriptInstanceData> driverScriptList) {
		this.driverScriptList = driverScriptList;
	}

	public List<ScriptInstanceData> getExternalScriptList() {
		return externalScriptList;
	}

	public void setExternalScriptList(List<ScriptInstanceData> externalScriptList) {
		this.externalScriptList = externalScriptList;
	}
	
}
