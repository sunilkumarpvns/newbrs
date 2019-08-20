package com.elitecore.corenetvertex.spr.data;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name="TBLMLDAPDS")
public class LDAPDSData {

	private Long id;
	private String name;
	private String address;
	private Long timeout;
	private Long sizeLimit;
	private String administrator;
	private String password;
	private String userdnprefix;
	private Integer maximumpool;
	private Integer minimumPool;
	private String commonstatusid;
	private Long statusCheckDuaration;
	private List<LDAPBaseDnDetailData> ldapBaseDnDetailDatas;
	
	@Id
	@Column(name="LDAPDSID")
	@GeneratedValue(generator="sequenceGenerator")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="NAME")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="ADDRESS")
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Column(name="TIMEOUT")
	public Long getTimeout() {
		return timeout;
	}
	
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	
	@Column(name="LDAPSIZELIMIT")
	public Long getSizeLimit() {
		return sizeLimit;
	}
	
	public void setSizeLimit(Long sizeLimit) {
		this.sizeLimit = sizeLimit;
	}
	
	@Column(name="ADMINISTRATOR")
	public String getAdministrator() {
		return administrator;
	}
	
	public void setAdministrator(String administrator) {
		this.administrator = administrator;
	}
	
	@Column(name="PASSWORD")	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name="USERDNPREFIX")
	public String getUserdnprefix() {
		return userdnprefix;
	}
	
	public void setUserdnprefix(String userdnprefix) {
		this.userdnprefix = userdnprefix;
	}
	
	@Column(name="MAXIMUMPOOL")
	public Integer getMaximumpool() {
		return maximumpool;
	}
	
	public void setMaximumpool(Integer maximumpool) {
		this.maximumpool = maximumpool;
	}
	
	@Column(name="MINIMUMPOOL")
	public Integer getMinimumPool() {
		return minimumPool;
	}
	
	public void setMinimumPool(Integer minimumPool) {
		this.minimumPool = minimumPool;
	}
	
	@Column(name="COMMONSTATUSID")
	public String getCommonstatusid() {
		return commonstatusid;
	}
	
	public void setCommonstatusid(String commonstatusid) {
		this.commonstatusid = commonstatusid;
	}
	
	@Column(name="STATUSCHECKDURATION")
	public Long getStatusCheckDuaration() {
		return statusCheckDuaration;
	}
	
	public void setStatusCheckDuaration(Long statusCheckDuaration) {
		this.statusCheckDuaration = statusCheckDuaration;
	}
	
	@OneToMany(fetch=FetchType.EAGER, mappedBy="ldapdsData")
	@Fetch(FetchMode.SUBSELECT)
	public List<LDAPBaseDnDetailData> getLdapBaseDnDetailDatas() {
		return ldapBaseDnDetailDatas;
	}
	
	public void setLdapBaseDnDetailDatas(List<LDAPBaseDnDetailData> ldapBaseDnDetailDatas) {
		this.ldapBaseDnDetailDatas = ldapBaseDnDetailDatas;
	}
}
