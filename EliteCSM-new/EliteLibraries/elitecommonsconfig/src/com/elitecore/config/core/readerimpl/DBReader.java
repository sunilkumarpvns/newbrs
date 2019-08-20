package com.elitecore.config.core.readerimpl;



import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.config.core.Configurable;
import com.elitecore.config.core.ConfigurationContext;
import com.elitecore.config.core.Reader;
import com.elitecore.config.core.annotations.ConfigurationProperties;
import com.elitecore.config.core.annotations.DBRead;
import com.elitecore.config.core.annotations.DBReload;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.config.util.ReflectionUtil;

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
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex.getCause());
			throw new LoadConfigurationException("Error in reading configuration." + ex.getCause().getMessage(), ex);
		}
	}

	@Override
	public void reload(ConfigurationContext configurationContext,Configurable configurable) throws LoadConfigurationException {
		try{
			ReflectionUtil.getMethodAnnotatedWith(configurable.getClass(), DBReload.class).invoke(configurable, new Object[]{});
		}catch (Throwable ex) {
			LogManager.getLogger().trace(ex.getCause());
			throw new LoadConfigurationException(ex.getMessage(), ex);
		}
	}

}
