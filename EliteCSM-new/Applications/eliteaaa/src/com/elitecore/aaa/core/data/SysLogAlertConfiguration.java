package com.elitecore.aaa.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class SysLogAlertConfiguration {

	private String listenerId;
	private String syslogHost;
	private String litenerName;
	private String facility;
	private String SYSLOG_ALERT_LISTENER_ID = "ALT0003";
	private List<String> enabeldId;
	private boolean repeatedMessageReduction = true;

	public SysLogAlertConfiguration(){
		//required by Jaxb.
		enabeldId = new ArrayList<String>();
	}

	@XmlElementWrapper(name = "enabled")
	@XmlElement(name = "id")
	public List<String> getEnabeldId() {
		return enabeldId;
	}
	public void setEnabeldId(List<String> enabeldId) {
		this.enabeldId = enabeldId;
	}

	@XmlElement(name = "address",type = String.class)
	public String getSyslogHost() {
		return syslogHost;
	}

	public void setSyslogHost(String syslogHost) {
		this.syslogHost = syslogHost;
	}

	@XmlElement(name = "name",type = String.class)
	public String getLitenerName() {
		return litenerName;
	}

	public void setLitenerName(String litenerName) {
		this.litenerName = litenerName;
	}
	@XmlElement(name = "facility",type = String.class)
	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public void setListenerId(String listenerId) {
		this.listenerId = listenerId;
	}

	@XmlElement(name = "listener-id",type = String.class)
	public String getListenerId() {
		return this.listenerId;
	}

	@XmlTransient
	public String getListenerType() {
		return SYSLOG_ALERT_LISTENER_ID;
	}
	public void setListenerType(String listnerType){
		this.SYSLOG_ALERT_LISTENER_ID = "ALT0003";
	}
	
	public void setIsRepeatedMessageReduction(boolean repeatedMessageReduction) {
		this.repeatedMessageReduction = repeatedMessageReduction;
	}
	
	@XmlElement(name = "repeated-message-reduction", type = Boolean.class, defaultValue = "true")
	public boolean getIsRepeatedMessageReduction() {
		return this.repeatedMessageReduction;
	}
}
