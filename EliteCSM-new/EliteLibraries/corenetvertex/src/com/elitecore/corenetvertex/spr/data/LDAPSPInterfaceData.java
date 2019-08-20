package com.elitecore.corenetvertex.spr.data;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name="com.elitecore.corenetvertex.spr.data.LDAPSPInterfaceData")
@Table(name="TBLMLDAPSPINTERFACE")
public class LDAPSPInterfaceData {
	
	private Long id;
	private String expiryDatePattern;
	private Integer passwordDecryptType;
	private Long maxQueryTimeoutCount;
	private LDAPDSData ldapdsData;
	private List<LDAPFieldMappingData> ldapFieldMappingDatas;
	private Long driverInstanceId;
	
	@Id
	@Column(name="LDAPSPINTERFACEID")
	@GeneratedValue(generator="sequenceGenerator")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="EXPIRYDATE_PATTERN")
	public String getExpiryDatePattern() {
		return expiryDatePattern;
	}
	
	
	public void setExpiryDatePattern(String expiryDatePattern) {
		this.expiryDatePattern = expiryDatePattern;
	}
	
	@Column(name="PASSWORD_DECRYPT_TYPE")
	public Integer getPasswordDecryptType() {
		return passwordDecryptType;
	}
	
	public void setPasswordDecryptType(Integer passwordDecryptType) {
		this.passwordDecryptType = passwordDecryptType;
	}
	
	@Column(name="QUERY_MAX_EXEC_TIME")
	public Long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	
	public void setMaxQueryTimeoutCount(Long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="LDAPDSID")
	public LDAPDSData getLdapdsData() {
		return ldapdsData;
	}
	
	public void setLdapdsData(LDAPDSData ldapdsData) {
		this.ldapdsData = ldapdsData;
	}
	
	@OneToMany(fetch=FetchType.EAGER, mappedBy="ldapspInterfaceData")
	@Fetch(FetchMode.SUBSELECT)
	public List<LDAPFieldMappingData> getLdapFieldMappingDatas() {
		return ldapFieldMappingDatas;
	}
	
	public void setLdapFieldMappingDatas(List<LDAPFieldMappingData> ldapFieldMappingDatas) {
		this.ldapFieldMappingDatas = ldapFieldMappingDatas;
	}

	@Column(name = "DRIVERINSTANCEID")
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}

	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
}