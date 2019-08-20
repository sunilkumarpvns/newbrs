package com.elitecore.elitesm.web.plugins.forms;

import java.util.Map;
import java.util.Set;

import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginFile;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.web.plugins.GroovyDetails;

/**
 * @author nayana.rathod
 *
 */
public class UpdateRadiusGroovyPluginForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	
	/* Plugin Instance related Properties */
	private String pluginName;
	private String description;
	
	private String pluginId;
	private String pluginType;
	private String pluginInstanceId;
	private byte[] pluginData;
	private String action;
	private String auditUId;
	private Set<GroovyPluginFile> groovyPluginFileSet;
	private Map<String,GroovyDetails>  groovyDetailsMap;
	private String groovyDatas;
	private String status;
	
	/* Internal Purpose */
	private String groovyFileName;
	private String redirectUrlForViewFile;
	
	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public String getPluginType() {
		return pluginType;
	}

	public void setPluginType(String pluginType) {
		this.pluginType = pluginType;
	}

	public String getPluginInstanceId() {
		return pluginInstanceId;
	}

	public void setPluginInstanceId(String pluginInstanceId) {
		this.pluginInstanceId = pluginInstanceId;
	}

	public byte[] getPluginData() {
		return pluginData;
	}

	public void setPluginData(byte[] pluginData) {
		this.pluginData = pluginData;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	public Set<GroovyPluginFile> getGroovyPluginFileSet() {
		return groovyPluginFileSet;
	}

	public void setGroovyPluginFileSet(Set<GroovyPluginFile> groovyPluginFileSet) {
		this.groovyPluginFileSet = groovyPluginFileSet;
	}

	public Map<String,GroovyDetails> getGroovyDetailsMap() {
		return groovyDetailsMap;
	}

	public void setGroovyDetailsMap(Map<String,GroovyDetails> groovyDetailsMap) {
		this.groovyDetailsMap = groovyDetailsMap;
	}
	public String getGroovyDatas() {
		return groovyDatas;
	}

	public void setGroovyDatas(String groovyDatas) {
		this.groovyDatas = groovyDatas;
	}

	public String getGroovyFileName() {
		return groovyFileName;
	}

	public void setGroovyFileName(String groovyFileName) {
		this.groovyFileName = groovyFileName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRedirectUrlForViewFile() {
		return redirectUrlForViewFile;
	}

	public void setRedirectUrlForViewFile(String redirectUrlForViewFile) {
		this.redirectUrlForViewFile = redirectUrlForViewFile;
	}
	
}
