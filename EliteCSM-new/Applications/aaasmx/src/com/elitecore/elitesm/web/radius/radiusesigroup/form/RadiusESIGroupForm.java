package com.elitecore.elitesm.web.radius.radiusesigroup.form;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.ActivePassiveCommunicatorData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.CommunicatorData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusEsiGroupConfigurationData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class RadiusESIGroupForm extends BaseWebForm {
	/**
	 * @author Tejas shah
	 */
	private static final long serialVersionUID = 1L;
	private String esiGroupId;
	private String esiGroupName;
	private String description;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String action;
	private String auditUId;
	private List<RadiusESIGroupData> radiusESIGroupDataList;
	private List<ExternalSystemInterfaceInstanceData> externalSystemInterfaceInstanceData;
	private List<RadiusEsiGroupConfigurationData> deserializedRadiusEsiGroupDataList;

	private String esiType;
	private boolean stickySession;
	private boolean switchBack;
	private String redundancyMode;
	private List<String> authTypeEsiDataList = new ArrayList<>();
	private List<String> acctTypeEsiDataList = new ArrayList<>();
	private List<String> chargingGatewayEsiDataList = new ArrayList<>();
	private List<String> correlatedRadiusEsiDataList = new ArrayList<>();
	private String activePassiveEsiJson;

	List<CommunicatorData> primaryEsiValues = new ArrayList<>();
	List<CommunicatorData> secondaryEsiValues = new ArrayList<>();
	List<ActivePassiveCommunicatorData> activePassiveEsiList = new ArrayList<>();
	
	public String getEsiGroupId() {
		return esiGroupId;
	}
	public void setEsiGroupId(String esiGroupId) {
		this.esiGroupId = esiGroupId;
	}
	public String getEsiGroupName() {
		return esiGroupName;
	}
	public void setEsiGroupName(String esiGroupName) {
		this.esiGroupName = esiGroupName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
	
	public List<RadiusESIGroupData> getRadiusESIGroupDataList() {
		return radiusESIGroupDataList;
	}
	public void setRadiusESIGroupDataList(List<RadiusESIGroupData> radiusESIGroupDataList) {
		this.radiusESIGroupDataList = radiusESIGroupDataList;
	}
	public List<ExternalSystemInterfaceInstanceData> getExternalSystemInterfaceInstanceData() {
		return externalSystemInterfaceInstanceData;
	}
	public void setExternalSystemInterfaceInstanceData(List<ExternalSystemInterfaceInstanceData> externalSystemInterfaceInstanceData) {
		this.externalSystemInterfaceInstanceData = externalSystemInterfaceInstanceData;
	}

	public String getEsiType() {
		return esiType;
	}

	public void setEsiType(String esiType) {
		this.esiType = esiType;
	}

	public boolean isStickySession() {
		return stickySession;
	}

	public void setStickySession(boolean stickySession) {
		this.stickySession = stickySession;
	}

	public boolean isSwitchBack() {
		return switchBack;
	}

	public void setSwitchBack(boolean switchBack) {
		this.switchBack = switchBack;
	}

	public String getRedundancyMode() {
		return redundancyMode;
	}

	public void setRedundancyMode(String redundancyMode) {
		this.redundancyMode = redundancyMode;
	}

	public List<String> getAuthTypeEsiDataList() {
		return authTypeEsiDataList;
	}

	public void setAuthTypeEsiDataList(List<String> authTypeEsiDataList) {
		this.authTypeEsiDataList = authTypeEsiDataList;
	}

	public List<String> getAcctTypeEsiDataList() {
		return acctTypeEsiDataList;
	}

	public void setAcctTypeEsiDataList(List<String> acctTypeEsiDataList) {
		this.acctTypeEsiDataList = acctTypeEsiDataList;
	}

	public List<String> getChargingGatewayEsiDataList() {
		return chargingGatewayEsiDataList;
	}

	public void setChargingGatewayEsiDataList(List<String> chargingGatewayEsiDataList) {
		this.chargingGatewayEsiDataList = chargingGatewayEsiDataList;
	}

	public List<String> getCorrelatedRadiusEsiDataList() {
		return correlatedRadiusEsiDataList;
	}

	public void setCorrelatedRadiusEsiDataList(List<String> correlatedRadiusEsiDataList) {
		this.correlatedRadiusEsiDataList = correlatedRadiusEsiDataList;
	}

	public List<CommunicatorData> getPrimaryEsiValues() {
		return primaryEsiValues;
	}

	public void setPrimaryEsiValues(List<CommunicatorData> primaryEsiValues) {
		this.primaryEsiValues = primaryEsiValues;
	}

	public List<CommunicatorData> getSecondaryEsiValues() {
		return secondaryEsiValues;
	}

	public void setSecondaryEsiValues(List<CommunicatorData> secondaryEsiValues) {
		this.secondaryEsiValues = secondaryEsiValues;
	}

	public String getActivePassiveEsiJson() {
		return activePassiveEsiJson;
	}

	public void setActivePassiveEsiJson(String activePassiveEsiJson) {
		this.activePassiveEsiJson = activePassiveEsiJson;
	}

	public List<ActivePassiveCommunicatorData> getActivePassiveEsiList() {
		return activePassiveEsiList;
	}

	public void setActivePassiveEsiList(List<ActivePassiveCommunicatorData> activePassiveEsiList) {
		this.activePassiveEsiList = activePassiveEsiList;
	}

	public List<RadiusEsiGroupConfigurationData> getDeserializedRadiusEsiGroupDataList() {
		return deserializedRadiusEsiGroupDataList;
	}

	public void setDeserializedRadiusEsiGroupDataList(List<RadiusEsiGroupConfigurationData> deserializedRadiusEsiGroupDataList) {
		this.deserializedRadiusEsiGroupDataList = deserializedRadiusEsiGroupDataList;
	}
}
