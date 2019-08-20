package com.elitecore.core.server.data;

import java.io.Serializable;

public class ServerReloadResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9211550931255107120L;
	
	boolean bReload;
	String errorMessage;
	String configurationName;
	
	public boolean isReload() {
		return bReload;
	}
	public void setReload(boolean reload) {
		bReload = reload;
	}
	public String getConfigurationName() {
		return configurationName;
	}
	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
