package com.elitecore.netvertexsm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.netvertexsm.util.constants.BaseConstant;

/**
 * 
 * @author dhavalraval
 *
 */
public class NetServiceInstanceData implements INetServiceInstanceData{
    private long netServiceId;
    private String instanceId;
    private String name;
    private String displayName;
    private String description;
    private String netServiceTypeId;
    private long netServerId;
//  private String configInstanceId;
    private java.sql.Timestamp createDate;
    private Long createdByStaffId;
    private java.sql.Timestamp lastModifiedDate;
    private Long lastModifiedByStaffId;
    private String commonStatusId;
    private java.sql.Timestamp statusChangeDate;
    private String systemGenerated;
    private java.sql.Timestamp lastSyncDate;
    private java.sql.Timestamp lastSuccessSynDate;
    private String lastSyncStatus;
    private String isInSync;

    public long getNetServiceId() {
        return netServiceId;
    }
    public void setNetServiceId(long netServiceId) {
        this.netServiceId = netServiceId;
    }
    public String getInstanceId() {
        return instanceId;
    }
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getNetServiceTypeId() {
        return netServiceTypeId;
    }
    public void setNetServiceTypeId(String netServiceTypeId) {
        this.netServiceTypeId = netServiceTypeId;
    }
    public long getNetServerId() {
        return netServerId;
    }
    public void setNetServerId(long netServerId) {
        this.netServerId = netServerId;
    }
    /*public String getConfigInstanceId() {
		return configInstanceId;
	}
	public void setConfigInstanceId(String configInstanceId) {
		this.configInstanceId = configInstanceId;
	}*/
    public java.sql.Timestamp getCreateDate() {
        return createDate;
    }
    public void setCreateDate(java.sql.Timestamp createDate) {
        this.createDate = createDate;
    }
    public Long getCreatedByStaffId() {
        return createdByStaffId;
    }
    public void setCreatedByStaffId(Long createdByStaffId) {
        this.createdByStaffId = createdByStaffId;
    }
    public java.sql.Timestamp getLastModifiedDate() {
        return lastModifiedDate;
    }
    public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    public Long getLastModifiedByStaffId() {
        return lastModifiedByStaffId;
    }
    public void setLastModifiedByStaffId(Long lastModifiedByStaffId) {
        this.lastModifiedByStaffId = lastModifiedByStaffId;
    }
    public String getCommonStatusId() {
        return commonStatusId;
    }
    public void setCommonStatusId(String commonStatusId) {
        this.commonStatusId = commonStatusId;
    }
    public java.sql.Timestamp getStatusChangeDate() {
        return statusChangeDate;
    }
    public void setStatusChangeDate(java.sql.Timestamp statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }
    public String getSystemGenerated() {
        return systemGenerated;
    }
    public void setSystemGenerated(String systemGenerated) {
        this.systemGenerated = systemGenerated;
    }
    public java.sql.Timestamp getLastSyncDate() {
        return lastSyncDate;
    }
    public void setLastSyncDate(java.sql.Timestamp lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }
    public java.sql.Timestamp getLastSuccessSynDate() {
        return lastSuccessSynDate;
    }
    public void setLastSuccessSynDate(java.sql.Timestamp lastSuccessSynDate) {
        this.lastSuccessSynDate = lastSuccessSynDate;
    }
    public String getLastSyncStatus() {
        return lastSyncStatus;
    }
    public void setLastSyncStatus(String lastSyncStatus) {
        this.lastSyncStatus = lastSyncStatus;
    }

    public boolean isInSync( ) {
        if(isInSync != null && isInSync.equalsIgnoreCase(BaseConstant.SHOW_STATUS_ID))
            return true;
        return false; 
    }
    public String getIsInSync( ) {
        return isInSync;
    }
    public void setIsInSync( String isInSync ) {
        this.isInSync = isInSync;
    }



    @Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------NetServiceInstanceData-----------------");
        writer.println("netServiceId="+netServiceId);
        writer.println("instanceId="+instanceId);
        writer.println("name="+name);
        writer.println("displayName="+displayName);
        writer.println("description="+description);
        writer.println("netServiceTypeId="+netServiceTypeId);
        writer.println("netServerId="+netServerId);
        writer.println("createDate="+createDate);
        writer.println("createdByStaffId="+createdByStaffId);
        writer.println("lastModifiedDate="+lastModifiedDate);
        writer.println("lastModifiedByStaffId="+lastModifiedByStaffId);
        writer.println("commonStatusId="+commonStatusId);
        writer.println("statusChangeDate="+statusChangeDate);
        writer.println("systemGenerated="+systemGenerated);
        writer.println("lastSyncDate="+lastSyncDate);
        writer.println("lastSuccessSynDate="+lastSuccessSynDate);
        writer.println("lastSyncStatus="+lastSyncStatus);
        writer.println("isInSync="+isInSync);
        writer.println("----------------------------------------------------");
        writer.close();
        return out.toString();
    }   

}
