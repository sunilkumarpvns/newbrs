package com.elitecore.config.core.writerimpl;


import com.elitecore.config.core.Configurable;
import com.elitecore.config.core.ConfigurationContext;
import com.elitecore.config.core.Writer;
import com.elitecore.config.core.annotations.DBWrite;
import com.elitecore.config.exception.StoreConfigurationException;
import com.elitecore.config.util.ReflectionUtil;

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
			throw new StoreConfigurationException("Error in writing configuration. " + ex.getMessage(), ex);
		}
	}

}
