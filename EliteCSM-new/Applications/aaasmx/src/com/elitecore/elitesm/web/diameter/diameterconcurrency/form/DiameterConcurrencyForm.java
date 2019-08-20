package com.elitecore.elitesm.web.diameter.diameterconcurrency.form;

import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyFieldMapping;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

/**
 * author : nayana.rathod
 */

public class DiameterConcurrencyForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	
	private String diaConConfigId;
	private String name;
	private String description;
	private String databaseDsId;
	private String tableName = "TBLMDIAMETERSESSIONDATA";
	private String startTimeField = "START_TIME";
	private String lastUpdateTimeField = "LAST_UPDATED_TIME";
	private String concurrencyIdentityField = "CONCURRENCY_ID";
	private String dbFailureAction;
	private String sessionOverrideAction; 
	private String sessionOverrideFields;
	
	private String action;
	private String auditUId;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	
	private Collection diameterConcurrencyList;
	private List listDiameterConcurrencyGroup;
	
	private List lstDatasource;
	private String lstFieldMapping;
	private List<DiameterConcurrencyFieldMapping> mandatoryFieldMappingsList;
	private List<DiameterConcurrencyFieldMapping> additionalFieldMappingsList;
	
	public String getDiaConConfigId() {
		return diaConConfigId;
	}
	public void setDiaConConfigId(String diaConConfigId) {
		this.diaConConfigId = diaConConfigId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDatabaseDsId() {
		return databaseDsId;
	}
	public void setDatabaseDsId(String databaseDsId) {
		this.databaseDsId = databaseDsId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getStartTimeField() {
		return startTimeField;
	}
	public void setStartTimeField(String startTimeField) {
		this.startTimeField = startTimeField;
	}
	public String getLastUpdateTimeField() {
		return lastUpdateTimeField;
	}
	public void setLastUpdateTimeField(String lastUpdateTimeField) {
		this.lastUpdateTimeField = lastUpdateTimeField;
	}
	public String getDbFailureAction() {
		return dbFailureAction;
	}
	public void setDbFailureAction(String dbFailureAction) {
		this.dbFailureAction = dbFailureAction;
	}

	public String getSessionOverrideAction() {
		return sessionOverrideAction;
	}
	public void setSessionOverrideAction(String sessionOverrideAction) {
		this.sessionOverrideAction = sessionOverrideAction;
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
	public Collection getDiameterConcurrencyList() {
		return diameterConcurrencyList;
	}
	public void setDiameterConcurrencyList(Collection diameterConcurrencyList) {
		this.diameterConcurrencyList = diameterConcurrencyList;
	}
	public List getListDiameterConcurrencyGroup() {
		return listDiameterConcurrencyGroup;
	}
	public void setListDiameterConcurrencyGroup(List listDiameterConcurrencyGroup) {
		this.listDiameterConcurrencyGroup = listDiameterConcurrencyGroup;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List getLstDatasource() {
		return lstDatasource;
	}
	public void setLstDatasource(List lstDatasource) {
		this.lstDatasource = lstDatasource;
	}
	public String getSessionOverrideFields() {
		return sessionOverrideFields;
	}
	public void setSessionOverrideFields(String sessionOverrideFields) {
		this.sessionOverrideFields = sessionOverrideFields;
	}
	public String getLstFieldMapping() {
		return lstFieldMapping;
	}
	public void setLstFieldMapping(String lstFieldMapping) {
		this.lstFieldMapping = lstFieldMapping;
	}
	public List<DiameterConcurrencyFieldMapping> getMandatoryFieldMappingsList() {
		return mandatoryFieldMappingsList;
	}
	public void setMandatoryFieldMappingsList(
			List<DiameterConcurrencyFieldMapping> mandatoryFieldMappingsList) {
		this.mandatoryFieldMappingsList = mandatoryFieldMappingsList;
	}
	public List<DiameterConcurrencyFieldMapping> getAdditionalFieldMappingsList() {
		return additionalFieldMappingsList;
	}
	public void setAdditionalFieldMappingsList(
			List<DiameterConcurrencyFieldMapping> additionalFieldMappingsList) {
		this.additionalFieldMappingsList = additionalFieldMappingsList;
	}
	public String getConcurrencyIdentityField() {
		return concurrencyIdentityField;
	}
	public void setConcurrencyIdentityField(String concurrencyIdentityField) {
		this.concurrencyIdentityField = concurrencyIdentityField;
	}
}
