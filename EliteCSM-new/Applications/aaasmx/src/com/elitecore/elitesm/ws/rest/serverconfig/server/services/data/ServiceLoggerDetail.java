package com.elitecore.elitesm.ws.rest.serverconfig.server.services.data;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;

@XmlRootElement(name = "logging")
@XmlType(propOrder = {"bServiceLoggerEnabled", "logLevel", "rollingType", "rollingUnit", "maxRolledUnit", 
		"bCompressRolledUnit", "logLocation", "sysLogConfiguration"})
public class ServiceLoggerDetail {
	
	@Valid
	private SysLogConfiguration sysLogConfiguration;
	
	@NotEmpty(message = "Log Level must be specified. It can be OFF, WARN, ERROR, INFO, FATAL, DEBUG, TRACE OR ALL.")
	@Pattern(regexp = RestValidationMessages.LOGLEVEL_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Log Level. It can be OFF, WARN, ERROR, INFO, FATAL, DEBUG, TRACE OR ALL." )
	private String logLevel;
	
	@NotEmpty(message = "Rolling Type must be specified. It can be Time-Based or Size-Based only.")
	@Pattern(regexp = RestValidationMessages.ROLLING_TYPE_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Rolling Type. It can be Time-Based or Size-Based only.")
	private String rollingType;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_DIGIT_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Rolling Unit")
	private String rollingUnit;
	
	@Pattern(regexp = RestValidationMessages.PARAMETER_DIGIT_REGEX, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Max Rolled Unit")
	private String maxRolledUnit;
	
	@NotEmpty(message = "Compress Rolled Unit  must be specified. It can be true or false.")
	@Pattern(regexp = RestValidationMessages.TRUE_FALSE_WITH_EMPTY, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Compress Rolled Unit. It can be true or false." )
	private String bCompressRolledUnit;
	
	private String logLocation;
	
	@NotEmpty(message = "Service Logger Enabled must be specified. It can be true or false.")
	@Pattern(regexp = RestValidationMessages.TRUE_FALSE_WITH_EMPTY, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Service Logger Enabled. It can be true or false." )
	private String bServiceLoggerEnabled;

	public ServiceLoggerDetail() {
		//required By Jaxb.
		sysLogConfiguration = new SysLogConfiguration();
		//setLogLevel("INFO");
		//setRollingType("Time-Based");
		setRollingUnit("5");
		setMaxRolledUnit("10");
	}

	@XmlElement(name = "syslog")
	public SysLogConfiguration getSysLogConfiguration() {
		return sysLogConfiguration;
	}
	public void setSysLogConfiguration(SysLogConfiguration sysLogconfiguration) {
		this.sysLogConfiguration = sysLogconfiguration;
	}

	@XmlElement(name = "log-level")
	public String getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
	
	@XmlElement(name = "rolling-type")   
	public String getRollingType() {
		return rollingType;
	}
	public void setRollingType(String rollingType) {
		this.rollingType = rollingType;
	}
	
	@XmlElement(name = "rolling-unit")
	public String getRollingUnit() {
		return rollingUnit;
	}
	public void setRollingUnit(String rollingUnit) {
		this.rollingUnit = rollingUnit;
	}

	@XmlElement(name = "max-rolled-unit")
	public String getMaxRolledUnit() {
		return maxRolledUnit;
	}
	public void setMaxRolledUnit(String maxRolledUnit) {
		this.maxRolledUnit = maxRolledUnit;
	}
	
	@XmlElement(name = "compress-rolled-unit")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getbCompressRolledUnit() {
		return bCompressRolledUnit;
	}
	public void setbCompressRolledUnit(String bCompressRolledUnit) {
		this.bCompressRolledUnit = bCompressRolledUnit;
	}
	
	@XmlElement(name = "service-logger-enabled")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getbServiceLoggerEnabled() {
		return bServiceLoggerEnabled;
	}
	public void setbServiceLoggerEnabled(String bServiceLoggerEnabled) {
		this.bServiceLoggerEnabled = bServiceLoggerEnabled;
	}
	
	@XmlElement(name = "location")
	public String getLogLocation() {
		return logLocation;
	}
	public void setLogLocation(String logLocation) {
		this.logLocation = logLocation;
	}

}
