package com.elitecore.netvertexsm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.netvertexsm.util.constants.BaseConstant;


/**
 * 
 * @author dhavalraval
 *
 */
public class NetServerInstanceData implements INetServerInstanceData{
	private long netServerId;
	private String netServerCode;
	private String name;
	private String description;
	private String version;
	private String netServerTypeId;
	private String adminHost;
	private int adminPort;
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
	private INetServerTypeData netServerType;
	private String javaHome;
        private String serverHome;
        private String isInSync;
        private NetServerStartupConfigData startupConfig;
        
	
        
        
        public NetServerStartupConfigData getStartupConfig() {
            return startupConfig;
        }


        
        public void setStartupConfig( NetServerStartupConfigData startupConfig ) {
            this.startupConfig = startupConfig;
        }


        public String getServerHome() {
            return serverHome;
        }

        
        public void setServerHome( String serverHome ) {
            this.serverHome = serverHome;
        }

        public String getJavaHome() {
            return javaHome;
        }
        
        public void setJavaHome( String javaHome ) {
            this.javaHome = javaHome;
        }
        
 
        public long getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(long netServerId) {
		this.netServerId = netServerId;
	}
	public String getNetServerCode() {
		return netServerCode;
	}
	public void setNetServerCode(String netServerCode) {
		this.netServerCode = netServerCode;
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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getNetServerTypeId() {
		return netServerTypeId;
	}
	public void setNetServerTypeId(String netServerTypeId) {
		this.netServerTypeId = netServerTypeId;
	}
	public String getAdminHost() {
		return adminHost;
	}
	public void setAdminHost(String adminHost) {
		this.adminHost = adminHost;
	}
	public int getAdminPort() {
		return adminPort;
	}
	public void setAdminPort(int adminPort) {
		this.adminPort = adminPort;
	}
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
	public INetServerTypeData getNetServerType() {
		return netServerType;
	}
	public void setNetServerType(INetServerTypeData netServerType) {
		this.netServerType = netServerType;
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
            writer.println("------------NetServerInstanceData-----------------");
            writer.println("netServerId=" +netServerId);                                     
            writer.println("netServerCode=" +netServerCode);                                     
            writer.println("name=" +name);                                     
            writer.println("description=" +description);                                     
            writer.println("version=" +version);                                     
            writer.println("netServerTypeId=" +netServerTypeId);                                     
            writer.println("adminHost=" +adminHost);                                     
            writer.println("adminPort=" +adminPort);                                     
            writer.println("createDate=" +createDate);                                     
            writer.println("createdByStaffId=" +createdByStaffId);                                     
            writer.println("lastModifiedDate=" +lastModifiedDate);                                     
            writer.println("lastModifiedByStaffId=" +lastModifiedByStaffId);                                     
            writer.println("commonStatusId=" +commonStatusId);                                     
            writer.println("statusChangeDate=" +statusChangeDate);                                     
            writer.println("systemGenerated=" +systemGenerated);                                     
            writer.println("lastSyncDate=" +lastSyncDate);                                     
            writer.println("lastSuccessSynDate=" +lastSuccessSynDate);                                     
            writer.println("lastSyncStatus=" +lastSyncStatus);                                     
            writer.println("netServerType=" +netServerType);                                     
            writer.println("javaHome=" +javaHome); 
            writer.println("serverHome=" +serverHome);                                     
            writer.println("isInSync=" +isInSync); 
            writer.println("----------------------------------------------------");
            writer.close();                                               
            return out.toString();
        }
	
	
	
        
}
