package com.elitecore.netvertexsm.web.core.base;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="Base-Data")
@XmlAccessorType(XmlAccessType.NONE)
public abstract class BaseData{
	@XmlTransient private Timestamp createdDate;
	@XmlTransient private Timestamp modifiedDate;
	@XmlTransient private Long createdByStaffId;
	@XmlTransient private Long modifiedByStaffId;
	@XmlTransient private String clientIp;
	
	public Timestamp getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}
	
	public Timestamp getModifiedDate() {
		return modifiedDate;
	}
	
	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	public Long getCreatedByStaffId() {
		return createdByStaffId;
	}
	
	public void setCreatedByStaffId(Long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	
	public Long getModifiedByStaffId() {
		return modifiedByStaffId;
	}
	
	public void setModifiedByStaffId(Long modifiedByStaffId) {
		this.modifiedByStaffId = modifiedByStaffId;
	}
	
	public String getClientIp() {
		return clientIp;
	}
	
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
}