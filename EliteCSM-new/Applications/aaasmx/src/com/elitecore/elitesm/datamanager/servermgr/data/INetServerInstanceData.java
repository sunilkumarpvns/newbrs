package com.elitecore.elitesm.datamanager.servermgr.data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 
 * @author dhavalraval
 *
 */
public interface INetServerInstanceData {
	public String getNetServerId();
	public void setNetServerId(String netServerId);
	
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
	
	public Integer getAdminPort();
	public void setAdminPort(Integer adminPort);
	
	public Timestamp getCreateDate();
	public void setCreateDate(Timestamp createDate);
	
	public String getCreatedByStaffId();
	public void setCreatedByStaffId(String createdByStaffId);
	
	public Timestamp getLastModifiedDate();
	public void setLastModifiedDate(Timestamp lastModifiedDate);
	
	public String getLastModifiedByStaffId() ;
	public void setLastModifiedByStaffId(String lastModifiedByStaffId);
	
	public String getCommonStatusId();
	public void setCommonStatusId(String commonStatusId);
	
	public Timestamp getStatusChangeDate();
	public void setStatusChangeDate(Timestamp statusChangeDate) ;
	
	public String getSystemGenerated() ;
	public void setSystemGenerated(String systemGenerated) ;
	
	public Timestamp getLastSyncDate() ;
	public void setLastSyncDate(Timestamp lastSyncDate);
	
	public Timestamp getLastSuccessSynDate() ;
	public void setLastSuccessSynDate(Timestamp lastSuccessSynDate);
	
	public String getLastSyncStatus() ;
	public void setLastSyncStatus(String lastSyncStatus);
	
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
	
	public Integer getLicenseExpiryDays();
	public void setLicenseExpiryDays(Integer licenseExpiryDays);
	
	public Date getLicenseCheckDate();
	public void setLicenseCheckDate(Date licenseCheckDate);
	
	public String getStaff();
	public void setStaff(String staff);
	
}
