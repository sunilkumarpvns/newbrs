package com.elitecore.elitesm.datamanager.servermgr.drivers.data;


public interface IDriverTypeData {
	
	public long getDriverTypeId() ;
	public void setDriverTypeId(long driverTypeId) ;
	
	public long getServiceTypeId() ;
	public void setServiceTypeId(long serviceTypeId) ;
	
	public String getName() ;
	public void setName(String name) ;
	
	public long getSerialNo() ;
	public void setSerialNo(long serialNo) ;
	
	public String getDisplayName() ;
	public void setDisplayName(String displayName); 

	public String getAlias() ;
	public void setAlias(String alias) ;
	
	public String getDescription() ;
	public void setDescription(String description) ;
	
	public String getStatus() ;
	public void setStatus(String status) ;
	
}
