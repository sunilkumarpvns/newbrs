package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.eliteaaaserver.data;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data.SysLogConfigurationData;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder = {"logLevel","rollingType","rollingUnit","maxRolledUnit","compressRolledUnit","sysLogConfiguration"})
@ValidObject
public class LoggerDetailData implements Validator{

	private SysLogConfigurationData sysLogConfiguration;
	@NotEmpty(message="Log Level value must be specified in Logging")
	private String logLevel;
	
	@NotEmpty(message="Rolling Type value must be specified in Logging")
	private String rollingType;
	
	private String rollingUnit;
	private String maxRolledUnit;
	
	@NotEmpty(message="Compress Rolled Unit value must be specified in Logging")
	private String compressRolledUnit;

	public LoggerDetailData(){
		sysLogConfiguration = new SysLogConfigurationData();
		rollingUnit= EliteRollingFileLogger.TIME_BASED_ROLLING_EVERY_DAY+"";
		maxRolledUnit = "10";
	}

	
	@XmlElement(name = "syslog")
	@Valid
	public SysLogConfigurationData getSysLogConfiguration() {
		return sysLogConfiguration;
	}
	public void setSysLogConfiguration(SysLogConfigurationData sysLogconfiguration) {
		this.sysLogConfiguration = sysLogconfiguration;
	}


	@Reloadable(type=String.class)
	@XmlElement(name = "log-level")
	@Pattern(regexp=RestValidationMessages.REGEX_LOG_LEVEL,message="Invalid value of Log Level(OFF,WARN,ERROR,INFO,FATAL,DEBUG,TRACE,ALL) in Logging")
	public String getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel.toUpperCase();
	}
	
	@XmlElement(name = "rolling-type")
	@Pattern(regexp=RestValidationMessages.REGEX_ROLLING_TYPE,message="Invalid Rolling Type(Size-Based/Time-Based) in Logging")
	public String getRollingType() {
		return rollingType;
	}
	public void setRollingType(String rollingType) {
		this.rollingType = rollingType;;
	}
	
	@XmlElement(name = "rolling-unit")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC_POSITIVE,message="Rolling Unit must be numeric in Logging")
	public String getRollingUnit() {
		return rollingUnit;
	}
	public void setRollingUnit(String rollingUnit) {
		this.rollingUnit = rollingUnit;
	}
	
	@XmlElement(name = "max-rolled-unit")
	@Pattern(regexp=RestValidationMessages.REGEX_NUMERIC_POSITIVE,message="Max Rolled Unit must be numeric in Logging")
	public String getMaxRolledUnit() {
		return maxRolledUnit;
	}
	public void setMaxRolledUnit(String maxRolledUnit) {
		this.maxRolledUnit = maxRolledUnit;
	}
	
	@XmlElement(name = "compress-rolled-unit")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE,message = "Invalid value of Compress Rolled Unit(true/false) in Logging")
	public String getCompressRolledUnit() {
		return compressRolledUnit;
	}
	public void setCompressRolledUnit(String compressRolledUnit) {
		this.compressRolledUnit = compressRolledUnit.toLowerCase();
	}


	@Override
	public boolean validate(ConstraintValidatorContext context) {
		if (RestValidationMessages.TIME_BASED.equals(rollingType)) {
			rollingType = "1";
		} else if (RestValidationMessages.SIZE_BASED.equals(rollingType)) {
			rollingType = "2";
		}
		return true;
	}
}
