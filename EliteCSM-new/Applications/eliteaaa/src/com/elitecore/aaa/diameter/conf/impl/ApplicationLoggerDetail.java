package com.elitecore.aaa.diameter.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.data.SysLogConfiguration;
import com.elitecore.core.util.logger.EliteRollingFileLogger;

@XmlType(propOrder={})
public class ApplicationLoggerDetail {
	private SysLogConfiguration sysLogconfiguration;
	private String logLevel = "INFO";
	private int rollingType = EliteRollingFileLogger.TIME_BASED_ROLLING_TYPE;
	private int rollingUnit= EliteRollingFileLogger.TIME_BASED_ROLLING_EVERY_DAY;
	private int maxRolledUnit = 10;
	private boolean bCompressRolledUnit;
	private String logLocation;
	private boolean bServiceLoggerEnabled;
	
	
	public ApplicationLoggerDetail(){
		//required By Jaxb.
		sysLogconfiguration = new SysLogConfiguration();
	}

	@Reloadable(type=SysLogConfiguration.class)
	@XmlElement(name = "syslog")
	public SysLogConfiguration getSysLogConfiguration() {
		return sysLogconfiguration;
	}
	public void setSysLogConfiguration(SysLogConfiguration sysLogconfiguration) {
		this.sysLogconfiguration = sysLogconfiguration;
	}


	@Reloadable(type=String.class)
	@XmlElement(name = "log-level",type = String.class,defaultValue ="INFO")
	public String getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
	@XmlElement(name = "rolling-type",type = int.class,defaultValue ="1")
	public int getLogRollingType() {
		return rollingType;
	}
	public void setLogRollingType(int rollingType) {
		this.rollingType = rollingType;
	}
	@XmlElement(name = "rolling-unit",type = int.class,defaultValue ="5")
	public int getLogRollingUnit() {
		return rollingUnit;
	}
	public void setLogRollingUnit(int rollingUnit) {
		this.rollingUnit = rollingUnit;
	}
	@XmlElement(name = "max-rolled-unit",type = int.class,defaultValue = "10")
	public int getLogMaxRolledUnits() {
		return maxRolledUnit;
	}
	public void setLogMaxRolledUnits(int maxRolledUnit) {
		this.maxRolledUnit = maxRolledUnit;
	}
	@XmlElement(name = "compress-rolled-unit",type = boolean.class)
	public boolean getIsbCompressRolledUnit() {
		return bCompressRolledUnit;
	}
	public void setIsbCompressRolledUnit(boolean bCompressRolledUnit) {
		this.bCompressRolledUnit = bCompressRolledUnit;
	}

	@XmlElement(name = "application-logger-enabled",type = boolean.class)
	public boolean getIsbServiceLoggerEnabled() {
		return bServiceLoggerEnabled;
	}
	public void setIsbServiceLoggerEnabled(boolean bServiceLoggerEnabled) {
		this.bServiceLoggerEnabled = bServiceLoggerEnabled;
	}
	@XmlElement(name = "location",type =String.class)
	public String getLogLocation() {
		return logLocation;
	}
	public void setLogLocation(String logLocation) {
		this.logLocation = logLocation;
	}


}
