package com.elitecore.netvertexsm.datamanager.datasource.ldap.data;

import java.io.Serializable;
import java.util.List;

public class LDAPBaseDnDetailData implements Serializable, ILDAPBaseDnDetailData{
	
	private long baseDnDetailId;
	private String searchBaseDn;
	private long ldapDsId;
	private List baseDnDetailList;

	public long getBaseDnDetailId() {
		return baseDnDetailId;
	}

	public long getLdapDsId() {
		return ldapDsId;
	}

	public String getSearchBaseDn() {
		return searchBaseDn;
	}

	public void setBaseDnDetailId(long baseDnDetailId) {
		this.baseDnDetailId = baseDnDetailId;
	}

	public void setLdapDsId(long ldapDsId) {
		this.ldapDsId = ldapDsId;
	}

	public void setSearchBaseDn(String searchBaseDn) {
		this.searchBaseDn = searchBaseDn;
	}

	public List getBaseDnDetailList() {
		return baseDnDetailList;
	}

	public void setBaseDnDetailList(List baseDnDetailList) {
		this.baseDnDetailList = baseDnDetailList;
	}

	
}
