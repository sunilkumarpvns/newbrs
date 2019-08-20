package com.elitecore.elitesm.ws.ytl.cal.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "aaa")
public class AAAData {
	
	private String id;
	private String name;
	private String dmUrl;
	private String asrUrl;
	
	@XmlElement(name = "id")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "dm-url")
	public String getDmUrl() {
		return dmUrl;
	}
	
	public void setDmUrl(String dmUrl) {
		this.dmUrl = dmUrl;
	}
	
	@XmlElement(name = "asr-url")
	public String getAsrUrl() {
		return asrUrl;
	}
	
	public void setAsrUrl(String asrUrl) {
		this.asrUrl = asrUrl;
	}
}