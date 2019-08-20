package com.elitecore.aaa.radius.sessionx.conf;

import java.util.Map;

import com.elitecore.core.commons.configuration.LoadConfigurationException;

public interface SessionManagerConfiguration {
	
	public void readConfiguration() throws LoadConfigurationException;
	public Map<String, SessionManagerData> getSmConfigurationMap() ;
	public SessionManagerData getSessionManagerConfigById(String smInstanceId);
	public SessionManagerData getSessionManagerConfig(String name);
	
}
