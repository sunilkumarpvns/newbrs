package com.elitecore.elitesm.datamanager.servermgr.transmapconf.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class TranslationMappingInstDetailRelData extends BaseData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String mappingInstanceId;
	private String detailId;
	private Long orderNumber;
	
	public String getMappingInstanceId() {
		return mappingInstanceId;
	}
	public void setMappingInstanceId(String mappingInstanceId) {
		this.mappingInstanceId = mappingInstanceId;
	}
	public String getDetailId() {
		return detailId;
	}
	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}
	
	@XmlTransient
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
}

