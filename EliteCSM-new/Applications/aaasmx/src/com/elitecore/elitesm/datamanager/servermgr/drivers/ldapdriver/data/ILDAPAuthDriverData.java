package com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data;

import java.util.List;

public interface ILDAPAuthDriverData {

	public String getLdapDriverId() ;
	public void setLdapDriverId(String ldapDriverId) ;
	
	/*public long getDsStatusCheckInterval() ;
	public void setDsStatusCheckInterval(long dsStatusCheckInterval) ;*/
	
	public String getExpiryDatePattern() ;
	public void setExpiryDatePattern(String expiryDatePattern) ;
	
	public Long getPasswordDecryptType() ;
	public void setPasswordDecryptType(Long passwordDecryptType) ;
	
	public Long getQueryMaxExecTime() ;
	public void setQueryMaxExecTime(Long queryMaxExecTime) ;
	
	public String getDriverInstanceId() ;
	public void setDriverInstanceId(String driverInstanceId) ;
	
	public String getLdapDsId() ;
	public void setLdapDsId(String ldapDsId) ;
	
	public List<LDAPAuthFieldMapData> getLdapAuthDriverFieldMapList() ;
	public void setLdapAuthDriverFieldMapList(List<LDAPAuthFieldMapData> ldapAuthDriverFieldMapList) ;
		
}
