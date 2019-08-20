package com.elitecore.aaa.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class FileAlertConfiguration{

	private String FILE_ALERT_LISTENER_ID = "ALT0001";
	private String fileName;
	private String name;
	private int rollingType;
	private int rollingUnit;
	private int maxRollingUnit;
	private boolean compRollingUnit;
	private String listenerId;
	private List<String> enabledId;
	private boolean repeatedMessageReduction = true;

	public FileAlertConfiguration(){
		//required By Jaxb.
		enabledId = new ArrayList<String>();
	}

	@XmlElementWrapper(name = "enabled")
	@XmlElement(name = "id",type = String.class)
	public List<String> getEnabledId() {
		return enabledId;
	}

	public void setEnabledId(List<String> enabledId) {
		this.enabledId = enabledId;
	}

	public void setName(String name){
		this.name = name;
	}

	@XmlElement(name = "name",type = String.class)
	public String getName(){
		return name;
	}

	@XmlElement(name = "file-name",type =String.class)
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@XmlElement(name = "rolling-type",type = int.class)
	public int getRollingType() {
		return rollingType;
	}

	public void setRollingType(int rollingType) {
		this.rollingType = rollingType;
	}


	@XmlElement(name = "rolling-unit",type = int.class)
	public int getRollingUnit() {
		return rollingUnit;
	}

	public void setRollingUnit(int rollingUnit) {
		this.rollingUnit = rollingUnit;
	}

	@XmlElement(name = "maximunm-rolling-unit",type = int.class)
	public int getMaxRollingUnit() {
		return maxRollingUnit;
	}

	public void setMaxRollingUnit(int maxRollingUnit) {
		this.maxRollingUnit = maxRollingUnit;
	}

	@XmlElement(name = "compress-rolling-unit",type = boolean.class)
	public boolean getIsCompRollingUnit() {
		return compRollingUnit;
	}

	public void setIsCompRollingUnit(boolean compRollingUnit) {
		this.compRollingUnit = compRollingUnit;
	}	

	//@XmlElement(name = "type",type = String.class)
	@XmlTransient
	public String getListenerType() {		
		return FILE_ALERT_LISTENER_ID;
	}
	public void setListenerType(String listnerType){
		this.FILE_ALERT_LISTENER_ID = "ALT0001";
	}

	@XmlElement(name = "listener-id",type = String.class)
	public String getListenerId() {
		return this.listenerId;
	}
	public void setListenerId(String listenerId) {
		this.listenerId = listenerId;
	}
	
	public void setIsRepeatedMessageReduction(boolean repeatedMessageReduction) {
		this.repeatedMessageReduction = repeatedMessageReduction;
	}
	
	@XmlElement(name = "repeated-message-reduction", type = Boolean.class, defaultValue = "true")
	public boolean getIsRepeatedMessageReduction() {
		return this.repeatedMessageReduction;
	}
}
