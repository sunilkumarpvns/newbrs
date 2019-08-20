package com.elitecore.netvertexsm.datamanager.servermgr.data;

/**
 * 
 * @author dhavalraval
 *
 */
public interface INetServerInstanceData {
	public long getNetServerId();
	public void setNetServerId(long netServerId);
	public String getNetServerCode();
	public void setNetServerCode(String netServerCode);
	public String getName();
	public void setName(String name);
	public String getDescription();
	public void setDescription(String description);
	public String getVersion();
	public void setVersion(String version);
	public String getNetServerTypeId();
	public void setNetServerTypeId(String netServerTypeId) ;
	public String getAdminHost();
	public void setAdminHost(String adminHost);
	public int getAdminPort();
	public void setAdminPort(int adminPort);
	public java.sql.Timestamp getCreateDate();
	public void setCreateDate(java.sql.Timestamp createDate);
	public Long getCreatedByStaffId();
	public void setCreatedByStaffId(Long createdByStaffId);
	public java.sql.Timestamp getLastModifiedDate();
	public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate);
	public Long getLastModifiedByStaffId() ;
	public void setLastModifiedByStaffId(Long lastModifiedByStaffId);
	public String getCommonStatusId();
	public void setCommonStatusId(String commonStatusId);
	public java.sql.Timestamp getStatusChangeDate();
	public void setStatusChangeDate(java.sql.Timestamp statusChangeDate) ;
	public String getSystemGenerated() ;
	public void setSystemGenerated(String systemGenerated) ;
	public java.sql.Timestamp getLastSyncDate() ;
	public void setLastSyncDate(java.sql.Timestamp lastSyncDate);
	public java.sql.Timestamp getLastSuccessSynDate() ;
	public void setLastSuccessSynDate(java.sql.Timestamp lastSuccessSynDate);
	public String getLastSyncStatus() ;
	public void setLastSyncStatus(String lastSyncStatus);
	/*public String getConfigInstanceId();
	public void setConfigInstanceId(String configInstanceId);*/
	public INetServerTypeData getNetServerType();
	public void setNetServerType(INetServerTypeData netServerType);
	public String getJavaHome(); 
	public void setJavaHome( String javaHome );
	public String getServerHome();
	public void setServerHome( String serverHome );
	public NetServerStartupConfigData getStartupConfig();
	public void setStartupConfig( NetServerStartupConfigData startupConfig );
	public boolean isInSync( );
	public String getIsInSync( ) ;
	public void setIsInSync( String isInSync );
	public String toString();
}
