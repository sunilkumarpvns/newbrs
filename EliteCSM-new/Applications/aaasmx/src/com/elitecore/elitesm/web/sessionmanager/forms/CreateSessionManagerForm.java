package com.elitecore.elitesm.web.sessionmanager.forms;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateSessionManagerForm extends BaseWebForm {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long sminstanceid;
	private String name;
	private String description;
	private String status;
	private long createdbystaffid;
	private long lastmodifiedbystaffid;
	private String systemGenerated;
	private Timestamp lastmodifieddate;
	private Timestamp createdate;
	private String type;
	private String action;
	private boolean acceptOnTimeOut;	
	private Set smConfigInstanceSet;
	private List<IDatabaseDSData> lstDatasource = null;
	private long databaseId;
	private long translationMapConfigId;
	private String script;
	private List<TranslationMappingConfData> translationMappingConfDataList;
	private List<CopyPacketTranslationConfData> copyPacketMappingConfDataList;
	private Long copyPacketMapConfigId;
	private String configId;
	
	public boolean isAcceptOnTimeOut() {
		return acceptOnTimeOut;
	}
	public void setAcceptOnTimeOut(boolean acceptOnTimeOut) {
		this.acceptOnTimeOut = acceptOnTimeOut;
	}
	public List<IDatabaseDSData> getLstDatasource() {
		return lstDatasource;
	}
	public void setLstDatasource(List<IDatabaseDSData> lstDatasource) {
		this.lstDatasource = lstDatasource;
	}
	public long getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(long databaseId) {
		this.databaseId = databaseId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public long getSminstanceid() {
		return sminstanceid;
	}
	public void setSminstanceid(long sminstanceid) {
		this.sminstanceid = sminstanceid;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getCreatedbystaffid() {
		return createdbystaffid;
	}
	public void setCreatedbystaffid(long createdbystaffid) {
		this.createdbystaffid = createdbystaffid;
	}
	public long getLastmodifiedbystaffid() {
		return lastmodifiedbystaffid;
	}
	public void setLastmodifiedbystaffid(long lastmodifiedbystaffid) {
		this.lastmodifiedbystaffid = lastmodifiedbystaffid;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public Timestamp getLastmodifieddate() {
		return lastmodifieddate;
	}
	public void setLastmodifieddate(Timestamp lastmodifieddate) {
		this.lastmodifieddate = lastmodifieddate;
	}
	public Timestamp getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Timestamp createdate) {
		this.createdate = createdate;
	}
	public Set getSmConfigInstanceSet() {
		return smConfigInstanceSet;
	}
	public void setSmConfigInstanceSet(Set smConfigInstanceSet) {
		this.smConfigInstanceSet = smConfigInstanceSet;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
//	public String getUserIdentityAttributeId() {
//		return userIdentityAttributeId;
//	}
//	public void setUserIdentityAttributeId(String userIdentityAttributeId) {
//		this.userIdentityAttributeId = userIdentityAttributeId;
//	}
//	public String getDefaultUserIdentityValue() {
//		return defaultUserIdentityValue;
//	}
//	public void setDefaultUserIdentityValue(String defaultUserIdentityValue) {
//		this.defaultUserIdentityValue = defaultUserIdentityValue;
//	}
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
	public List<CopyPacketTranslationConfData> getCopyPacketMappingConfDataList() {
		return copyPacketMappingConfDataList;
	}
	public void setCopyPacketMappingConfDataList(
			List<CopyPacketTranslationConfData> copyPacketMappingConfDataList) {
		this.copyPacketMappingConfDataList = copyPacketMappingConfDataList;
	}
	public Long getCopyPacketMapConfigId() {
		return copyPacketMapConfigId;
	}
	public void setCopyPacketMapConfigId(Long copyPacketMapConfigId) {
		this.copyPacketMapConfigId = copyPacketMapConfigId;
	}
	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	
	
	

}
