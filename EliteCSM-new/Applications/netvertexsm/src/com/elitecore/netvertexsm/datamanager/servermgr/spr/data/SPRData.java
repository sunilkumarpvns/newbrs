package com.elitecore.netvertexsm.datamanager.servermgr.spr.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;

@Entity
@Table(name = "TBLM_SPR")
public class SPRData extends ResourceData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String sprName;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	
	private Long driverInstanceId;
	
	private DriverInstanceData driverInstanceData;

	private Long databaseDSId;
	
	private DatabaseDSData databaseDSData;
	
	
	private long batchSize;
	
	private String clientIp;
	
	transient private String groups;
	
	private String alternateIdField;
	
	@Column(name = "NAME")
	public String getSprName() {
		return sprName;
	}
	public void setSprName(String sprName) {
		this.sprName = sprName;
	}
	
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name = "DATABASE_DS_ID")
	public Long getDatabaseDSId() {
		return databaseDSId;
	}
	public void setDatabaseDSId(Long databaseDsId) {
		this.databaseDSId = databaseDsId;
	}
	
	@Transient
	@Column(name = "DATABASE_DS_ID")
	@ManyToOne
	public DatabaseDSData getDatabaseDSData() {
		return databaseDSData;
	}
	public void setDatabaseDSData(DatabaseDSData databaseDSData) {
		this.databaseDSData = databaseDSData;
	} 
	
	@Column(name = "DRIVER_INSTANCE_ID")
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@Transient
	public DriverInstanceData getDriverInstanceData() {
		return driverInstanceData;
	}
	public void setDriverInstanceData(DriverInstanceData driverInstanceData) {
		this.driverInstanceData = driverInstanceData;
	}
	
	@Column(name = "BATCH_SIZE")
	public long getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(long batchSize) {
		this.batchSize = batchSize;
	}
	
	@Column(name = "CLIENT_IP")
	public String getClientIp() {
	    return clientIp;
	}
	public void setClientIp(String clientIp) {
	    this.clientIp = clientIp;
	}
	
	@Column(name="GROUPS")
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}

	@Transient
	public String getName() {
		return getSprName();
	}

	@Column(name = "ALTERNATE_ID_FIELD")
	public String getAlternateIdField() {
		return alternateIdField;
	}
	public void setAlternateIdField(String alternateIdField) {
		this.alternateIdField = alternateIdField;
	}

	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}
	
	
}