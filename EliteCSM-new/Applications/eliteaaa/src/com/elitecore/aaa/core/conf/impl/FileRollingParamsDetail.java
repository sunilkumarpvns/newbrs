package com.elitecore.aaa.core.conf.impl;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.fileio.RollingTypeConstant;

public @XmlType(propOrder={})
class FileRollingParamsDetail{

	private String sequence;
	private boolean globalization;
	private String pattern;
	private int timeBaseRollingUnit;
	private int sizeBaseRollingUnit;
	private int recordBaseRollingUnit;
	private int timeBoundryRollingUnit;
	private Map<RollingTypeConstant, Integer> rollingTypeMap;
	
	public FileRollingParamsDetail() {
	}

	public void setGlobalization(boolean globalization) {
		this.globalization = globalization;
	}
	public void setSequenceRange(String sequence) {
		this.sequence = sequence;
	}

	@XmlElement(name ="globalization",type =boolean.class)
	public boolean getGlobalization() {
		return this.globalization;
	}

	@XmlElement(name ="sequence-range",type =String.class)
	public String getSequenceRange() {
		return this.sequence;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	@XmlElement(name ="sequence-position",type =String.class)
	public String getPattern() {
		return this.pattern;
	}

	@XmlElement(name ="timebase-rolling-unit",type =int.class)
	public int getTimeBaseRollingUnit(){
		return timeBaseRollingUnit;
	}
	public void setTimeBaseRollingUnit(int rollUnit){
		this.timeBaseRollingUnit = rollUnit;
	}
	
	@XmlElement(name ="sizebase-rolling-unit",type =int.class)
	public int getSizeBaseRollingUnit(){
		return sizeBaseRollingUnit;
	}
	public void setSizeBaseRollingUnit(int rollUnit){
		this.sizeBaseRollingUnit = rollUnit;
	}
	
	@XmlElement(name ="recordbase-rolling-unit",type =int.class)
	public int getRecordBaseRollingUnit(){
		return recordBaseRollingUnit;
	}
	public void setRecordBaseRollingUnit(int rollUnit){
		this.recordBaseRollingUnit = rollUnit;
	}
	
	@XmlElement(name ="timeboundry-rolling-unit",type =int.class)
	public int getTimeBoundryRollingUnit() {
		return timeBoundryRollingUnit;
	}
	public void setTimeBoundryRollingUnit(int timeBoundryRollUnit) {
		this.timeBoundryRollingUnit = timeBoundryRollUnit;
	}
	
	@XmlTransient
	public Map<RollingTypeConstant, Integer> getRollingTypeMap() {
		return rollingTypeMap;
	}
	
	public void setRollingTypeMap(Map<RollingTypeConstant, Integer> rollingTypeMap){
		this.rollingTypeMap = rollingTypeMap;
	}
}

