package com.elitecore.elitesm.datamanager.datasource.ldap.data;

import javax.xml.bind.annotation.XmlTransient;

public interface ILDAPBaseDnDetailData {
	
	public String getSearchBaseDn();
	public void setSearchBaseDn(String searchBaseDn);
	
	public String getLdapDsId();
	public void setLdapDsId(String ldapDsId);
	
	public String getBaseDnDetailId();
	public void setBaseDnDetailId(String baseDnDetailId);
	
	public Integer getOrderNumber();
	public void setOrderNumber(Integer orderNumber);

}
