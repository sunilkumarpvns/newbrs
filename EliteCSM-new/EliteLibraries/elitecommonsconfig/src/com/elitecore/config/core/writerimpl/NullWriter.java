package com.elitecore.config.core.writerimpl;

import com.elitecore.config.core.Configurable;
import com.elitecore.config.core.ConfigurationContext;
import com.elitecore.config.core.Writer;
import com.elitecore.config.exception.StoreConfigurationException;

public class NullWriter extends Writer{

	@Override
	public void write(ConfigurationContext configurationContext, Configurable configurable) throws StoreConfigurationException {
		//This is just a dummy writer no need to do anything here
	}
}
