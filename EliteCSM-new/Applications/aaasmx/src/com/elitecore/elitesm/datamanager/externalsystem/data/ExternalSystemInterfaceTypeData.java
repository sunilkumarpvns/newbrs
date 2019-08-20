package com.elitecore.elitesm.datamanager.externalsystem.data;

import java.io.Serializable;
import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class ExternalSystemInterfaceTypeData extends BaseData implements IExternalSystemInterfaceTypeData,Serializable {

	private long esiTypeId;
	private String name;
	private String displayName ;
	private long serialNo ;
	private String alias;
	private String description;
	private String status;
	
	private Set esiInstanceDetail;

	public Set getEsiInstanceDetail() {
		return esiInstanceDetail;
	}

	public void setEsiInstanceDetail(Set esiInstanceDetail) {
		this.esiInstanceDetail = esiInstanceDetail;
	}

	public long getEsiTypeId() {
		return esiTypeId;
	}

	public void setEsiTypeId(long esiTypeId) {
		this.esiTypeId = esiTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public long getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(long serialNo) {
		this.serialNo = serialNo;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}


