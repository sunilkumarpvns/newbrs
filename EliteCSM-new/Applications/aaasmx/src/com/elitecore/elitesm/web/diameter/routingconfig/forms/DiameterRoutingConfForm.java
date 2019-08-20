package com.elitecore.elitesm.web.diameter.routingconfig.forms;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class DiameterRoutingConfForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String routingConfigId;
	private String name;
	private String description;
	private String realmName;
	private String appIds;
	private String originHost;
	private String originRealm;
	private String ruleset;
	private Long routingAction;
	private Long orderNumber;
	private Timestamp createDate;
	private Long createdByStaffId;
	private Timestamp lastModifiedDate;
	private Long lastModifiedByStaffId;
	private List<TranslationMappingConfData> translationMappingConfDataList;
	private List<CopyPacketTranslationConfData> copyPacketMappingConfDataList;
	private String copyPacketMapConfigId;
	private String configId;
	private String translationMapConfigId;
	private String peerGroupRuleSet;
	private List<DiameterPeerData> diameterPeersList;
	private String peerId;
	private List<DiameterRoutingTableData> diameterRoutingTablesList;
	private String routingTableId;
	private Long protocolFailureAction;
	private Long transientFailureAction;
	private Long permanentFailureAction;
	private String protocolFailureArguments;
	private String transientFailureArguments;
	private String permanentFailureArguments;
	private Long timeOutAction;
	private String timeOutArguments;
	private Long transactionTimeout;
	private Long statefulRouting;
	private Boolean attachedRedirection=false;
	private Map<Long,String> failureActionMap;
	private String[] errorCode;
	private Short[] failureAction;
	private String[] failureArgument;
	private String defaultFailureArgument;
	private String defaultFailureAction;
	private String auditUId;
	private String imsiBasedRoutingTableId;
	private List<IMSIBasedRoutingTableData> imsiBasedRoutingTableDataList;
	private List<MSISDNBasedRoutingTableData> msisdnBasedRoutingTableDataList;
	private String msisdnBasedRoutingTableId;
	private String subscriberMode;
	private String subscriberRouting1;
	private String subscriberRouting2;
	
	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public List<DiameterPeerData> getDiameterPeersList() {
		return diameterPeersList;
	}

	public void setDiameterPeersList(List<DiameterPeerData> diameterPeersList) {
		this.diameterPeersList = diameterPeersList;
	}

	public String getPeerGroupRuleSet() {
		return peerGroupRuleSet;
	}

	public void setPeerGroupRuleSet(String peerGroupRuleSet) {
		this.peerGroupRuleSet = peerGroupRuleSet;
	}

	public String getTranslationMapConfigId() {
		return translationMapConfigId;
	}

	public void setTranslationMapConfigId(String translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}

	public List<TranslationMappingConfData> getTranslationMappingConfDataList() {
		return translationMappingConfDataList;
	}

	public void setTranslationMappingConfDataList(
			List<TranslationMappingConfData> translationMappingConfDataList) {
		this.translationMappingConfDataList = translationMappingConfDataList;
	}

	public String getRoutingConfigId() {
		return routingConfigId;
	}
	
	public void setRoutingConfigId(String routingConfigId) {
		this.routingConfigId = routingConfigId;
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
	
	public String getRealmName() {
		return realmName;
	}
	
	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}
	
	public String getAppIds() {
		return appIds;
	}
	
	public void setAppIds(String appIds) {
		this.appIds = appIds;
	}
	
	public String getOriginHost() {
		return originHost;
	}
	
	public void setOriginHost(String originHost) {
		this.originHost = originHost;
	}
	
	public String getOriginRealm() {
		return originRealm;
	}
	
	public void setOriginRealm(String originRealm) {
		this.originRealm = originRealm;
	}
	
	public String getRuleset() {
		return ruleset;
	}
	
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	
	public Long getRoutingAction() {
		return routingAction;
	}
	
	public void setRoutingAction(Long routingAction) {
		this.routingAction = routingAction;
	}
	
	public Long getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public Timestamp getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	
	public Long getCreatedByStaffId() {
		return createdByStaffId;
	}
	
	public void setCreatedByStaffId(Long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	public Long getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	
	public void setLastModifiedByStaffId(Long lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}

	public List<DiameterRoutingTableData> getDiameterRoutingTablesList() {
		return diameterRoutingTablesList;
	}

	public void setDiameterRoutingTablesList(
			List<DiameterRoutingTableData> diameterRoutingTablesList) {
		this.diameterRoutingTablesList = diameterRoutingTablesList;
	}

	public String getRoutingTableId() {
		return routingTableId;
	}

	public void setRoutingTableId(String routingTableId) {
		this.routingTableId = routingTableId;
	}

	public Long getProtocolFailureAction() {
		return protocolFailureAction;
	}

	public void setProtocolFailureAction(Long protocolFailureAction) {
		this.protocolFailureAction = protocolFailureAction;
	}

	public Long getTransientFailureAction() {
		return transientFailureAction;
	}

	public void setTransientFailureAction(Long transientFailureAction) {
		this.transientFailureAction = transientFailureAction;
	}

	public Long getPermanentFailureAction() {
		return permanentFailureAction;
	}

	public void setPermanentFailureAction(Long permanentFailureAction) {
		this.permanentFailureAction = permanentFailureAction;
	}

	public String getProtocolFailureArguments() {
		return protocolFailureArguments;
	}

	public void setProtocolFailureArguments(String protocolFailureArguments) {
		this.protocolFailureArguments = protocolFailureArguments;
	}

	public String getTransientFailureArguments() {
		return transientFailureArguments;
	}

	public void setTransientFailureArguments(String transientFailureArguments) {
		this.transientFailureArguments = transientFailureArguments;
	}

	public String getPermanentFailureArguments() {
		return permanentFailureArguments;
	}

	public void setPermanentFailureArguments(String permanentFailureArguments) {
		this.permanentFailureArguments = permanentFailureArguments;
	}

	public Long getTimeOutAction() {
		return timeOutAction;
	}

	public void setTimeOutAction(Long timeOutAction) {
		this.timeOutAction = timeOutAction;
	}

	public String getTimeOutArguments() {
		return timeOutArguments;
	}

	public void setTimeOutArguments(String timeOutArguments) {
		this.timeOutArguments = timeOutArguments;
	}

	public Long getTransactionTimeout() {
		return transactionTimeout;
	}

	public void setTransactionTimeout(Long transactionTimeout) {
		this.transactionTimeout = transactionTimeout;
	}

	public Long getStatefulRouting() {
		return statefulRouting;
	}

	public void setStatefulRouting(Long statefulRouting) {
		this.statefulRouting = statefulRouting;
	}

	public Map<Long, String> getFailureActionMap() {
		return failureActionMap;
	}

	public void setFailureActionMap(Map<Long, String> failureActionMap) {
		this.failureActionMap = failureActionMap;
	}
	
	
	public String[] getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String[] errorCode) {
		this.errorCode = errorCode;
	}

	public Short[] getFailureAction() {
		return failureAction;
	}

	public void setFailureAction(Short[] failureAction) {
		this.failureAction = failureAction;
	}

	public String[] getFailureArgument() {
		return failureArgument;
	}

	public void setFailureArgument(String[] failureArgument) {
		this.failureArgument = failureArgument;
	}

	
	public String getDefaultFailureArgument() {
		return defaultFailureArgument;
	}

	public void setDefaultFailureArgument(String defaultFailureArgument) {
		this.defaultFailureArgument = defaultFailureArgument;
	}

	
	public String getDefaultFailureAction() {
		return defaultFailureAction;
	}

	public void setDefaultFailureAction(String defaultFailureAction) {
		this.defaultFailureAction = defaultFailureAction;
	}
	

	public void setDefaultFailureActionMap() {
		Map<Long, String> failureActionMap  = new LinkedHashMap<Long, String>();
		for(DiameterFailureConstants diameterFailureConstant : DiameterFailureConstants.VALUES){
			failureActionMap.put((long) diameterFailureConstant.failureAction, diameterFailureConstant.failureActionStr);
		}
		this.failureActionMap = failureActionMap;
	}

	public Boolean getAttachedRedirection() {
		return attachedRedirection;
	}

	public void setAttachedRedirection(Boolean attachedRedirection) {
		this.attachedRedirection = attachedRedirection;
	}

	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	public List<CopyPacketTranslationConfData> getCopyPacketMappingConfDataList() {
		return copyPacketMappingConfDataList;
	}
	
	public void setCopyPacketMappingConfDataList(
			List<CopyPacketTranslationConfData> copyPacketMappingConfDataList) {
		this.copyPacketMappingConfDataList = copyPacketMappingConfDataList;
	}
	
	public String getCopyPacketMapConfigId() {
		return copyPacketMapConfigId;
	}
	
	public void setCopyPacketMapConfigId(String copyPacketMapConfigId) {
		this.copyPacketMapConfigId = copyPacketMapConfigId;
	}
	
	public String getConfigId() {
		return configId;
	}
	
	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getImsiBasedRoutingTableId() {
		return imsiBasedRoutingTableId;
	}

	public void setImsiBasedRoutingTableId(String imsiBasedRoutingTableId) {
		this.imsiBasedRoutingTableId = imsiBasedRoutingTableId;
	}

	public List<IMSIBasedRoutingTableData> getImsiBasedRoutingTableDataList() {
		return imsiBasedRoutingTableDataList;
	}

	public void setImsiBasedRoutingTableDataList(
			List<IMSIBasedRoutingTableData> imsiBasedRoutingTableDataList) {
		this.imsiBasedRoutingTableDataList = imsiBasedRoutingTableDataList;
	}

	public List<MSISDNBasedRoutingTableData> getMsisdnBasedRoutingTableDataList() {
		return msisdnBasedRoutingTableDataList;
	}

	public void setMsisdnBasedRoutingTableDataList(
			List<MSISDNBasedRoutingTableData> msisdnBasedRoutingTableDataList) {
		this.msisdnBasedRoutingTableDataList = msisdnBasedRoutingTableDataList;
	}

	public String getMsisdnBasedRoutingTableId() {
		return msisdnBasedRoutingTableId;
	}

	public void setMsisdnBasedRoutingTableId(String msisdnBasedRoutingTableId) {
		this.msisdnBasedRoutingTableId = msisdnBasedRoutingTableId;
	}

	public String getSubscriberMode() {
		return subscriberMode;
	}

	public void setSubscriberMode(String subscriberMode) {
		this.subscriberMode = subscriberMode;
	}

	public String getSubscriberRouting1() {
		return subscriberRouting1;
	}

	public void setSubscriberRouting1(String subscriberRouting1) {
		this.subscriberRouting1 = subscriberRouting1;
	}

	public String getSubscriberRouting2() {
		return subscriberRouting2;
	}

	public void setSubscriberRouting2(String subscriberRouting2) {
		this.subscriberRouting2 = subscriberRouting2;
	}
}