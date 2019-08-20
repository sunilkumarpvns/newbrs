package com.elitecore.corenetvertex.spr.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="TBLMLDAPFIELDMAP")
public class LDAPFieldMappingData {

	private Long id;
	private String logicalName;
	private String attribute;
	
	transient LDAPSPInterfaceData ldapspInterfaceData;

	@Id
	@Column(name="LDAPFIELDMAPID")
	@GeneratedValue(generator="sequenceGenerator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="LOGICALNAME")
	public String getLogicalName() {
		return logicalName;
	}

	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}

	@Column(name="LDAPATTRIBUTE")
	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="LDAPSPINTERFACEID")
	public LDAPSPInterfaceData getLdapspInterfaceData() {
		return ldapspInterfaceData;
	}

	public void setLdapspInterfaceData(LDAPSPInterfaceData ldapspInterfaceData) {
		this.ldapspInterfaceData = ldapspInterfaceData;
	}
}
