package com.elitecore.elitesm.ws.ytl.cal.data;

import javax.xml.bind.annotation.XmlElement;

public class FANode {

	private String sent;
	private String pseudoIdentifier;
	private String rmsClusterId;
	private ErrorMessage error;
	
	
	@XmlElement(name = "sent")
	public String getSent() {
		return sent;
	}
	
	public void setSent(String sent) {
		this.sent = sent;
	}
	
	@XmlElement(name = "pseudo-identifier")
	public String getPseudoIdentifier() {
		return pseudoIdentifier;
	}
	
	public void setPseudoIdentifier(String pseudoIdentifier) {
		this.pseudoIdentifier = pseudoIdentifier;
	}
	
	@XmlElement(name = "rms-cluster-id")
	public String getRmsClusterId() {
		return rmsClusterId;
	}
	
	public void setRmsClusterId(String rmsClusterId) {
		this.rmsClusterId = rmsClusterId;
	}
	
	@XmlElement(name = "error")
	public ErrorMessage getError() {
		return error;
	}
	
	public void setError(ErrorMessage error) {
		this.error = error;
	}
}
