package com.elitecore.elitesm.web.radius.dictionary.forms;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Set;

import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class ViewDictionaryForm extends BaseDictionaryForm {

	 
    private String dictionaryId;
    private String name;
    private String description;
    private String modalNumber;
    private String editable;
    private String systemGenerated;
    private long dictionaryNumber;
    private String commonStatusId;
    private Timestamp lastModifiedDate;
    private String lastModifiedByStaffId;
    private long vendorId;
    private Timestamp createDate;
    private String createdByStaffId;
    private Timestamp statusChangedDate;
    private Set dictionaryParameterDetail;
    private IStaffData lastModifiedByStaff;
    private IStaffData createdByStaff;    
	private static java.text.DateFormat formatter = new java.text.SimpleDateFormat("dd-MMM-yyyy");    
    
    public String getCommonStatusId() {
        return commonStatusId;
    }
    public void setCommonStatusId(String commonStatusId) {
        this.commonStatusId = commonStatusId;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDictionaryId() {
        return dictionaryId;
    }
    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
    public long getDictionaryNumber() {
        return dictionaryNumber;
    }
    public void setDictionaryNumber(long dictionaryNumber) {
        this.dictionaryNumber = dictionaryNumber;
    }
    public String getEditable() {
        return editable;
    }
    public void setEditable(String editable) {
        this.editable = editable;
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
    public String getModalNumber() {
        return modalNumber;
    }
    public void setModalNumber(String modalNumber) {
        this.modalNumber = modalNumber;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Timestamp getStatusChangedDate() {
        return statusChangedDate;
    }
    public void setStatusChangedDate(Timestamp statusChangedDate) {
        this.statusChangedDate = statusChangedDate;
    }
    public String getSystemGenerated() {
        return systemGenerated;
    }
    public void setSystemGenerated(String systemGenerated) {
        this.systemGenerated = systemGenerated;
    }
    public long getVendorId() {
        return vendorId;
    }
    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }
    public Set getDictionaryParameterDetail() {
        return dictionaryParameterDetail;
    }
    public void setDictionaryParameterDetail(Set dictionaryParameterDetail) {
        this.dictionaryParameterDetail = dictionaryParameterDetail;
    }
    
    
	public String getStrStatusChangedDate(){
		return formatter.format(statusChangedDate);
	}
	public void setStrStatusChangedDate(String strStatusChangeDate){
		try{
			this.statusChangedDate=new Timestamp(formatter.parse(strStatusChangeDate).getTime());
		}catch(ParseException e){
			e.printStackTrace();
		}
	}

	public String getStrLastModifiedDate(){
		return formatter.format(lastModifiedDate);
	}
	public void setStrLastModifiedDate(String strStatusChangeDate){
		try{
			this.lastModifiedDate=new Timestamp(formatter.parse(strStatusChangeDate).getTime());
		}catch(ParseException e){
			e.printStackTrace();
		}
	}

	public String getStrCreateDate(){
		return formatter.format(createDate);
	}
	public void setStrCreateDate(String strStatusChangeDate){
		try{
			this.createDate=new Timestamp(formatter.parse(strStatusChangeDate).getTime());
		}catch(ParseException e){
			e.printStackTrace();
		}
	}

    public IStaffData getLastModifiedByStaff() {
        return lastModifiedByStaff;
    }
    public void setLastModifiedByStaff(IStaffData lastModifiedByStaff) {
        this.lastModifiedByStaff = lastModifiedByStaff;
    }
    public IStaffData getCreatedByStaff() {
        return createdByStaff;
    }
    public void setCreatedByStaff(IStaffData createdByStaff) {
        this.createdByStaff = createdByStaff;
    }

}
