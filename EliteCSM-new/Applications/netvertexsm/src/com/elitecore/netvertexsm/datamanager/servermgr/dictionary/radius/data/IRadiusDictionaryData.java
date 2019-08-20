package com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;

public interface IRadiusDictionaryData {

    public String getCommonStatusId() ;
    public void setCommonStatusId(String commonStatusId) ;
    public Timestamp getCreateDate() ;
    public void setCreateDate(Timestamp createDate) ;
    public String getCreatedByStaffId() ;
    public void setCreatedByStaffId(String createdByStaffId) ;
    public String getDescription() ;
    public void setDescription(String description) ;
    public long getDictionaryId() ;
    public void setDictionaryId(long dictionaryId) ;
    public long getDictionaryNumber() ;
    public void setDictionaryNumber(long dictionaryNumber) ;
    public String getEditable() ;
    public void setEditable(String editable) ;
    public String getLastModifiedByStaffId() ;
    public void setLastModifiedByStaffId(String lastModifiedByStaffId) ;
    public Timestamp getLastModifiedDate() ;
    public void setLastModifiedDate(Timestamp lastModifiedDate) ;
    public String getModalNumber() ;
    public void setModalNumber(String modalNumber) ;
    public String getName() ;
    public void setName(String name) ;
    public Timestamp getStatusChangedDate() ;
    public void setStatusChangedDate(Timestamp statusChangedDate) ;
    public Set getDictionaryParameterDetail() ;
    public void setDictionaryParameterDetail(Set dictionaryParameterDetail) ;
    public String getSystemGenerated() ;
    public void setSystemGenerated(String systemGenerated) ;
    public long getVendorId() ;
    public void setVendorId(long vendorId) ;
    public IStaffData getLastModifiedByStaff();
    public void setLastModifiedByStaff(IStaffData lastModifiedByStaff);
    public IStaffData getCreatedByStaff();
    public void setCreatedByStaff(IStaffData createdByStaff);
    public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailList();
	public void setDictionaryParameterDetailList(List<RadiusDictionaryParamDetailData> dictionaryParameterDetailList);

}
