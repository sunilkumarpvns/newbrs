package com.elitecore.netvertexsm.datamanager.servermgr.data;


public interface INetServiceInstanceData {
    public long getNetServiceId();
    public void setNetServiceId(long netServiceId);
    public String getInstanceId();
    public void setInstanceId(String instanceId);
    public String getName();
    public void setName(String name);
    public String getDisplayName();
    public void setDisplayName(String displayName);
    public String getDescription();
    public void setDescription(String description);
    public String getNetServiceTypeId() ;
    public void setNetServiceTypeId(String netServiceTypeId);
    public long getNetServerId();
    public void setNetServerId(long netServerId) ;
    public java.sql.Timestamp getCreateDate() ;
    public void setCreateDate(java.sql.Timestamp createDate);
    public Long getCreatedByStaffId() ;
    public void setCreatedByStaffId(Long createdByStaffId) ;
    public java.sql.Timestamp getLastModifiedDate() ;
    public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) ;
    public Long getLastModifiedByStaffId() ;
    public void setLastModifiedByStaffId(Long lastModifiedByStaffId) ;
    public String getCommonStatusId() ;
    public void setCommonStatusId(String commonStatusId) ;
    public java.sql.Timestamp getStatusChangeDate() ;
    public void setStatusChangeDate(java.sql.Timestamp statusChangeDate) ;
    public String getSystemGenerated() ;
    public void setSystemGenerated(String systemGenerated) ;
    public java.sql.Timestamp getLastSyncDate() ;
    public void setLastSyncDate(java.sql.Timestamp lastSyncDate) ;
    public java.sql.Timestamp getLastSuccessSynDate() ;
    public void setLastSuccessSynDate(java.sql.Timestamp lastSuccessSynDate) ;
    public String getLastSyncStatus() ;
    public void setLastSyncStatus(String lastSyncStatus) ;
    public boolean isInSync( );
    public String getIsInSync( ) ;
    public void setIsInSync( String isInSync );
    public String toString();
}
