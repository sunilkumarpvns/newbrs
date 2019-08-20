package com.elitecore.elitesm.web.script.form;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptTypeData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

/**
 * @author Tejas.P.Shah
 *
 */
public class ScriptInstanceForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String scriptId;
	private String scriptName;
	private String status;
	private String description;
	private String scriptType;
	private String selectedScript;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private String groovyDatas;
	private String action;
	private List<ScriptTypeData> scriptTypeList;
	private List<ScriptData> scriptDataList;
	private List<ScriptInstanceData> scriptInstanceDataList;
	private String auditUId;
	private String scriptDataId;
	private String scriptFileName;
	private String redirectUrlForViewFile;
	public String getScriptId() {
		return scriptId;
	}
	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}
	public String getScriptName() {
		return scriptName;
	}
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getScriptType() {
		return scriptType;
	}
	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
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
	
	public String getGroovyDatas() {
		return groovyDatas;
	}
	public void setGroovyDatas(String groovyDatas) {
		this.groovyDatas = groovyDatas;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getSelectedScript() {
		return selectedScript;
	}
	public void setSelectedScript(String selectedScript) {
		this.selectedScript = selectedScript;
	}
	public List<ScriptTypeData> getScriptTypeList() {
		return scriptTypeList;
	}
	public void setScriptTypeList(List<ScriptTypeData> scriptTypeList) {
		this.scriptTypeList = scriptTypeList;
	}
	public List<ScriptInstanceData> getScriptInstanceDataList() {
		return scriptInstanceDataList;
	}
	public void setScriptInstanceDataList(List<ScriptInstanceData> scriptInstanceDataList) {
		this.scriptInstanceDataList = scriptInstanceDataList;
	}
	public String getAuditUId() {
		return auditUId; 
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getScriptDataId() {
		return scriptDataId;
	}
	public void setScriptDataId(String scriptDataId) {
		this.scriptDataId = scriptDataId;
	}
	public List<ScriptData> getScriptDataList() {
		return scriptDataList;
	}
	public void setScriptDataList(List<ScriptData> scriptDataList) {
		this.scriptDataList = scriptDataList;
	}
	public String getScriptFileName() {
		return scriptFileName;
	}
	public void setScriptFileName(String scriptFileName) {
		this.scriptFileName = scriptFileName;
	}
	public String getRedirectUrlForViewFile() {
		return redirectUrlForViewFile;
	}
	public void setRedirectUrlForViewFile(String redirectUrlForViewFile) {
		this.redirectUrlForViewFile = redirectUrlForViewFile;
	}
}
