package com.elitecore.core.commons.config.core.readerimpl;


import java.lang.reflect.InvocationTargetException;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.ConfigurationContext;
import com.elitecore.core.commons.config.core.Reader;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.util.ReflectionUtil;
import com.elitecore.core.commons.configuration.LoadConfigurationException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class DBReader extends Reader{

	@Override
	public Configurable read(ConfigurationContext configurationContext, Class<? extends Configurable> configurableClass) throws LoadConfigurationException {
		try{
			ConfigurationProperties configurationProperties = configurableClass.getAnnotation(ConfigurationProperties.class);
			Configurable configurable = ReflectionUtil.createInstance(configurableClass);
			injectConfigurationContext(configurable, configurationContext);
			ReflectionUtil.getMethodAnnotatedWith(configurableClass, DBRead.class).invoke(configurable, new Object[]{});
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(configurationProperties.moduleName(), "Successfully read configuration from database.");
			}
			return configurable;
		}catch(InvocationTargetException ex){
			LogManager.getLogger().trace(ex.getTargetException());
			throw new LoadConfigurationException("Error in reading configuration." + ex.getTargetException().getMessage(), ex);
		} catch (IllegalAccessException ex) {
			LogManager.getLogger().trace(ex);
			throw new LoadConfigurationException("Error in reading configuration." + ex.getMessage(), ex);
		} catch (Exception ex) {
			LogManager.getLogger().trace(ex);
			throw new LoadConfigurationException("Error in reading configuration." + ex.getMessage(), ex);
		}
	}

	@Override
	public void reload(ConfigurationContext configurationContext,Configurable configurable) throws LoadConfigurationException {
		try{
			ReflectionUtil.getMethodAnnotatedWith(configurable.getClass(), DBReload.class).invoke(configurable, new Object[]{});
		}catch (Throwable ex) { 
			LogManager.getLogger().trace(ex.getCause());
			throw new LoadConfigurationException(ex.getCause().getMessage(), ex);
		}
	}

}
