package com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data;

import java.io.Serializable;

public class LDAPFieldMapData  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long ldapFieldMapId;
	private String logicalName;
	private String ldapAttribute;
	private Long ldapSPInterfaceId;
	
	public Long getLdapFieldMapId() {
		return ldapFieldMapId;
	}
	public void setLdapFieldMapId(Long ldapFieldMapId) {
		this.ldapFieldMapId = ldapFieldMapId;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public String getLdapAttribute() {
		return ldapAttribute;
	}
	public void setLdapAttribute(String ldapAttribute) {
		this.ldapAttribute = ldapAttribute;
	}
	public Long getLdapSPInterfaceId() {
		return ldapSPInterfaceId;
	}
	public void setLdapSPInterfaceId(Long ldapDriverId) {
		this.ldapSPInterfaceId = ldapDriverId;
	}
}
