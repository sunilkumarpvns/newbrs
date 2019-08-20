package com.elitecore.config.core;


import com.elitecore.config.exception.LoadConfigurationException;

/**
 * 
 * @author narendra.pathai
 *
 */
public abstract class Reader {
	public void injectConfigurationContext(Configurable configurable, ConfigurationContext configurationContext) throws IllegalArgumentException, IllegalAccessException{
		configurable.setConfigurationContext(configurationContext);
	}
	
	public abstract Configurable read(ConfigurationContext configurationContext, Class<? extends Configurable> configurableClass) throws LoadConfigurationException;
	public abstract void reload(ConfigurationContext configurationContext, Configurable configurable) throws LoadConfigurationException;
}
