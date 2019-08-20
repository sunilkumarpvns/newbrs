package com.elitecore.config.core;


import com.elitecore.config.core.factory.ReaderFactory;
import com.elitecore.config.exception.LoadConfigurationException;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author narendra.pathai
 *
 */
public class Readers {

	private Map<Class<? extends Reader>, Reader> readerClassToReader;
	private ReaderFactory factory;

	public Readers(ReaderFactory factory) {
		this.factory = factory;
		this.readerClassToReader = new HashMap<Class<? extends Reader>, Reader>();
	}

	Configurable read(ConfigurationContext configurationContext, Class<? extends Configurable> configurableClass, Class<? extends Reader> readerClass) throws LoadConfigurationException{
		return  getReader(readerClass).read(configurationContext, configurableClass);
	}
	
	void reload(ConfigurationContext configurationContext, Configurable configurable, Class<? extends Reader> reloaderClass) throws LoadConfigurationException{
		getReader(reloaderClass).reload(configurationContext, configurable);
	}
	
	private boolean isAlreadyCreated(Class<? extends Reader> readerClass){
		return readerClassToReader.keySet().contains(readerClass);
	}

	private Reader getReader(Class<? extends Reader> readerClass) throws LoadConfigurationException{
		try{
			Reader reader;
			if(isAlreadyCreated(readerClass) == false){
				reader = factory.createInstance(readerClass);
				readerClassToReader.put(readerClass, reader);
			}else{
				reader = readerClassToReader.get(readerClass);
			}
			return reader;
		}catch (Exception ex) {
			throw new LoadConfigurationException(ex);
		}
	}
}
