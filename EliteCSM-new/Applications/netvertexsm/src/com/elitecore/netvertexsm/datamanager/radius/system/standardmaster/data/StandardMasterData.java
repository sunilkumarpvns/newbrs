package com.elitecore.netvertexsm.datamanager.radius.system.standardmaster.data;

import java.sql.Timestamp;

import com.elitecore.netvertexsm.datamanager.core.base.data.BaseData;

public class StandardMasterData extends BaseData implements IStandardMasterData{

	private String masterId                  ; 
	private String masterTypeId              ; 
	private String name                      ; 
	private String alias                     ; 
	private String description               ; 
	private String categoryName              ; 
	private String editable                  ; 
	private Timestamp createDate             ; 
	private String createdByStaffId          ; 
	private Timestamp lastModifiedDate       ; 
	private String lastModifiedDateByStaffId ; 
	private String systemGenerated           ; 
	private String enableDisable             ; 
	private String showHide                  ; 
	private Timestamp enableDisableChangeDate; 
	private Timestamp showHideChangeDate     ; 
	private String enableDisableByStaffId    ; 
	private String showHideByStaffId         ; 
	private String strParam1                 ; 
	private String strParam2                 ; 
	private String strParam3                 ; 
	private Timestamp dateParam1             ; 
	private Timestamp dateParam2             ; 
	private Timestamp dateParam3             ; 
	private long numParam1                   ; 
	private long numParam2                   ; 
	private long numParam3                   ; 
	private String charParam1                ; 
	private String charParam2                ; 
	private String charParam3                ; 
	private float floatNum1                  ; 
	private float floatNum2                  ; 
	private float floatNum3                  ; 
	private String displayName               ; 
	private String displayNameLock           ;
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCharParam1() {
		return charParam1;
	}
	public void setCharParam1(String charParam1) {
		this.charParam1 = charParam1;
	}
	public String getCharParam2() {
		return charParam2;
	}
	public void setCharParam2(String charParam2) {
		this.charParam2 = charParam2;
	}
	public String getCharParam3() {
		return charParam3;
	}
	public void setCharParam3(String charParam3) {
		this.charParam3 = charParam3;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public Timestamp getDateParam1() {
		return dateParam1;
	}
	public void setDateParam1(Timestamp dateParam1) {
		this.dateParam1 = dateParam1;
	}
	public Timestamp getDateParam2() {
		return dateParam2;
	}
	public void setDateParam2(Timestamp dateParam2) {
		this.dateParam2 = dateParam2;
	}
	public Timestamp getDateParam3() {
		return dateParam3;
	}
	public void setDateParam3(Timestamp dateParam3) {
		this.dateParam3 = dateParam3;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDisplayNameLock() {
		return displayNameLock;
	}
	public void setDisplayNameLock(String displayNameLock) {
		this.displayNameLock = displayNameLock;
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
	public String getEnableDisableByStaffId() {
		return enableDisableByStaffId;
	}
	public void setEnableDisableByStaffId(String enableDisableByStaffId) {
		this.enableDisableByStaffId = enableDisableByStaffId;
	}
	public Timestamp getEnableDisableChangeDate() {
		return enableDisableChangeDate;
	}
	public void setEnableDisableChangeDate(Timestamp enableDisableChangeDate) {
		this.enableDisableChangeDate = enableDisableChangeDate;
	}
	public float getFloatNum1() {
		return floatNum1;
	}
	public void setFloatNum1(float floatNum1) {
		this.floatNum1 = floatNum1;
	}
	public float getFloatNum2() {
		return floatNum2;
	}
	public void setFloatNum2(float floatNum2) {
		this.floatNum2 = floatNum2;
	}
	public float getFloatNum3() {
		return floatNum3;
	}
	public void setFloatNum3(float floatNum3) {
		this.floatNum3 = floatNum3;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getLastModifiedDateByStaffId() {
		return lastModifiedDateByStaffId;
	}
	public void setLastModifiedDateByStaffId(String lastModifiedDateByStaffId) {
		this.lastModifiedDateByStaffId = lastModifiedDateByStaffId;
	}
	public String getMasterId() {
		return masterId;
	}
	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}
	public String getMasterTypeId() {
		return masterTypeId;
	}
	public void setMasterTypeId(String masterTypeId) {
		this.masterTypeId = masterTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getNumParam1() {
		return numParam1;
	}
	public void setNumParam1(long numParam1) {
		this.numParam1 = numParam1;
	}
	public long getNumParam2() {
		return numParam2;
	}
	public void setNumParam2(long numParam2) {
		this.numParam2 = numParam2;
	}
	public long getNumParam3() {
		return numParam3;
	}
	public void setNumParam3(long numParam3) {
		this.numParam3 = numParam3;
	}
	public String getShowHide() {
		return showHide;
	}
	public void setShowHide(String showHide) {
		this.showHide = showHide;
	}
	public String getShowHideByStaffId() {
		return showHideByStaffId;
	}
	public void setShowHideByStaffId(String showHideByStaffId) {
		this.showHideByStaffId = showHideByStaffId;
	}
	public Timestamp getShowHideChangeDate() {
		return showHideChangeDate;
	}
	public void setShowHideChangeDate(Timestamp showHideChangeDate) {
		this.showHideChangeDate = showHideChangeDate;
	}
	public String getStrParam1() {
		return strParam1;
	}
	public void setStrParam1(String strParam1) {
		this.strParam1 = strParam1;
	}
	public String getStrParam2() {
		return strParam2;
	}
	public void setStrParam2(String strParam2) {
		this.strParam2 = strParam2;
	}
	public String getStrParam3() {
		return strParam3;
	}
	public void setStrParam3(String strParam3) {
		this.strParam3 = strParam3;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
		
}
