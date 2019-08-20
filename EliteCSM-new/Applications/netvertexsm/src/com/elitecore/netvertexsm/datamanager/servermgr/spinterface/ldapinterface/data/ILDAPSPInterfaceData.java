package com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data;

import java.util.Set;

public interface ILDAPSPInterfaceData {

	public long getLdapSPInterfaceId();
	public void setLdapSPInterfaceId(long ldapSPInterfaceId);
	public String getExpiryDatePattern();
	public void setExpiryDatePattern(String expiryDatePattern);
	public Integer getPasswordDecryptType();
	public void setPasswordDecryptType(Integer passwordDecryptType);
	public Long getQueryMaxExecTime();
	public void setQueryMaxExecTime(Long queryMaxExecTime);
	public Long getDriverInstanceId();
	public void setDriverInstanceId(Long driverInstanceId);
	public Long getLdapDsId();
	public void setLdapDsId(Long ldapDsId);
	
	public Set<LDAPFieldMapData> getFieldMapSet();
	public void setFieldMapSet(Set<LDAPFieldMapData> fieldMapSet);
}
