package com.elitecore.elitesm.datamanager.datasource.ldap.data;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class LDAPBaseDnDetailData extends BaseData implements ILDAPBaseDnDetailData{
	
	private String baseDnDetailId;
	private String searchBaseDn;
	private String ldapDsId;
	private List baseDnDetailList;
	private Integer orderNumber;

	@XmlTransient
	public String getBaseDnDetailId() {
		return baseDnDetailId;
	}

	@XmlTransient
	public String getLdapDsId() {
		return ldapDsId;
	}

	@XmlElement(name="ldap-search-base-dn")
	public String getSearchBaseDn() {
		return searchBaseDn;
	}

	public void setBaseDnDetailId(String baseDnDetailId) {
		this.baseDnDetailId = baseDnDetailId;
	}

	public void setLdapDsId(String ldapDsId) {
		this.ldapDsId = ldapDsId;
	}

	public void setSearchBaseDn(String searchBaseDn) {
		this.searchBaseDn = searchBaseDn;
	}

	@XmlTransient
	public List getBaseDnDetailList() {
		return baseDnDetailList;
	}

	public void setBaseDnDetailList(List baseDnDetailList) {
		this.baseDnDetailList = baseDnDetailList;
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof LDAPBaseDnDetailData)){
			return false;
		}
		return searchBaseDn.equals(((LDAPBaseDnDetailData)obj).searchBaseDn);
	}

	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}
