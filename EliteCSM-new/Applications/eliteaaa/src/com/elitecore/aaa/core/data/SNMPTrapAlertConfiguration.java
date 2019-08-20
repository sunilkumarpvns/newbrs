package com.elitecore.aaa.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class SNMPTrapAlertConfiguration {

	private String trapListnerId ;
	private int port ;
	private int trapVersion ;
	private String community ;
	private boolean advanceTrap = true;
	private String name;
	private String TRAP_ALERT_LISTENER_ID = "ALT0002";
	private List<String> enabledId;
	private String trapServerAddress;
	private String strIpAddress;
	private boolean repeatedMessageReduction = true;
	

	public SNMPTrapAlertConfiguration(){
		//required By Jaxb.
		enabledId = new ArrayList<String>();
	}
	@XmlTransient
	public String getIpAddress() {
		return strIpAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.strIpAddress = ipAddress;
	}

	@XmlElement(name ="trap-server-address",type = String.class)
	public String getTrapServerAddress() {
		return trapServerAddress;
	}

	public void setTrapServerAddress(String trapServer) {
		this.trapServerAddress = trapServer;
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

	@XmlElement(name = "advance-trap",type = boolean.class,defaultValue ="true")
	public boolean getIsAdvanceTrap() {
		return advanceTrap;
	}

	public void setIsAdvanceTrap(boolean advanceTrap) {
		this.advanceTrap = advanceTrap;
	}

	@XmlTransient
	public String getListenerType() {
		return TRAP_ALERT_LISTENER_ID;
	}
	public void setListenerType(String listnerType){
		this.TRAP_ALERT_LISTENER_ID = "ALT0002";
	}

	@XmlElement(name = "listener-id",type = String.class)
	public String getListenerId() {
		return trapListnerId;
	}
	public void setListenerId(String trapListnerId){
		this.trapListnerId = trapListnerId;
	}

	@XmlTransient
	public int getPort() {
		return port;
	}
	public void setPort(int port){
		this.port = port;
	}

	@XmlElement(name = "trap-version",type = int.class)
	public int getTrapVersion() {
		return trapVersion;
	}
	public void setTrapVersion(int trapVersion){
		this.trapVersion = trapVersion;
	}
	@XmlElement(name = "community",type = String.class)
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community){
		this.community = community;
	}
	
	public void setIsRepeatedMessageReduction(boolean repeatedMessageReduction) {
		this.repeatedMessageReduction = repeatedMessageReduction;
	}
	
	@XmlElement(name = "repeated-message-reduction", type = Boolean.class, defaultValue = "true")
	public boolean getIsRepeatedMessageReduction() {
		return this.repeatedMessageReduction;
	}
}
