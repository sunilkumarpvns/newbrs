package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder={})
public class DBAccountingDriverDetails {
	
	private String dsName;
	private String dsType;
	private int dbQueryTimeout=2;
	
	private int maxQueryTimeoutCount=200;
	private String multivalDelimeter=";";
	
	public DBAccountingDriverDetails() {
		// TODO Auto-generated constructor stub
	}


	@XmlElement(name="database-datasource",type=String.class)
	public String getDsName() {
		return dsName;
	}

	@XmlElement(name="database-type",type=String.class)
	public String getDsType() {
		return dsType;
	}

	@XmlElement(name="db-query-timeout",type=int.class,defaultValue="2")
	public int getDbQueryTimeout() {
		return dbQueryTimeout;
	}

	@XmlElement(name="maximum-query-timeout-count",type=int.class,defaultValue="200")
	public int getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	
	@XmlElement(name="multiple-value-delimiter",type=String.class,defaultValue=";")
	public String getMultivalDelimeter() {
		return multivalDelimeter;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public void setDsType(String dsType) {
		this.dsType = dsType;
	}

	public void setDbQueryTimeout(int dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}

	public void setMaxQueryTimeoutCount(int maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}

	public void setMultivalDelimeter(String multivalDelimeter) {
		this.multivalDelimeter = multivalDelimeter;
	}

}
