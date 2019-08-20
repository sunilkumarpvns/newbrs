package com.elitecore.corenetvertex.spr.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.elitecore.corenetvertex.sm.ResourceData;


@Entity
@Table(name="TBLM_SPR")
public class SubscriberRepositoryData extends ResourceData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	transient private DatabaseDSData databaseDSData;
	transient private SPInterfaceData spInterfaceData;
	private Integer batchSize;
	private String alternateIdField;
	
	@Column(name="NAME")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="DESCRIPTION")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DRIVER_INSTANCE_ID")
	public SPInterfaceData getSpInterfaceData() {
		return spInterfaceData;
	}
	
	public void setSpInterfaceData(SPInterfaceData spInterfaceData) {
		this.spInterfaceData = spInterfaceData;
	}
	
	@Column(name="BATCH_SIZE")
	public Integer getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DATABASE_DS_ID")
	public DatabaseDSData getDatabaseDSData() {
		return databaseDSData;
	}

	public void setDatabaseDSData(DatabaseDSData databaseDSData) {
		this.databaseDSData = databaseDSData;
	}
	

	@Column(name="ALTERNATE_ID_FIELD")
	public String getAlternateIdField() {
		return alternateIdField;
	}

	public void setAlternateIdField(String alternateIdField) {
		this.alternateIdField = alternateIdField;
	}

	
	@Override
	@Column(name="GROUPS")
	public String getGroups() {
		return super.getGroups();
	}

	public void setGroups(String groups) {
	   super.setGroups(groups);
	}

	@Override
	@Transient
	public String getHierarchy() {
		return getId() + "<br>" + getName();
	}
	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}
}
