package com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared;

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
	private String vendorName;
	private String applicationName;
	private String description;
	private String modalNumber;
	private String editable;
	private String systemGenerated;
	private Long dictionaryNumber;
	private String commonStatusId;
	private Long vendorId;
	private Long applicationId;
	private Timestamp lastModifiedDate;
	private String lastModifiedByStaffId;
	private Timestamp createDate;
	private String createdByStaffId;
	private Timestamp statusChangedDate;
	
	private List<AttributeData> attributeList = new ArrayList<AttributeData>();

	
	public DictionaryData() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the dictionaryId
	 */
	public String getDictionaryId() {
		return dictionaryId;
	}


	/**
	 * @param dictionaryId the dictionaryId to set
	 */
	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
	}


	/**
	 * @return the vendorName
	 */
	public String getVendorName() {
		return vendorName;
	}


	/**
	 * @param vendorName the vendorName to set
	 */
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}


	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}


	/**
	 * @param applicationName the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the modalNumber
	 */
	public String getModalNumber() {
		return modalNumber;
	}


	/**
	 * @param modalNumber the modalNumber to set
	 */
	public void setModalNumber(String modalNumber) {
		this.modalNumber = modalNumber;
	}


	/**
	 * @return the editable
	 */
	public String getEditable() {
		return editable;
	}


	/**
	 * @param editable the editable to set
	 */
	public void setEditable(String editable) {
		this.editable = editable;
	}


	/**
	 * @return the systemGenerated
	 */
	public String getSystemGenerated() {
		return systemGenerated;
	}


	/**
	 * @param systemGenerated the systemGenerated to set
	 */
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}


	/**
	 * @return the dictionaryNumber
	 */
	public Long getDictionaryNumber() {
		return dictionaryNumber;
	}


	/**
	 * @param dictionaryNumber the dictionaryNumber to set
	 */
	public void setDictionaryNumber(Long dictionaryNumber) {
		this.dictionaryNumber = dictionaryNumber;
	}


	/**
	 * @return the commonStatusId
	 */
	public String getCommonStatusId() {
		return commonStatusId;
	}


	/**
	 * @param commonStatusId the commonStatusId to set
	 */
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}


	/**
	 * @return the vendorId
	 */
	public Long getVendorId() {
		return vendorId;
	}


	/**
	 * @param vendorId the vendorId to set
	 */
	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}


	/**
	 * @return the applicationId
	 */
	public Long getApplicationId() {
		return applicationId;
	}


	/**
	 * @param applicationId the applicationId to set
	 */
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}


	/**
	 * @return the lastModifiedDate
	 */
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}


	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}


	/**
	 * @return the lastModifiedByStaffId
	 */
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}


	/**
	 * @param lastModifiedByStaffId the lastModifiedByStaffId to set
	 */
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}


	/**
	 * @return the createDate
	 */
	public Timestamp getCreateDate() {
		return createDate;
	}


	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}


	/**
	 * @return the createdByStaffId
	 */
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}


	/**
	 * @param createdByStaffId the createdByStaffId to set
	 */
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}


	/**
	 * @return the statusChangedDate
	 */
	public Timestamp getStatusChangedDate() {
		return statusChangedDate;
	}


	/**
	 * @param statusChangedDate the statusChangedDate to set
	 */
	public void setStatusChangedDate(Timestamp statusChangedDate) {
		this.statusChangedDate = statusChangedDate;
	}


	/**
	 * @return the attributeList
	 */
	public List<AttributeData> getAttributeList() {
		return attributeList;
	}


	/**
	 * @param attributeList the attributeList to set
	 */
	public void setAttributeList(List<AttributeData> attributeList) {
		this.attributeList = attributeList;
	}
	
   	public String toString(){
   		return this.applicationName;
   	}
	
	
	

}
