package com.elitecore.netvertexsm.web.servermgr.spr.form;

import java.util.List;

import com.elitecore.corenetvertex.sm.acl.GroupInfo;
import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData;

public class SPRForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sprId;
	private String sprName;
	private String description;
	private Long databaseDSId;
	private DatabaseDSData databaseDsData;
	private Long spInterfaceId;
	private DriverInstanceData spInterfaceData;
	private String action;
	private List<SPRData> sprDataList;
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;	
	private List<DriverInstanceData> spInterfaceList;
	private List<DatabaseDSData> databaseList;
	private long batchSize;
	private String groups;
	private List<String> groupNameList;
	private List<String> accessGroups;
	private String alternateIdField;
	private String selectedGroups;
	private List<GroupInfo> groupInfoList;

	public String getSprId() {
		return sprId;
	}
	public void setSprId(String sprId) {
		this.sprId = sprId;
	}
	public String getSprName() {
		return sprName;
	}
	public void setSprName(String sprName) {
		this.sprName = sprName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getDatabaseDSId() {
		return databaseDSId;
	}
	public void setDatabaseDSId(Long databaseDSId) {
		this.databaseDSId = databaseDSId;
	}
	public DatabaseDSData getDatabaseDsData() {
		return databaseDsData;
	}
	public void setDatabaseDsData(DatabaseDSData databaseDsData) {
		this.databaseDsData = databaseDsData;
	}
	public Long getSpInterfaceId() {
		return spInterfaceId;
	}
	public void setSpInterfaceId(Long spInterfaceId) {
		this.spInterfaceId = spInterfaceId;
	}
	public DriverInstanceData getSPInterfaceData() {
		return spInterfaceData;
	}
	public void setSPInterfaceData(DriverInstanceData driverInstanceData) {
		this.spInterfaceData = driverInstanceData;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<SPRData> getSprDataList() {
		return sprDataList;
	}
	public void setSprDataList(List<SPRData> sprDataList) {
		this.sprDataList = sprDataList;
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
	public List<DriverInstanceData> getSpInterfaceList() {
		return spInterfaceList;
	}
	public void setSpInterfaceList(List<DriverInstanceData> spInterfaceList) {
		this.spInterfaceList = spInterfaceList;
	}
	public List<DatabaseDSData> getDatabaseList() {
		return databaseList;
	}
	public void setDatabaseList(List<DatabaseDSData> databaseList) {
		this.databaseList = databaseList;
	}
	public DriverInstanceData getSpInterfaceData() {
	    return spInterfaceData;
	}
	public void setSpInterfaceData(DriverInstanceData spInterfaceData) {
	    this.spInterfaceData = spInterfaceData;
	}
	public long getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(long batchSize) {
		this.batchSize = batchSize;
	}
	public String getGroups() {
	    return groups;
	}
	public void setGroups(String groupNames) {
	    this.groups = groupNames;
	}
	public List<String> getGroupNameList() {
		return groupNameList;
	}
	public void setGroupNameList(List<String> groupNameList) {
		this.groupNameList = groupNameList;
	}
	public List<String> getAccessGroups() {
		return accessGroups;
	}
	public void setAccessGroups(List<String> accessGroups) {
		this.accessGroups = accessGroups;
	}
	public String getAlternateIdField() {
		return alternateIdField;
	}
	public void setAlternateIdField(String alternateIdField) {
		this.alternateIdField = alternateIdField;
	}
	public String getSelectedGroups() {
		return selectedGroups;
	}

	public void setSelectedGroups(String selectedGroups) {
		this.selectedGroups = selectedGroups;
	}

	public List<GroupInfo> getGroupInfoList() {
		return groupInfoList;
	}

	public void setGroupInfoList(List<GroupInfo> groupInfoList) {
		this.groupInfoList = groupInfoList;
	}
}
