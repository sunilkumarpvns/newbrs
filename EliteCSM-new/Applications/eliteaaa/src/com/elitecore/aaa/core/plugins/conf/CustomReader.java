package com.elitecore.aaa.core.plugins.conf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.ConfigurationContext;
import com.elitecore.core.commons.config.core.Reader;
import com.elitecore.core.commons.config.util.ReflectionUtil;
import com.elitecore.core.commons.configuration.LoadConfigurationException;

/**
 * 
 * This reader can be used to read configuration class using some custom mechanism like manual DB query or other. 
 * <p>
 * It creates instance of configuration and calls method annotated with {@link CustomRead}, to allow custom reading.
 * Similarly it calls method annotated with {@link CustomReload}, to allow custom reloading.
 *  
 * @author narendra.pathai
 *
 */
public class CustomReader extends Reader {

	private static final String ERROR_IN_RELOADING_CONFIGURATION = "Error in reloading configuration.";
	private static final String ERROR_IN_READING_CONFIGURATION = "Error in reading configuration.";

	@Override
	public Configurable read(ConfigurationContext configurationContext,
			Class<? extends Configurable> configurableClass)
			throws LoadConfigurationException {
		try {
			
			Method customReadMethod = ReflectionUtil.getMethodAnnotatedWith(configurableClass, CustomRead.class);
			if (customReadMethod == null) {
				throw new LoadConfigurationException("No method in class: " + configurableClass + " annotated with " + CustomRead.class);
			}
			Configurable configurable = ReflectionUtil.createInstance(configurableClass);
			injectConfigurationContext(configurable, configurationContext);
			customReadMethod.invoke(configurable, new Object[] {});
			return configurable;
			
		} catch (InvocationTargetException ex) {
			throw new LoadConfigurationException(ERROR_IN_READING_CONFIGURATION + ex.getTargetException().getMessage(), ex);
		} catch (IllegalArgumentException ex) {
			throw new LoadConfigurationException(ERROR_IN_READING_CONFIGURATION + ex.getMessage(), ex);
		} catch (IllegalAccessException ex) {
			throw new LoadConfigurationException(ERROR_IN_READING_CONFIGURATION + ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new LoadConfigurationException(ERROR_IN_READING_CONFIGURATION + ex.getMessage(), ex);
		}
	}

	@Override
	public void reload(ConfigurationContext configurationContext,
			Configurable configurable) throws LoadConfigurationException {
			try {
				ReflectionUtil.getMethodAnnotatedWith(configurable.getClass(), CustomReload.class).invoke(configurable, new Object[]{});
			} catch (IllegalArgumentException ex) {
				throw new LoadConfigurationException(ERROR_IN_RELOADING_CONFIGURATION + ex.getMessage(), ex);
			} catch (IllegalAccessException ex) {
				throw new LoadConfigurationException(ERROR_IN_RELOADING_CONFIGURATION + ex.getMessage(), ex);
			} catch (InvocationTargetException ex) {
				throw new LoadConfigurationException(ERROR_IN_RELOADING_CONFIGURATION + ex.getTargetException().getMessage(), ex);
			} catch (Exception ex) {
				throw new LoadConfigurationException(ERROR_IN_RELOADING_CONFIGURATION + ex.getMessage(), ex);
			}
	}
}
