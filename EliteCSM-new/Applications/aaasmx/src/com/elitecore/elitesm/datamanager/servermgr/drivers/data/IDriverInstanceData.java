package com.elitecore.elitesm.datamanager.servermgr.drivers.data;

import java.sql.Timestamp;

public interface IDriverInstanceData {
	
	public String getDriverInstanceId() ;
	public void setDriverInstanceId(String driverInstanceId) ;
	
	public String getName() ;
	public void setName(String name) ;
	
	public String getDescription() ;
	public void setDescription(String description) ;
	
	public String getStatus() ;
	public void setStatus(String status) ;
	
	public String getCreatedByStaffId() ;
	public void setCreatedByStaffId(String createdByStaffId) ;
	
	public String getLastModifiedByStaffId() ;
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) ;
	
	public Timestamp getLastModifiedDate() ;
	public void setLastModifiedDate(Timestamp lastModifiedDate) ;
	
	public Timestamp getCreateDate() ;
	public void setCreateDate(Timestamp createDate) ;
	
	public long getDriverTypeId() ;
	public void setDriverTypeId(long driverTypeId) ;
		
}
