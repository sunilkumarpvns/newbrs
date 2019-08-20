package com.elitecore.aaa.core.conf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.conf.AAAPluginConfManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.serverx.ServerContext;

@ReadOrder(order = { "aaaPluginConfManagerConfigurable" })
public class AAAPluginConfManagerImpl extends CompositeConfigurable implements AAAPluginConfManager{

	private static final String MODULE = "AAA-PLGN-CONF-MGR";	
	
	@Configuration(required =false) private AAAPluginConfManagerConfigurable aaaPluginConfManagerConfigurable;
	
	private Map<String, PluginConfiguration> radPluginConfigurationMap;
	private Map<String, PluginConfiguration> diameterPluginConfigurationMap;
	
	private List<PluginInfo> radiusPluginList;
	private List<PluginInfo> diameterPluginList;
	
	@Deprecated
	public AAAPluginConfManagerImpl(ServerContext serverContext) {
		radiusPluginList = new ArrayList<PluginInfo>();
		diameterPluginList = new ArrayList<PluginInfo>();
		this.radPluginConfigurationMap = new HashMap<String, PluginConfiguration>();
	}

	//required by configuration library
	public AAAPluginConfManagerImpl() {
		radiusPluginList = new ArrayList<PluginInfo>();
		diameterPluginList = new ArrayList<PluginInfo>();
		radPluginConfigurationMap = new HashMap<String, PluginConfiguration>();
		diameterPluginConfigurationMap = new HashMap<String, PluginConfiguration>();
	}
	
	@PostRead
	public void postReadProcessing() throws LoadConfigurationException{
		this.radiusPluginList = aaaPluginConfManagerConfigurable.getRadPluginInfoList();
		this.diameterPluginList = aaaPluginConfManagerConfigurable.getDiameterPluginInfoList();
		loadPluginConfigurations();
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing() throws LoadConfigurationException{
		reloadPluginConfigrations();
	}
	
	private void loadPluginConfigurations() throws LoadConfigurationException{
		//Creates the instances of all Radius plugins configured in system mapping
		this.radPluginConfigurationMap = loadPluginConfigurations(this.radiusPluginList);

		//Creates the instances of all Diameter plugins configured in system mapping
		this.diameterPluginConfigurationMap = loadPluginConfigurations(this.diameterPluginList);
	}
	
	private void reloadPluginConfigrations() throws LoadConfigurationException {
		this.radPluginConfigurationMap = reloadPluginConfigrations(this.radiusPluginList, this.radPluginConfigurationMap);
		
		this.diameterPluginConfigurationMap = reloadPluginConfigrations(this.diameterPluginList, this.diameterPluginConfigurationMap);
	}
	
	private Map<String, PluginConfiguration> reloadPluginConfigrations(List<PluginInfo> pluginInfos, Map<String, PluginConfiguration> pluginConfigurationEntries) throws LoadConfigurationException {
		Map<String, PluginConfiguration> reloadedPluginConfigurations = new HashMap<String, PluginConfiguration>();
		for(PluginInfo pluginInfo : pluginInfos){
			PluginConfiguration pluginConfiguration = pluginConfigurationEntries.get(pluginInfo.getPluginName());
			if(pluginConfiguration != null){
				try{
					AAAConfigurationContext context = ((AAAConfigurationContext)getConfigurationContext()).getDependentConfigurationContext();
					PluginConfiguration reloadedPluginConfiguration = (PluginConfiguration) context.reload((Configurable)pluginConfiguration);
					reloadedPluginConfigurations.put(pluginInfo.getPluginName(), (PluginConfiguration)reloadedPluginConfiguration);
				}catch(LoadConfigurationException ex){
					LogManager.getLogger().error(MODULE, "Unable to reload configuration for Plugin " + pluginInfo.getPluginName() + ", configuration class : "
							+ pluginInfo.getPluginConfClass() + ". Reason : " + ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
					throw ex;
				}
			}
		}
		return reloadedPluginConfigurations;
	}

	private Map<String, PluginConfiguration> loadPluginConfigurations(List<PluginInfo> pluginInfoList) throws LoadConfigurationException{
		Map<String, PluginConfiguration> pluginConfigurationMap = new HashMap<String, PluginConfiguration>();
		String pluginConfClassName = null;
		if(pluginInfoList !=null){
			final int size = pluginInfoList.size();
			for(int i=0;i<size;i++){
				pluginConfClassName = pluginInfoList.get(i).getPluginConfClass();
				if(pluginConfClassName != null && pluginConfClassName.length() > 0){
					try{
						Class<?> pluginConfigurationClass = Class.forName(pluginConfClassName);
						if(PluginConfiguration.class.isAssignableFrom(pluginConfigurationClass) && Configurable.class.isAssignableFrom(pluginConfigurationClass)){
							AAAConfigurationContext context = ((AAAConfigurationContext)getConfigurationContext()).getDependentConfigurationContext();
							Class<? extends Configurable> pluginConfigurationClazz = pluginConfigurationClass.asSubclass(Configurable.class);
							PluginConfiguration pluginConfiguration = (PluginConfiguration) context.read(pluginConfigurationClazz);
							pluginConfigurationMap.put(pluginInfoList.get(i).getPluginName(), (PluginConfiguration)pluginConfiguration);
						}else{
							LogManager.getLogger().error(MODULE, "Class: " + pluginConfigurationClass.getSimpleName() + " does not implement " + PluginConfiguration.class.getName() 
																+  " or does not extend : " + Configurable.class.getSimpleName() + ". Plugin will not be added.");
						}
					} catch (ClassNotFoundException e) {
						LogManager.getLogger().error(MODULE, "Plugin " + pluginInfoList.get(i).getPluginName() + " configuration class : "
								                + pluginInfoList.get(i).getPluginConfClass() + " not found");
						LogManager.getLogger().trace(MODULE, e);
					}catch(LoadConfigurationException ex){
						LogManager.getLogger().error(MODULE, "Unable to read configuration for Plugin " + pluginInfoList.get(i).getPluginName() + ", configuration class : "
				                + pluginInfoList.get(i).getPluginConfClass() + ". Reason : " + ex.getMessage());
						LogManager.getLogger().trace(MODULE, ex);
					}
				}	
			}
		}
		return pluginConfigurationMap;
	}
	

	@Override
	public List<PluginInfo> getRadPluginInfoList() {		
		return radiusPluginList;
	}

	@Override
	public PluginConfiguration getRadPluginConfiguration(String pluginName) {
		return this.radPluginConfigurationMap.get(pluginName);
	}
	
	public PluginConfiguration getDiameterPluginConfiguration(String pluginName){
		return this.diameterPluginConfigurationMap.get(pluginName);
	}
	
	@Override
	public List<PluginInfo> getDiameterPluginInfoList() {
		return this.diameterPluginList;
	}

	@Override
	public boolean isEligible(
			Class<? extends Configurable> configurableClass) {
		return false;
	}
}
