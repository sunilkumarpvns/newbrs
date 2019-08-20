package com.elitecore.config.core;


import com.elitecore.config.exception.StoreConfigurationException;

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
