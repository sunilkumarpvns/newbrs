/**
 * 
 */
package com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * @author pratik.chauhan
 *
 */
public class DiameterDictionaryData implements Serializable{

	private static final long serialVersionUID = 1L;
   
	private Long dictionaryId;
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
	private Set<DiameterDictionaryParamDetailData> diameterDictionaryParamDetailSet;
	private List<DiameterDictionaryParamDetailData> diameterDictionaryParamDetailList; 
	
	
	
	
	/**
	 * @return the dictionaryId
	 */
	public Long getDictionaryId() {
		return dictionaryId;
	}
	/**
	 * @param dictionaryId the dictionaryId to set
	 */
	public void setDictionaryId(Long dictionaryId) {
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

	public Set<DiameterDictionaryParamDetailData> getDiameterDictionaryParamDetailSet() {
		return diameterDictionaryParamDetailSet;
	}
	public void setDiameterDictionaryParamDetailSet(
			Set<DiameterDictionaryParamDetailData> diameterDictionaryParamDetailSet) {
		this.diameterDictionaryParamDetailSet = diameterDictionaryParamDetailSet;
	}
	public List<DiameterDictionaryParamDetailData> getDiameterDictionaryParamDetailList() {
		return diameterDictionaryParamDetailList;
	}
	public void setDiameterDictionaryParamDetailList(
			List<DiameterDictionaryParamDetailData> diameterDictionaryParamDetailList) {
		this.diameterDictionaryParamDetailList = diameterDictionaryParamDetailList;
	}
	
	public String toString(){
		
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ Diameter Dictionary Data --------------");
		writer.println("vendorName 	             				 :"+vendorName);
		writer.println("applicationName                          :"+applicationName);           
		writer.println("description                   			 :"+description);
		writer.println("modalNumber                        		 :"+modalNumber);         
		writer.println("editable                                 :"+editable);
		writer.println("systemGenerated	           		         :"+systemGenerated);   
		writer.println("dictionaryNumber                         :"+dictionaryNumber);
		writer.println("commonStatusId                           :"+commonStatusId);
		writer.println("vendorId   	                             :"+vendorId);          
		writer.println("applicationId			                 :"+applicationId);	
		writer.println("lastModifiedDate			             :"+lastModifiedDate);	
		writer.println("lastModifiedByStaffId		             :"+lastModifiedByStaffId);	
		writer.println("createDate			                     :"+createDate);
		writer.println("createdByStaffId	                     :"+createdByStaffId);
		writer.println("statusChangedDate	  					 :"+statusChangedDate);
		writer.println("diameterDictionaryParamDetailList        :"+diameterDictionaryParamDetailList);
		writer.println("----------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
    
				
	}
	
	

}
