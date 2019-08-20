package com.elitecore.aaa.radius.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.annotations.Reloadable;

@XmlType(propOrder={})
public class DynAuthDBScanDetail {

	 private boolean isEnabled;
	 private String dataSourceName;
	 private int maxRecordPerScan=1000;
	 private long scanningPeriod = 300000; // 1000 * 60 * 5
	 private long delatBetweenSubsequentRequest = 10;
	 
	 public DynAuthDBScanDetail() {
    }
	@XmlElement(name="enabled",type=boolean.class,defaultValue="false")
	public boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@XmlElement(name="datasource-name",type=String.class)
	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	@Reloadable(type=Integer.class)
	@XmlElement(name="max-records-per-scan",type=int.class)
	public int getMaxRecordPerScan() {
		return maxRecordPerScan;
	}

	public void setMaxRecordPerScan(int maxRecordPerScan) {
		this.maxRecordPerScan = maxRecordPerScan;
	}

	@Reloadable(type=Long.class)
	@XmlElement(name="scanning-period",type=long.class)
	public long getScanningPeriod() {
		return scanningPeriod;
	}

	public void setScanningPeriod(long scanningPeriod) {
		this.scanningPeriod = scanningPeriod;
	}

	@Reloadable(type=Long.class)
	@XmlElement(name="delay-between-subsequent-requests",type=long.class)	
	public long getDelatBetweenSubsequentRequest() {
		return delatBetweenSubsequentRequest;
	}

	public void setDelatBetweenSubsequentRequest(long delatBetweenSubsequentRequest) {
		this.delatBetweenSubsequentRequest = delatBetweenSubsequentRequest;
	}
	
	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("DB Scan Detail");
		out.println("    Enabled                           = "+isEnabled);
		out.println("    DatasourceName                    = "+getDataSourceName());
		out.println("    Max Records Per Scan              = "+getMaxRecordPerScan());
		out.println("    Scanning Period      			   = "+getScanningPeriod());
		out.println("    Delay Between Subsequent Requests = "+getDelatBetweenSubsequentRequest());
		out.close();
		return stringBuffer.toString();
	}

}
