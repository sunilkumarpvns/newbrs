package com.elitecore.netvertexsm.datamanager.datasource.ldap.data;

public interface ILDAPBaseDnDetailData {
	
	public String getSearchBaseDn();
	public void setSearchBaseDn(String searchBaseDn);
	
	public long getLdapDsId();
	public void setLdapDsId(long ldapDsId);
	
	public long getBaseDnDetailId();
	public void setBaseDnDetailId(long baseDnDetailId);

}
