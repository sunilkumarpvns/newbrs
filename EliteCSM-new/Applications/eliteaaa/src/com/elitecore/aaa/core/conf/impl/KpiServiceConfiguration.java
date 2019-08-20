package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class KpiServiceConfiguration {
	
	private long dumpInterval = 60;			//	in seconds
	private long queryInterval = 10;			//	in seconds
	private int batchSize = 200;
	private int maxNoOfThreads = 15;
	private String driverClassName = "oracle.jdbc.driver.OracleDriver";
	private String dsName;
	
	@XmlElement(name = "batch-interval", type = Long.class , defaultValue = "60")
	public long getDumpInterval() {
		return this.dumpInterval;
	}

	public int getMaxNoOfThreads() {
		return this.maxNoOfThreads;
	}

	@XmlElement(name = "kpi-query-interval" , type = Long.class , defaultValue = "10")
	public long getQueryInterval() {
		return this.queryInterval;
	}

	@XmlElement(name = "batch-size", type = int.class , defaultValue = "200")
	public int getBatchSize() {
		return this.batchSize;
	}

	public void setDumpInterval(long dumpInterval) {
		this.dumpInterval = dumpInterval;
	}

	public void setQueryInterval(long queryInterval) {
		this.queryInterval = queryInterval;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void setMaxNoOfThreads(int maxNoOfThreads) {
		this.maxNoOfThreads = maxNoOfThreads;
	}
	
	public String getDBDriverClass() {
		return this.driverClassName;
	}

	@XmlElement(name = "datasource-name", type = String.class)
	public String getDSName() {
		return dsName;
	}

	public void setDSName(String dsName) {
		this.dsName = dsName;
	}
}
