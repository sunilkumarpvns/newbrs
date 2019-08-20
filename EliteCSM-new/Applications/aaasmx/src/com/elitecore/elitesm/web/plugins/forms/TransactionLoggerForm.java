package com.elitecore.elitesm.web.plugins.forms;

import java.util.Set;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.FormatMappingData;
/**
 * @author Tejas Shah
 *
 */
public class TransactionLoggerForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String pluginId;
	private String pluginType;
	private String pluginInstanceId;
	private byte[] pluginData;
	private String action;
	private String pluginName;
	private String description;
	private Long timeBoundry=30L;
	private String logFile;
	private String formatMappingsJson;
	private String status;
	private String range;
	private String pattern="suffix";
	private String globalization="false";
	private String auditUId;
	private Set<FormatMappingData> formatMappingDataSet;
	private String groovyFileName;
	
	
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
	public String getLogFile() {
		return logFile;
	}
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}
	public String getFormatMappingsJson() {
		return formatMappingsJson;
	}
	public void setFormatMappingsJson(String formatMappingsJson) {
		this.formatMappingsJson = formatMappingsJson;
	}
	public Long getTimeBoundry() {
		return timeBoundry;
	}
	public void setTimeBoundry(Long timeBoundry) {
		this.timeBoundry = timeBoundry;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getGlobalization() {
		return globalization;
	}
	public void setGlobalization(String globalization) {
		this.globalization = globalization;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public Set<FormatMappingData> getFormatMappingDataSet() {
		return formatMappingDataSet;
	}

	public void setFormatMappingDataSet(Set<FormatMappingData> formatMappingDataSet) {
		this.formatMappingDataSet = formatMappingDataSet;
	}
	public String getGroovyFileName() {
		return groovyFileName;
	}
	public void setGroovyFileName(String groovyFileName) {
		this.groovyFileName = groovyFileName;
	}
}
