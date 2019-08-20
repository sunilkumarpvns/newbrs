package com.elitecore.core.commons.config.core.readerimpl;

import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.ConfigurationContext;
import com.elitecore.core.commons.config.core.Reader;
import com.elitecore.core.commons.configuration.LoadConfigurationException;

/**
 * Does no reloading.
 * <p>
 * {@link NullReloader} does not support reading.
 * 
 * @author narendra.pathai
 *
 */
public class NullReloader extends Reader {

	@Override
	public Configurable read(ConfigurationContext configurationContext,
			Class<? extends Configurable> configurableClass)
			throws LoadConfigurationException {
		throw new UnsupportedOperationException("Reading is not supported with NullReloader");
	}

	@Override
	public void reload(ConfigurationContext configurationContext,
			Configurable configurable) throws LoadConfigurationException {
		// do no reloading
	}
	
}
