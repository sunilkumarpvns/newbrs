package com.elitecore.corenetvertex.spr.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="com.elitecore.corenetvertex.spr.data.LDAPBaseDnDetailData")
@Table(name="TBLMLDAPBASEDNDETAIL")
public class LDAPBaseDnDetailData {
	
	private Long id;
	private String searchBaseDn;
	transient private LDAPDSData ldapdsData;
	
	@Id
	@Column(name="BASEDNDETAILID")
	@GeneratedValue(generator="sequenceGenerator")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="SEARCHBASEDN")
	public String getSearchBaseDn() {
		return searchBaseDn;
	}
	
	public void setSearchBaseDn(String searchBaseDn) {
		this.searchBaseDn = searchBaseDn;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="LDAPDSID")
	public LDAPDSData getLdapdsData() {
		return ldapdsData;
	}
	
	public void setLdapdsData(LDAPDSData ldapdsData) {
		this.ldapdsData = ldapdsData;
	}
}
