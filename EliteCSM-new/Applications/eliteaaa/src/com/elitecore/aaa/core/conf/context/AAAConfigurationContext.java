package com.elitecore.aaa.core.conf.context;


import static com.elitecore.aaa.core.conf.context.AAAConfigurationState.FALLBACK_CONFIGURATION;
import static com.elitecore.aaa.core.conf.context.AAAConfigurationState.NORMAL;
import static com.elitecore.aaa.core.conf.context.AAAConfigurationState.UNRECOVERABLE;

import java.io.File;
import java.io.IOException;

import com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration;
import com.elitecore.aaa.core.conf.impl.ClassicCSVAcctDriverConfigurable;
import com.elitecore.aaa.core.conf.impl.MiscellaneousConfigurable;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.ConfigurationContext;
import com.elitecore.core.commons.config.core.Reader;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.util.FileUtil;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.configuration.StoreConfigurationException;

/**
 * 
 * <b>READ STRATEGY</b>
 * <pre>
  +-------------------------------------+
  | +-----------+        +------------+ |
  | |           |        |            | |
  | |   /conf   |        |   Conf DB  | |
  | |           |        |            | |
  | +-----------+        +------------+ |
  +-------------------------------------+
                    +
                    |
                    |
                    v
               ON ERROR IN READ
                    +
                    |
                    |
  +-----------------v-------------------+
  |                                     |
  |                                     |
  |   FALLBACK           CONFIGURATION  |
  |                                     |
  |                                     |
  +-----------------+-------------------+
                    |
                    |
                    v
               UNRECOVERABLE</pre>
               
               
  <b>WRITE STRATEGY</b>             
              
   <pre>
  +-------------------------------------+
  | +-----------+        +------------+ |
  | |           |        |            | |
  | |   /conf   |        |   Conf DB  | |
  | |           |        |            | |
  | +-----------+        +------------+ |
  +-------------------------------------+
                    +
                    |
                    |
                    v
               ON SUCCESSFUL READ
                    +
                    |
                    |
  +-----------------v-------------------+
  |                                     |
  |               CREATE                |
  |   FALLBACK           CONFIGURATION  |
  |                                     |
  |              /backup                |
  +-----------------+-------------------+</pre>
                           
 * @author narendra.pathai
 */
public class AAAConfigurationContext extends ConfigurationContext{
	
	private AAAConfigurationState state = NORMAL;
	private AAAServerContext serverContext;
	private String originalConfigurationPath;
	private String backupConfigurationPath;
	
	public AAAConfigurationContext(String basePath,AAAServerContext serverContext) {
		super(basePath);
		this.originalConfigurationPath = basePath;
		this.serverContext = serverContext;
		this.backupConfigurationPath = serverContext.getServerHome() + File.separator + "system" + File.separator + "backup";
		this.cloner.dontCloneInstanceOf(ClassicCSVAcctDriverConfigurable.class);
		this.cloner.dontCloneInstanceOf(ClassicCSVAcctDriverConfiguration.class);
	}
	
	public AAAServerContext getServerContext() {
		return serverContext;
	}
	
	@Override
	public Configurable read(Class<? extends Configurable> configurableClass) throws LoadConfigurationException {
		try{
			return super.read(configurableClass);
		}catch (LoadConfigurationException ex) {
			changeStateOnReadFailure();
			setBasePath(backupConfigurationPath);
			throw ex;
		}
	}

	@Override
	protected Configurable readSimple(Class<? extends Configurable> simpleConfigurableClass)throws LoadConfigurationException {
		if(state == FALLBACK_CONFIGURATION && MiscellaneousConfigurable.class.equals(simpleConfigurableClass)){
			setBasePath(originalConfigurationPath);
			Configurable configurable = super.readSimple(simpleConfigurableClass);
			setBasePath(backupConfigurationPath);
			return configurable;
		}
		return super.readSimple(simpleConfigurableClass);
	}
	
	@Override
	protected void reloadSimple(Configurable configurable) throws LoadConfigurationException {
		if(state == FALLBACK_CONFIGURATION && MiscellaneousConfigurable.class.equals(configurable.getClass())){
			setBasePath(originalConfigurationPath);
			super.reloadSimple(configurable);
			setBasePath(backupConfigurationPath);
		}else{
			super.reloadSimple(configurable);
		}
	}
	
	private void changeStateOnReadFailure(){
		if(state == NORMAL){
			state = FALLBACK_CONFIGURATION;
		}else if(state == FALLBACK_CONFIGURATION){
			state = UNRECOVERABLE;
		}
	}
	
	@Override
	public void write(Configurable configurable) throws StoreConfigurationException {
		super.write(configurable);
		storeAsLastGoodConfiguration();
	}
	
	@Override
	protected Class<? extends Reader> extractReader(Class<? extends Configurable> configurableClass) {
		if(state == FALLBACK_CONFIGURATION){
			ConfigurationProperties configurationProperties = configurableClass.getAnnotation(ConfigurationProperties.class);
			return configurationProperties.fallbackReadWith();
		}
		
		return super.extractReader(configurableClass);
	}
	
	private void storeAsLastGoodConfiguration() {
		try{
			File originalConfigurationFile = new File(serverContext.getServerHome() + File.separator + "conf");
			File backupConfigurationFile = new File(backupConfigurationPath);
			FileUtil.copyDirectoryToDirectory(originalConfigurationFile, backupConfigurationFile);
			LogManager.getLogger().info("AAA_CONFIGURATION_CONTEXT", "Successfully copied last good configuration to backup location.");
		}catch (IOException ex) {
			LogManager.getLogger().trace("AAA_CONFIGURATION_CONTEXT", ex);
		}
	}

	public AAAConfigurationState state(){
		return state;
	}
	
	public AAAConfigurationContext getDependentConfigurationContext(){
		AAAConfigurationContext dependentConfigurationContext = new AAAConfigurationContext(getBasePath(), serverContext);
		dependentConfigurationContext.state = state;
		return dependentConfigurationContext;
	}
}
