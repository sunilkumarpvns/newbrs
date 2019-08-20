package com.elitecore.elitesm.datamanager.radius.system.standardmaster.data;

import java.util.Date;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class StandardMasterTypeData extends BaseData implements IStandardMasterTypeData{

	private String masterTypeId              ; 
	private String typeName                  ; 
	private String alias                     ; 
	private String description               ; 
	private String editable                  ; 
	private String systemGenerated           ; 
	private String enableDisable             ; 
	private String showHide                  ;
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEditable() {
		return editable;
	}
	public void setEditable(String editable) {
		this.editable = editable;
	}
	public String getEnableDisable() {
		return enableDisable;
	}
	public void setEnableDisable(String enableDisable) {
		this.enableDisable = enableDisable;
	}
	public String getMasterTypeId() {
		return masterTypeId;
	}
	public void setMasterTypeId(String masterTypeId) {
		this.masterTypeId = masterTypeId;
	}
	public String getShowHide() {
		return showHide;
	}
	public void setShowHide(String showHide) {
		this.showHide = showHide;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	} 
	
		
}
