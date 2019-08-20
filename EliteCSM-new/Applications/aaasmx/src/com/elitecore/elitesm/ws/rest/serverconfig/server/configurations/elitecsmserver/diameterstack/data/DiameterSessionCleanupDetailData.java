package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlType(propOrder={"sessionCleanupInterval","sessionTimeOut"})
public class DiameterSessionCleanupDetailData {
	
	private String sessionCleanupInterval;
	private String sessionTimeOut;
	
    public DiameterSessionCleanupDetailData() {
    	sessionCleanupInterval = "3600";
    	sessionTimeOut = "86400";
	}
    
	@XmlElement(name="session-cleanup-interval")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC,message="Diameter Session Cleanup Interval value must be numeric")
	public String getSessionCleanupInterval() {
		return sessionCleanupInterval;
	}
	public void setSessionCleanupInterval(String sessionCleanupInterval) {
		this.sessionCleanupInterval = sessionCleanupInterval;
	}

	@XmlElement(name="session-timeout")
	@Pattern(regexp = RestValidationMessages.REGEX_NUMERIC,message="Diameter Session Timeout value must be numeric")
	public String getSessionTimeOut() {
		return sessionTimeOut;
	}
	public void setSessionTimeOut(String sessionTimeOut) {
		this.sessionTimeOut = sessionTimeOut;
	}
	
}
