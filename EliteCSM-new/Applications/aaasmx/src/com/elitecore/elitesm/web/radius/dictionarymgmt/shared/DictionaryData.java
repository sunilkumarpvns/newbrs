package com.elitecore.elitesm.web.radius.dictionarymgmt.shared;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DictionaryData implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String dictionaryId;
    private String name;
    private String description;
    private String modalNumber;
    private String editable;
    private String systemGenerated;
    private long dictionaryNumber;
    private String commonStatusId;
    private Timestamp lastModifiedDate;
    private Long lastModifiedByStaffId;
    private long vendorId;
    private Timestamp createDate;
    private Long createdByStaffId;
    private Timestamp statusChangedDate;
    //private Set dictionaryParameterDetail;
    private List<AttributeData> attributeList = new ArrayList<AttributeData>();

    
    
    
    public DictionaryData(){
    	
    }
	
    
	public List<AttributeData> getAttributeList() {
		return attributeList;
	}


	public void setAttributeList(List<AttributeData> attributeList) {
		this.attributeList = attributeList;
	}


	public String getDictionaryId() {
		return dictionaryId;
	}
	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
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
	public String getModalNumber() {
		return modalNumber;
	}
	public void setModalNumber(String modalNumber) {
		this.modalNumber = modalNumber;
	}
	public String getEditable() {
		return editable;
	}
	public void setEditable(String editable) {
		this.editable = editable;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	public long getDictionaryNumber() {
		return dictionaryNumber;
	}
	public void setDictionaryNumber(long dictionaryNumber) {
		this.dictionaryNumber = dictionaryNumber;
	}
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Long getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(Long lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	public long getVendorId() {
		return vendorId;
	}
	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public Long getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(Long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public Timestamp getStatusChangedDate() {
		return statusChangedDate;
	}
	public void setStatusChangedDate(Timestamp statusChangedDate) {
		this.statusChangedDate = statusChangedDate;
	}

	
	
	
	
	

}
