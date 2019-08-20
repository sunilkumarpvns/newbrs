package com.elitecore.elitesm.web.servermgr.copypacket.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateCopyPacketMappingConfigForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String script;
	private String selectedFromType;
	private String selectedToType;
	private String action;
	private String auditId;
	private String copyPacketTransConfId;
	
	private List<TranslatorTypeData> fromTypeTranslatorList;
	private List<TranslatorTypeData> toTypeTranslatorList;
	
	private List<ScriptInstanceData> externalScriptList;
	
	public String getCopyPacketTransConfId() {
		return copyPacketTransConfId;
	}
	public void setCopyPacketTransConfId(String copyPacketTransConfId) {
		this.copyPacketTransConfId = copyPacketTransConfId;
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
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getSelectedFromType() {
		return selectedFromType;
	}
	public void setSelectedFromType(String selectedFromType) {
		this.selectedFromType = selectedFromType;
	}
	public String getSelectedToType() {
		return selectedToType;
	}
	public void setSelectedToType(String selectedToType) {
		this.selectedToType = selectedToType;
	}
	public List<TranslatorTypeData> getFromTypeTranslatorList() {
		return fromTypeTranslatorList;
	}
	public void setFromTypeTranslatorList(
			List<TranslatorTypeData> fromTypeTranslatorList) {
		this.fromTypeTranslatorList = fromTypeTranslatorList;
	}
	public List<TranslatorTypeData> getToTypeTranslatorList() {
		return toTypeTranslatorList;
	}
	public void setToTypeTranslatorList(
			List<TranslatorTypeData> toTypeTranslatorList) {
		this.toTypeTranslatorList = toTypeTranslatorList;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAuditId() {
		return auditId;
	}
	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}
	public List<ScriptInstanceData> getExternalScriptList() {
		return externalScriptList;
	}
	public void setExternalScriptList(List<ScriptInstanceData> externalScriptList) {
		this.externalScriptList = externalScriptList;
	}
}
