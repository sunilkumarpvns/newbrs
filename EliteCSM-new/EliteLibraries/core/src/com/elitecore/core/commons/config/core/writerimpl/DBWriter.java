package com.elitecore.core.commons.config.core.writerimpl;


import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.ConfigurationContext;
import com.elitecore.core.commons.config.core.Writer;
import com.elitecore.core.commons.config.core.annotations.DBWrite;
import com.elitecore.core.commons.config.util.ReflectionUtil;
import com.elitecore.core.commons.configuration.StoreConfigurationException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class DBWriter extends Writer{

	@Override
	public void write(ConfigurationContext configurationContext, Configurable configurable) throws StoreConfigurationException {
		try{
			ReflectionUtil.getMethodAnnotatedWith(configurable.getClass(), DBWrite.class).invoke(configurable, new Object[]{});
		}catch(Throwable ex){
			throw new StoreConfigurationException("Error in writing configuration. " + ex.getMessage());
		}
	}

}
