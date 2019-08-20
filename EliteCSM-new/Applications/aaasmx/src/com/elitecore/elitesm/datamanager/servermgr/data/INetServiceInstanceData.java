package com.elitecore.elitesm.datamanager.servermgr.data;


public interface INetServiceInstanceData {
    public String getNetServiceId();
    public void setNetServiceId(String netServiceId);
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
    public String getNetServerId();
    public void setNetServerId(String netServerId) ;
    public java.sql.Timestamp getCreateDate() ;
    public void setCreateDate(java.sql.Timestamp createDate);
    public String getCreatedByStaffId() ;
    public void setCreatedByStaffId(String createdByStaffId) ;
    public java.sql.Timestamp getLastModifiedDate() ;
    public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) ;
    public String getLastModifiedByStaffId() ;
    public void setLastModifiedByStaffId(String lastModifiedByStaffId) ;
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
