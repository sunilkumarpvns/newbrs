package com.elitecore.aaa.radius.sessionx.conf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.radius.sessionx.conf.SessionManagerConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerData;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.configuration.LoadConfigurationException;

@ReadOrder(order = { "localSessionManagerConfigurable"})
public class SessionManagerConfigurationImpl extends CompositeConfigurable implements SessionManagerConfiguration{

	private Map<String,SessionManagerData> smConfigurationMap;
	private Map<String,SessionManagerData> smConfigurationMapByName;
	@Configuration private LocalSessionManagerConfigurable localSessionManagerConfigurable;
	
	public SessionManagerConfigurationImpl() {
		this.smConfigurationMap = new HashMap<String,SessionManagerData>();
		this.smConfigurationMapByName = new HashMap<String, SessionManagerData>();
	}
	
	@PostRead
	public void postReadProcessing(){
		List<LocalSessionManager> localSessionManagerList = new ArrayList<LocalSessionManager>();
		Map<String, SessionManagerData> sessionManagerDataMap = new HashMap<String, SessionManagerData>();
		Map<String, SessionManagerData> sessionManagerDataMapByName = new HashMap<String, SessionManagerData>();

		try{
			if(localSessionManagerConfigurable.getLocalSessionMangaerList()!=null){
				localSessionManagerList.addAll(localSessionManagerConfigurable.getLocalSessionMangaerList());
				
				int size  = localSessionManagerList.size();
				LocalSessionManager localSessionConfiguration;
				if(size >0){
					for(int i=0;i<size;i++){
						localSessionConfiguration = (LocalSessionManager) localSessionManagerList.get(i); 
						String name  = localSessionConfiguration.getInstanceName();; 
						if(localSessionConfiguration.getInstanceId() != null && name!=null && name.trim().length()>0){
							sessionManagerDataMap.put(localSessionConfiguration.getInstanceId(), localSessionConfiguration);	
							sessionManagerDataMapByName.put(name, localSessionConfiguration);
						}
					}
				}
			}

		}catch(Exception e){
			//TODO : Handle Exception
		}

		this.smConfigurationMap = sessionManagerDataMap;
		this.smConfigurationMapByName = sessionManagerDataMapByName;
	}

	
	@Override
	public Map<String, SessionManagerData> getSmConfigurationMap() {
		return smConfigurationMap;
	}

	@Override
	public SessionManagerData getSessionManagerConfigById(String smInstanceId) {
		return smConfigurationMap.get(smInstanceId);
	}

	@Override
	public SessionManagerData getSessionManagerConfig(String name) {
		if(name!=null){
			return this.smConfigurationMapByName.get(name);
		}
		return null;
	}
	@Override
	public boolean isEligible(
			Class<? extends com.elitecore.core.commons.config.core.Configurable> configurableClass) {
		return false;
	}
	
	@Override
	public void readConfiguration() throws LoadConfigurationException {
		
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}
}
