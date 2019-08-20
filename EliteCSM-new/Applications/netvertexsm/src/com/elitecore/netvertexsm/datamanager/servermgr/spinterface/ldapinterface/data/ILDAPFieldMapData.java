package com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data;

public interface ILDAPFieldMapData {
	public Long getLdapFieldMapId();
	public void setLdapFieldMapId(Long ldapFieldMapId);
	public String getLogicalName();
	public void setLogicalName(String logicalName);
	public String getLdapAttribute();
	public void setLdapAttribute(String ldapAttribute);
	public Long getLdapSPInterfaceId();
	public void setLdapSPInterfaceId(Long ldapSPinterfaceId);
}
