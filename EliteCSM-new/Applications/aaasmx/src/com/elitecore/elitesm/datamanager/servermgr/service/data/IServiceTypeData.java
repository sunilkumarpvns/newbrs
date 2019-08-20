package com.elitecore.elitesm.datamanager.servermgr.service.data;

import java.util.Set;

public interface IServiceTypeData {

	public long getServiceTypeId() ;
	public void setServiceTypeId(long serviceTypeId) ;
	
	public String getName() ;
	public void setName(String name) ;
	
	public String getDisplayName() ;
	public void setDisplayName(String displayName) ;
	
	public String getAlias() ;
	public void setAlias(String alias) ;
	
	public long getSerialNo() ;
	public void setSerialNo(long serialNo) ;
	
	public String getDescription() ;
	public void setDescription(String description) ;
	
	public String getStatus() ;
	public void setStatus(String status) ;
	
	public Set getDriverTypeSet();
	public void setDriverTypeSet(Set driverTypeSet) ;
	
}
