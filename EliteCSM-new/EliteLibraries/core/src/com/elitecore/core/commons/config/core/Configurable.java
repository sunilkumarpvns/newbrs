package com.elitecore.core.commons.config.core;



import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlTransient
public abstract class Configurable{
	
	private ConfigurationContext configurationContext;
	
	protected ConfigurationContext getConfigurationContext(){
		return this.configurationContext;
	}
	
	void setConfigurationContext(ConfigurationContext configurationContext){
		this.configurationContext = configurationContext;
	}
}
