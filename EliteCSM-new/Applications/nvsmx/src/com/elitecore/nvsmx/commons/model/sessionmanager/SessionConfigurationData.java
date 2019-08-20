package com.elitecore.nvsmx.commons.model.sessionmanager;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.elitecore.corenetvertex.spr.data.DatabaseDSData;

@Entity
@Table(name = "TBLMSESSIONCONFIGURATION")
public class SessionConfigurationData {

	private Integer sessionConfigurationId;
	private DatabaseDSData dataSourceData;
	private DatabaseDSData secondaryDataSourceData;
	private String batchUpdate;
	private Integer batchSize;
	private Integer batchUpdateInterval;
	private Integer dbQueryTimeout;
	private String clientIP;
	private Integer dbScanInterval;
	
	@Id
	@Column(name = "SESSIONCONFID")
	public Integer getSessionConfigurationId() {
		return sessionConfigurationId;
	}
	
	public void setSessionConfigurationId(Integer sessionConfigurationId) {
		this.sessionConfigurationId = sessionConfigurationId;
	}
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "DATASOURCEID")
	public DatabaseDSData getDataSourceData() {
		return dataSourceData;
	}
	public void setDataSourceData(DatabaseDSData dataSourceData) {
		this.dataSourceData = dataSourceData;
	}
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "SECONDARYDATASOURCEID")
	public DatabaseDSData getSecondaryDataSourceData() {
		return secondaryDataSourceData;
	}
	public void setSecondaryDataSourceData(DatabaseDSData secondaryDataSourceData) {
		this.secondaryDataSourceData = secondaryDataSourceData;
	}
	
	@Column(name = "BATCHUPDATE")
	public String getBatchUpdate() {
		return batchUpdate;
	}

	public void setBatchUpdate(String batchUpdate) {
		this.batchUpdate = batchUpdate;
	}

	@Column(name = "BATCHSIZE")
	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	@Column(name = "BATCHUPDATEINTERVAL")
	public Integer getBatchUpdateInterval() {
		return batchUpdateInterval;
	}

	public void setBatchUpdateInterval(Integer batchUpdateInterval) {
		this.batchUpdateInterval = batchUpdateInterval;
	}

	@Column(name = "DBQUERYTIMEOUT")
	public Integer getDbQueryTimeout() {
		return dbQueryTimeout;
	}

	public void setDbQueryTimeout(Integer dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}

	@Column(name = "CLIENTIP")
	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	@Column(name = "DBSCANINTERVAL")
	public Integer getDbScanInterval() {
		return dbScanInterval;
	}

	public void setDbScanInterval(Integer dbScanInterval) {
		this.dbScanInterval = dbScanInterval;
	}
	
}
