package com.elitecore.core.commons.config.core.writerimpl;

import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.ConfigurationContext;
import com.elitecore.core.commons.config.core.Writer;
import com.elitecore.core.commons.configuration.StoreConfigurationException;

public class NullWriter extends Writer{

	@Override
	public void write(ConfigurationContext configurationContext, Configurable configurable) throws StoreConfigurationException {
		//This is just a dummy writer no need to do anything here
	}
}
