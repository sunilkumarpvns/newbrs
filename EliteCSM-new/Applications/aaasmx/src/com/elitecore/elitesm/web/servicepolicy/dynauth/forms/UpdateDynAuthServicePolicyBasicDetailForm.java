package com.elitecore.elitesm.web.servicepolicy.dynauth.forms;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.web.servicepolicy.acct.forms.ExternalSystemBean;
import com.elitecore.elitesm.web.servicepolicy.dynauth.NasClients;

public class UpdateDynAuthServicePolicyBasicDetailForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;

	private String dynAuthPolicyId;
	private String name;
	private String description;
	private String ruleSet;
	private String responseAttributes;
	private Long eventTimestamp;
	private String status;
	private Integer eligibleSessions;
	private String validatePacket;
	private String tableName;
	private String databaseDatasourceId;
	private String action;
	private List<IDatabaseDSData> databaseDatasourceList;
	private String attributeids;
	private String dbfield;
	private String defaultvalue;
	private String mandatory;
	private List<ExternalSystemBean> nasClientList;
	private String[] nasClients;
	private long translationMapConfigId;
	private long copyPacketMapConfigId;
	private String script;
	private List<TranslationMappingConfData> translationMappingConfDataList;
	private List<CopyPacketTranslationConfData> copyPacketMappingConfDataList;
	private String auditUId;
	private String nasClientsJson;
	private List<NasClients> nasClientsList;
	private String dbFailureAction;
	private List<ScriptInstanceData> externalScriptList;
	
	public String getAttributeids() {
		return attributeids;
	}

	public void setAttributeids(String attributeids) {
		this.attributeids = attributeids;
	}

	public String getDbfield() {
		return dbfield;
	}

	public void setDbfield(String dbfield) {
		this.dbfield = dbfield;
	}

	public String getDefaultvalue() {
		return defaultvalue;
	}

	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public String getDynAuthPolicyId() {
		return dynAuthPolicyId;
	}

	public void setDynAuthPolicyId(String dynAuthPolicyId) {
		this.dynAuthPolicyId = dynAuthPolicyId;
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

	public String getRuleSet() {
		return ruleSet;
	}

	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}

	public String getResponseAttributes() {
		return responseAttributes;
	}

	public void setResponseAttributes(String responseAttributes) {
		this.responseAttributes = responseAttributes;
	}

	public Long getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(Long eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	public Integer getEligibleSessions() {
		return eligibleSessions;
	}

	public void setEligibleSessions(Integer eligibleSessions) {
		this.eligibleSessions = eligibleSessions;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDatabaseDatasourceId() {
		return databaseDatasourceId;
	}

	public void setDatabaseDatasourceId(String databaseDatasourceId) {
		this.databaseDatasourceId = databaseDatasourceId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getValidatePacket() {
		return validatePacket;
	}

	public void setValidatePacket(String validatePacket) {
		this.validatePacket = validatePacket;
	}

	public List<IDatabaseDSData> getDatabaseDatasourceList() {
		return databaseDatasourceList;
	}

	public void setDatabaseDatasourceList(
			List<IDatabaseDSData> databaseDatasourceList) {
		this.databaseDatasourceList = databaseDatasourceList;
	}
	
	

	public List<ExternalSystemBean> getNasClientList() {
		return nasClientList;
	}

	public void setNasClientList(List<ExternalSystemBean> nasClientList) {
		this.nasClientList = nasClientList;
	}
	

	public String[] getNasClients() {
		return nasClients;
	}

	public void setNasClients(String[] nasClients) {
		this.nasClients = nasClients;
	}

	public long getTranslationMapConfigId() {
		return translationMapConfigId;
	}

	public void setTranslationMapConfigId(long translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public List<TranslationMappingConfData> getTranslationMappingConfDataList() {
		return translationMappingConfDataList;
	}

	public void setTranslationMappingConfDataList(
			List<TranslationMappingConfData> translationMappingConfDataList) {
		this.translationMappingConfDataList = translationMappingConfDataList;
	}

	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println("--------------------- UpdateDynAuthServicePolicyBasicDetailForm ---------------------------");
		writer.println( "dynAuthPolicyId     : "+dynAuthPolicyId);
		writer.println( "name                : "+name);
		writer.println( "description         : "+description);
		writer.println( "ruleSet             : "+ruleSet);
		writer.println( "responseAttributes  : "+responseAttributes);
		writer.println( "eventTimestamp      : "+eventTimestamp);
		writer.println( "validatePacket      : "+validatePacket);
		writer.println( "eligibleSessions    : "+eligibleSessions);
		writer.println( "tableName           : "+tableName);
		writer.println( "databaseDatasourceId: "+databaseDatasourceId);
		writer.println( "status				 : "+status);
		writer.println( "nasClientList		 : "+nasClientList);
		writer.println( "nasClients		     : "+nasClients);
		writer.println("-----------------------------------------------------------------------------");
		writer.close();
		return out.toString();
	}

	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	public String getNasClientsJson() {
		return nasClientsJson;
	}

	public void setNasClientsJson(String nasClientsJson) {
		this.nasClientsJson = nasClientsJson;
	}

	public List<NasClients> getNasClientsList() {
		return nasClientsList;
	}

	public void setNasClientsList(List<NasClients> nasClientsList) {
		this.nasClientsList = nasClientsList;
	}

	public long getCopyPacketMapConfigId() {
		return copyPacketMapConfigId;
	}

	public void setCopyPacketMapConfigId(long copyPacketMapConfigId) {
		this.copyPacketMapConfigId = copyPacketMapConfigId;
	}

	public List<CopyPacketTranslationConfData> getCopyPacketMappingConfDataList() {
		return copyPacketMappingConfDataList;
	}

	public void setCopyPacketMappingConfDataList(
			List<CopyPacketTranslationConfData> copyPacketMappingConfDataList) {
		this.copyPacketMappingConfDataList = copyPacketMappingConfDataList;
	}

	public String getDbFailureAction() {
		return dbFailureAction;
	}

	public void setDbFailureAction(String dbFailureAction) {
		this.dbFailureAction = dbFailureAction;
	}

	public List<ScriptInstanceData> getExternalScriptList() {
		return externalScriptList;
	}

	public void setExternalScriptList(List<ScriptInstanceData> externalScriptList) {
		this.externalScriptList = externalScriptList;
	}
	
	
}
