package com.elitecore.core.commons.config.core;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.config.util.ReflectionUtil;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.configuration.StoreConfigurationException;

/**
 * 
 * @author narendra.pathai
 *
 */

//FIXME narendra - this class requires refactoring
//FIXME narendra - the post reload strategy will have to be changed should not be seperate call
public abstract class CompositeConfigurable extends Configurable {
	private static final String MODULE = "COMPOSITE_CONFIGURABLE";

	List<Field> orderedFields = new ArrayList<Field>();

	public CompositeConfigurable() {
		formOrderedFields();
	}

	public abstract boolean isEligible(Class<? extends Configurable> configurableClass);

	private void formOrderedFields() {
		try {
			ReadOrder readOrder = this.getClass().getAnnotation(ReadOrder.class);
			String[] order = readOrder.order();
			for(String property: order){
				Field field = ReflectionUtil.getFieldWithName(property, getClass());
				field.setAccessible(true);
				orderedFields.add(field);
			}
		} catch (Throwable ex) {
			LogManager.getLogger().trace(ex);
		}
	}

	final void read() throws LoadConfigurationException {
		for (Field field : orderedFields) { 
			Class<?> returnType = field.getType();
			Configuration configurationMetaData = field.getAnnotation(Configuration.class);
			if (configurationMetaData.conditional()) {
				if (isEligible(returnType.asSubclass(Configurable.class)) == false) {
					LogManager.getLogger().debug(MODULE, "Skipping reading of field: " + field.getName());
					continue;
				}
			}

			Configurable node = null;
			try {
				node = getConfigurationContext().readInternal(returnType.asSubclass(Configurable.class));
			} catch (LoadConfigurationException ex) {
				if (configurationMetaData.required()) {
					LogManager.getLogger().warn(MODULE, "Error in reading field: " + field.getName() + ". Reason: " + ex.getMessage());
					throw ex;
				} else {
					LogManager.getLogger().warn(MODULE, "Error in reading field: " + field.getName() 
							+ ". Reason: " + ex.getMessage() + ", but skipping as it is not required.");
					LogManager.getLogger().trace(ex);						
				}
			}
			setField(field, this, node);
		}
	}

	final void write() throws StoreConfigurationException {
		try {
			for (Field field : orderedFields) {
				Configuration configurationMetaData = field.getAnnotation(Configuration.class);
				if (configurationMetaData.conditional()) {
					if (isEligible(field.getType().asSubclass(Configurable.class)) == false) {
						LogManager.getLogger().debug(MODULE, "Skipping writing of field: " + field.getName());
						continue;
					}
				}

				try {
					Configurable configurable = (Configurable) field.get(this);
					getConfigurationContext().writeInternal(configurable);
				} catch (StoreConfigurationException ex) {
					LogManager.getLogger().trace(ex);
					if (configurationMetaData.required()) {
						LogManager.getLogger().warn(MODULE, "Error in writing field: " + field.getName() + ". Reason: " + ex.getMessage());
						throw ex;
					} else {
						LogManager.getLogger().warn(MODULE, "Error in writing field: " + field.getName() 
								+ ". Reason: " + ex.getMessage() + ", but skipping as it is not required");
					}
				}
			}
		} catch(Throwable ex) {
			throw new StoreConfigurationException(ex);
		}
	}

	final void reload() throws LoadConfigurationException {
		try {
			for (Field field : orderedFields) {
				Configuration configurationMetaData = field.getAnnotation(Configuration.class);
				if (configurationMetaData.conditional()) {
					if (isEligible(field.getType().asSubclass(Configurable.class)) == false) {
						LogManager.getLogger().debug(MODULE, "Skipping reloading of field: " + field.getName());
						continue;
					}
				}

				try {
					Configurable configurable = (Configurable) field.get(this);
					if (configurable != null) {
						getConfigurationContext().reloadInternal(configurable);
					} else {
						LogManager.getLogger().debug(MODULE, "Skipping reloading of field: " + field.getName() + ", as configuration not found");
					}
				} catch (LoadConfigurationException ex) {
					LogManager.getLogger().warn(MODULE, "Error in reloading field: " + field.getName() + ". Reason: " + ex.getCause());
					if (configurationMetaData.required()) {
						throw ex;
					}
				}
			}
		} catch(Throwable ex) {
			LogManager.getLogger().trace(ex);
			throw new LoadConfigurationException(ex);
		}
	}

	final void createDefaultInstance() throws LoadConfigurationException{
		for (Field field : orderedFields) { 
			Class<?> returnType = field.getType();
			Configurable node = getConfigurationContext().createDefaultInstanceInternal(returnType.asSubclass(Configurable.class));
			setField(field, this, node);
		}
	}

	private void setField(Field field, Object objToInjectFieldOn, Object objToInject) throws LoadConfigurationException{
		try {
			field.set(objToInjectFieldOn, objToInject);
		} catch (IllegalArgumentException e) {
			throw new LoadConfigurationException("Error in reading field: " + field.getName() + ". Reason: " + e.getMessage(), e.getCause());
		} catch (IllegalAccessException e) {
			throw new LoadConfigurationException(e.getCause().getMessage());
		}
	}
}
