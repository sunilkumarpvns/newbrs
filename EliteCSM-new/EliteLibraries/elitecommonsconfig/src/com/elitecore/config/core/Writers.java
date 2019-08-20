package com.elitecore.config.core;


import com.elitecore.config.core.factory.WriterFactory;
import com.elitecore.config.exception.StoreConfigurationException;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author narendra.pathai
 *
 */
class Writers {
	
	private Map<Class<? extends Writer>, Writer> writerClassToWriter;
	private WriterFactory factory;

	Writers(WriterFactory factory) {
		this.factory = factory;
		this.writerClassToWriter = new HashMap<>();

	}
	
	void write(ConfigurationContext configurationContext, Configurable configurable, Class<? extends Writer> writerClass) throws StoreConfigurationException{
		try {
			Writer writer = getWriter(writerClass);
			writer.write(configurationContext, configurable);
		} catch(StoreConfigurationException ex){
			throw ex;
		} catch (Throwable t) {
			throw new StoreConfigurationException(t);
		}
	}
	
	private boolean isAlreadyCreated(Class<? extends Writer> writerClass){
		return writerClassToWriter.keySet().contains(writerClass);
	}
	
	private Writer getWriter(Class<? extends Writer> writerClass) throws Exception{
		Writer writer;
		if(isAlreadyCreated(writerClass) == false){
			writer = factory.createInstance(writerClass);
			writerClassToWriter.put(writerClass, writer);

		}else{
			writer = writerClassToWriter.get(writerClass);
		}
		return writer;
	}


}
