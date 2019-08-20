package com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data;

import java.io.Serializable;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;

public class LDAPSPInterfaceData  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private long ldapSPInterfaceId;
	private String expiryDatePattern;
	private Integer passwordDecryptType;
	private Long queryMaxExecTime;
	private Long driverInstanceId;
	private Long ldapDsId;
	private LDAPDatasourceData ldapDs;
	private Set<LDAPFieldMapData> fieldMapSet;
	
	public LDAPDatasourceData getLdapDs() {
		return ldapDs;
	}
	public void setLdapDs(LDAPDatasourceData ldapDs) {
		this.ldapDs = ldapDs;
	}
	public Set<LDAPFieldMapData> getFieldMapSet() {
		return fieldMapSet;
	}
	public void setFieldMapSet(Set<LDAPFieldMapData> fieldMapSet) {
		this.fieldMapSet = fieldMapSet;
	}
	public long getLdapSPInterfaceId() {
		return ldapSPInterfaceId;
	}
	public void setLdapSPInterfaceId(long ldapDriverId) {
		this.ldapSPInterfaceId = ldapDriverId;
	}
	public String getExpiryDatePattern() {
		return expiryDatePattern;
	}
	public void setExpiryDatePattern(String expiryDatePattern) {
		this.expiryDatePattern = expiryDatePattern;
	}
	public Integer getPasswordDecryptType() {
		return passwordDecryptType;
	}
	public void setPasswordDecryptType(Integer passwordDecryptType) {
		this.passwordDecryptType = passwordDecryptType;
	}
	public Long getQueryMaxExecTime() {
		return queryMaxExecTime;
	}
	public void setQueryMaxExecTime(Long queryMaxExecTime) {
		this.queryMaxExecTime = queryMaxExecTime;
	}
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public Long getLdapDsId() {
		return ldapDsId;
	}
	public void setLdapDsId(Long ldapDsId) {
		this.ldapDsId = ldapDsId;
	}
}
