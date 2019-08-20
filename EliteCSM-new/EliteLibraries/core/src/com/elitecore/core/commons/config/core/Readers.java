package com.elitecore.core.commons.config.core;


import com.elitecore.core.commons.config.core.factory.ReaderFactory;
import com.elitecore.core.commons.configuration.LoadConfigurationException;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author narendra.pathai
 *
 */
public class Readers {

	private Map<Class<? extends Reader>, Reader> readers;
	private ReaderFactory factory;

	public Readers(ReaderFactory factory) {
		this.factory = factory;
		this.readers = new HashMap<Class<? extends Reader>, Reader>();
	}

	Configurable read(ConfigurationContext configurationContext, Class<? extends Configurable> configurableClass, Class<? extends Reader> readerClass) throws LoadConfigurationException{
		Configurable configurable = getReader(readerClass).read(configurationContext, configurableClass);
		return configurable;
	}
	
	void reload(ConfigurationContext configurationContext, Configurable configurable, Class<? extends Reader> reloaderClass) throws LoadConfigurationException{
		getReader(reloaderClass).reload(configurationContext, configurable);
	}
	
	private boolean isAlreadyCreated(Class<? extends Reader> readerClass){
		return readers.keySet().contains(readerClass);
	}

	private Reader getReader(Class<? extends Reader> readerClass) throws LoadConfigurationException{
		try{
			Reader reader;
			if(isAlreadyCreated(readerClass) == false){
				reader = factory.createInstance(readerClass);
				readers.put(readerClass, reader);
			}else{
				reader = readers.get(readerClass);
			}
			return reader;
		}catch (Throwable ex) {
			throw new LoadConfigurationException(ex.getMessage());
		}
	}
}
