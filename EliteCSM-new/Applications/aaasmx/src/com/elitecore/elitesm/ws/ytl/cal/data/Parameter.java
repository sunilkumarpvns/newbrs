package com.elitecore.elitesm.ws.ytl.cal.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "parameter")
public class Parameter {
	
	private UserData user;
	private WimaxHotlining wimax;
	
	@XmlElement(name = "user")
	public UserData getUser() {
		return user;
	}
	
	public void setUser(UserData user) {
		this.user = user;
	}
	
	@XmlElement(name = "wimax-hotlining")
	public WimaxHotlining getWimax() {
		return wimax;
	}
	
	public void setWimax(WimaxHotlining wimax) {
		this.wimax = wimax;
	}
}