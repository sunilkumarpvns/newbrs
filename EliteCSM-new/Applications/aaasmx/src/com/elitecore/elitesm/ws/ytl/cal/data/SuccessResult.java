package com.elitecore.elitesm.ws.ytl.cal.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "result")
public class SuccessResult {
	
	private ResponseUser respUser;
	private WimaxHotlining wimax;
	
	@XmlElement(name = "user")
	public ResponseUser getRespUser() {
		return respUser;
	}

	public void setRespUser(ResponseUser respUser) {
		this.respUser = respUser;
	}
	
	@XmlElement(name = "wimax-hotlining")
	public WimaxHotlining getWimax() {
		return wimax;
	}
	
	public void setWimax(WimaxHotlining wimax) {
		this.wimax = wimax;
	}
}
