package com.elitecore.elitesm.datamanager.servermgr.plugins.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.quotamgrplugin.data.QuotaMgtPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.TransactionLoggerData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.UniversalPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.usrstatpostauthplugin.data.UserStatPostAuthPluginData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PluginInstData extends BaseData implements IPluginInstData,Serializable,Differentiable{
	
	private static final long serialVersionUID = 1L;
	
	private String pluginInstanceId;	
	
	@Expose
	@SerializedName("Instance Name")
	private String name;
	
	@Expose
	@SerializedName("Description")
	private String description;
	
	private String status;
	private String createdByStaffId;
	private String lastModifiedByStaffId;
	private Timestamp lastModifiedDate;
	private Timestamp createDate;
	private String pluginTypeId;
	private PluginTypesData pluginTypesData;

	// for the purpose of displaying name when needed.....
	private String pluginTypeName;
	private String auditUId;
	
	private Set<UniversalPluginData> universalPluginDetails;
	private Set<GroovyPluginData> groovyPluginDataSet;
	private Set<TransactionLoggerData> transactionLoggerDataSet;
	private Set<QuotaMgtPluginData> quotaMgtPluginDataSet;
	private Set<UserStatPostAuthPluginData> userStatPostAuthPluginData;
	
	public PluginInstData() {
		description = RestUtitlity.getDefaultDescription();
	}
	
	public String getPluginInstanceId() {
		return pluginInstanceId;
	}
	public void setPluginInstanceId(String pluginInstanceId) {
		this.pluginInstanceId = pluginInstanceId;
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
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public String getPluginTypeId() {
		return pluginTypeId;
	}
	public void setPluginTypeId(String pluginTypeId) {
		this.pluginTypeId = pluginTypeId;
	}
	public PluginTypesData getPluginTypesData() {
		return pluginTypesData;
	}
	public void setPluginTypesData(PluginTypesData pluginTypesData) {
		this.pluginTypesData = pluginTypesData;
	}
	public String getPluginTypeName() {
		return pluginTypeName;
	}
	public void setPluginTypeName(String pluginTypeName) {
		this.pluginTypeName = pluginTypeName;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public Set<UniversalPluginData> getUniversalPluginDetails() {
		return universalPluginDetails;
	}
	public void setUniversalPluginDetails(Set<UniversalPluginData> universalPluginDetails) {
		this.universalPluginDetails = universalPluginDetails;
	}
	public Set<GroovyPluginData> getGroovyPluginDataSet() {
		return groovyPluginDataSet;
	}
	public void setGroovyPluginDataSet(Set<GroovyPluginData> groovyPluginDataSet) {
		this.groovyPluginDataSet = groovyPluginDataSet;
	}
	public Set<TransactionLoggerData> getTransactionLoggerDataSet() {
		return transactionLoggerDataSet;
	}
	public void setTransactionLoggerDataSet(Set<TransactionLoggerData> transactionLoggerDataSet) {
		this.transactionLoggerDataSet = transactionLoggerDataSet;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Plugin Name", name);
		object.put("Description", description);
		object.put("Status", status);
	
		if (universalPluginDetails != null && universalPluginDetails.size() > 0) {
			for (UniversalPluginData element : universalPluginDetails) {
				object.putAll(element.toJson());
			}
			return object;
		}

		if (groovyPluginDataSet != null && groovyPluginDataSet.size() > 0 ){
			for (GroovyPluginData element : groovyPluginDataSet) {
				object.putAll(element.toJson());
			}
			return object;
		}
		
		if( transactionLoggerDataSet != null && transactionLoggerDataSet.size() > 0 ){
			for(TransactionLoggerData element : transactionLoggerDataSet){
				object.putAll(element.toJson());
			}
		}
		
		return object;
	}
	public Set<QuotaMgtPluginData> getQuotaMgtPluginDataSet() {
		return quotaMgtPluginDataSet;
	}
	public void setQuotaMgtPluginDataSet(Set<QuotaMgtPluginData> quotaMgtPluginDataSet) {
		this.quotaMgtPluginDataSet = quotaMgtPluginDataSet;
	}
	public Set<UserStatPostAuthPluginData> getUserStatPostAuthPluginData() {
		return userStatPostAuthPluginData;
	}
	public void setUserStatPostAuthPluginData(Set<UserStatPostAuthPluginData> userStatPostAuthPluginData) {
		this.userStatPostAuthPluginData = userStatPostAuthPluginData;
	}
}