package com.elitecore.elitesm.datamanager.radius.dictionary.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;

public class DictionaryData extends BaseData implements IDictionaryData, Comparable<DictionaryData> {

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
    private List<DictionaryParameterDetailData> dictionaryParameterDetailList;
    private IStaffData lastModifiedByStaff;
    private IStaffData createdByStaff;
    
    public DictionaryData() {
     dictionaryNumber = -1;
     vendorId=-1;
    }
    
    
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
	public int compareTo(DictionaryData dictionaryData) {
		long result =this.vendorId-dictionaryData.getVendorId();
		if(result>0){
			return 1;
		}else if(result==0)	{
			return 0;
		}else{
			return -1;
		}
	}


	public List<DictionaryParameterDetailData> getDictionaryParameterDetailList() {
		return dictionaryParameterDetailList;
	}


	public void setDictionaryParameterDetailList(
			List<DictionaryParameterDetailData> dictionaryParameterDetailList) {
		this.dictionaryParameterDetailList = dictionaryParameterDetailList;
	}
	
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ DictionaryParameterDetailData in DictionaryData --------------");
		writer.println("dictionaryParameterDetail                                :"+dictionaryParameterDetail);
		writer.println("------------ DictionaryParameterDetailData in DictionaryData --------------");
		
		return out.toString();
		
	}
	
	

}
