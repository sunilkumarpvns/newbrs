package com.elitecore.nvsmx.system.groovy;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.InitializationFailedException;

public abstract class DDFGroovyScript {

	private static final String MODULE = "DDF-GROOVY-SCRIPT";

	public void init() throws InitializationFailedException {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "DDF Groovy script(" + getName() + ") initialized successfully");
		}
	}

	public void preSelect(SPRInfo sprInfo) {
		if(getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "preSelect of DDF Groovy script(" + getName() + ") called");
		}
	}

	public void postSelect(SPRInfo sprInfo) {
		if(getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postSelect of DDF Groovy script(" + getName() + ") called");
		}
	}
	
	public void preAddProfile(SPRInfo sprInfo) {
		if(getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "preAddProfile of DDF Groovy script(" + getName() + ") called");
		}
	}
	
	public void postAddProfile(SPRInfo sprInfo) {
		if(getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "postAddProfile of DDF Groovy script(" + getName() + ") called");
		}
	}
	
	//TODO add required methods in here --chetan

	abstract public String getName();
}
