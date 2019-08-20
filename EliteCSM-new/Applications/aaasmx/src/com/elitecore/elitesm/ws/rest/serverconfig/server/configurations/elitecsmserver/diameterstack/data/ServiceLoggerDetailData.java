package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder = {"serviceLoggerEnabled","logLevel","rollingType","rollingUnit","maxRolledUnit","compressRolledUnit",
		"logLocation","sysLogConfiguration"})
@ValidObject 
public class ServiceLoggerDetailData implements Validator{
	
	@NotEmpty(message="Service Logger Enable value must be specified")
	private String serviceLoggerEnabled;
	
	@NotEmpty(message="Log Level value must be specified")
	private String logLevel;
	
	@NotEmpty(message="Rolling Type value must be specified")
	private String rollingType;
	
	private String rollingUnit;
	private String maxRolledUnit;
	@NotEmpty(message="Compress Rolled Unit value must be specified")
	private String compressRolledUnit;
	
	private String logLocation;
	private SysLogConfigurationData sysLogConfiguration;
	
	
	public ServiceLoggerDetailData(){
		sysLogConfiguration = new SysLogConfigurationData();
		rollingType = EliteRollingFileLogger.TIME_BASED_ROLLING_TYPE+"";
		rollingUnit= EliteRollingFileLogger.TIME_BASED_ROLLING_EVERY_DAY+"";
		maxRolledUnit = "10";
		logLocation = "logs";
	}

	@XmlElement(name = "syslog")
	@Valid
	public SysLogConfigurationData getSysLogConfiguration() {
		return sysLogConfiguration;
	}
	public void setSysLogConfiguration(SysLogConfigurationData sysLogconfiguration) {
		this.sysLogConfiguration = sysLogconfiguration;
	}


	@XmlElement(name = "log-level")
	@Pattern(regexp=RestValidationMessages.REGEX_LOG_LEVEL,message="Invalid value of Log Level(OFF,WARN,ERROR,INFO,FATAL,DEBUG,TRACE,ALL)")
	public String getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel.toUpperCase();
	}
	@XmlElement(name = "rolling-type")
	@Pattern(regexp=RestValidationMessages.REGEX_ROLLING_TYPE,message="Invalid Rolling Type(Size-Based/Time-Based)")
	public String getRollingType() {
		return rollingType;
	}
	public void setRollingType(String rollingType) {
		this.rollingType = rollingType;
	}
	@XmlElement(name = "rolling-unit")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC_POSITIVE,message="Rolling Unit must be numeric")
	public String getRollingUnit() {
		return rollingUnit;
	}
	public void setRollingUnit(String rollingUnit) {
		this.rollingUnit = rollingUnit;
	}
	@XmlElement(name = "max-rolled-unit")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC_POSITIVE,message="Max Rolled Unit must be numeric")
	public String getMaxRolledUnit() {
		return maxRolledUnit;
	}
	public void setMaxRolledUnit(String maxRolledUnit) {
		this.maxRolledUnit = maxRolledUnit;
	}
	@XmlElement(name = "compress-rolled-unit")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE,message = "Invalid value of Compressed Rolled Unit(true/false)")
	public String getCompressRolledUnit() {
		return compressRolledUnit;
	}
	public void setCompressRolledUnit(String bCompressRolledUnit) {
		this.compressRolledUnit = bCompressRolledUnit.toLowerCase();
	}

	@XmlElement(name = "service-logger-enabled")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE,message = "Invalid value of Service Logger Enabled(true/false)")
	public String getServiceLoggerEnabled() {
		return serviceLoggerEnabled;
	}
	public void setServiceLoggerEnabled(String serviceLoggerEnabled) {
		this.serviceLoggerEnabled = serviceLoggerEnabled.toLowerCase();
	}
	
	@XmlElement(name = "location")
	public String getLogLocation() {
		return logLocation;
	}
	public void setLogLocation(String logLocation) {
		this.logLocation = logLocation;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		if(Strings.isNullOrBlank(rollingType) == false){
			
			if(RestValidationMessages.TIME_BASED.equals(rollingType)){
				rollingType = "1";
			}else if(RestValidationMessages.SIZE_BASED.equals(rollingType)){
				rollingType = "2" ;
			}
		}
		return true;
	}
}
