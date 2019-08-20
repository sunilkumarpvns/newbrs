package com.elitecore.core.commons.config.core;


import com.elitecore.core.commons.configuration.StoreConfigurationException;

/**
 * 
 * @author narendra.pathai
 *
 */
public abstract class Writer {
	
	public Writer() {
	}
	
	public abstract void write(ConfigurationContext configurationContext, Configurable configurable) throws StoreConfigurationException;
}
