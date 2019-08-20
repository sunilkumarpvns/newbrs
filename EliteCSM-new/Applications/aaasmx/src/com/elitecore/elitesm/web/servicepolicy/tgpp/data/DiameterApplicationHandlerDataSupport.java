package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import javax.xml.bind.annotation.XmlElement;

public abstract class DiameterApplicationHandlerDataSupport implements DiameterApplicationHandlerData {

	private String enabled;
	
	private String handlerName;
	
	@XmlElement(name = "enabled")
	public String getEnabled() {;
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	@XmlElement(name = "handler-name", type = String.class)
	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}
}
