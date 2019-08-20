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

@Entity
@Table(name="TBLMDBSPINTERFACE")
	public class DBSPInterfaceData {

	private Long id;
	private DatabaseDSData databaseDSData;
	private String tableName;
	private String identityField;
	private Integer queryTimeout;
	private Integer maxQueryTimeoutCount;
	private List<DBFieldMappingData> dbFieldMappingDataList;
	private Long driverInstanceId;


	
	@Id
	@Column(name="DBSPINTERFACEID")
	@GeneratedValue(generator="sequenceGenerator")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DATABASEDSID")
	public DatabaseDSData getDatabaseDSData() {
		return databaseDSData;
	}
	
	public void setDatabaseDSData(DatabaseDSData databaseDSData) {
		this.databaseDSData = databaseDSData;
	}
	
	@Column(name="TABLENAME")
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@Column(name="IDENTITYFIELD")
	public String getIdentityField() {
		return identityField;
	}
	
	public void setIdentityField(String identityField) {
		this.identityField = identityField;
	}
	
	@Column(name="DBQUERYTIMEOUT")
	public Integer getQueryTimeout() {
		return queryTimeout;
	}
	
	public void setQueryTimeout(Integer queryTimeout) {
		this.queryTimeout = queryTimeout;
	}
	
	@Column(name="MAXQUERYTIMEOUTCOUNT")
	public Integer getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	
	public void setMaxQueryTimeoutCount(Integer maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "dbspInterfaceData")
	@Fetch(FetchMode.SUBSELECT)
	public List<DBFieldMappingData> getDbFieldMappingDataList() {
		return dbFieldMappingDataList;
	}
	
	public void setDbFieldMappingDataList(List<DBFieldMappingData> dbFieldMappingDataList) {
		this.dbFieldMappingDataList = dbFieldMappingDataList;
	}


	@Column(name="DRIVERINSTANCEID")
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}

	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
}
