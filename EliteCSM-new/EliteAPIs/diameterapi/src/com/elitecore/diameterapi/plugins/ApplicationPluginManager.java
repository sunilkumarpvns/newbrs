package com.elitecore.diameterapi.plugins;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public class ApplicationPluginManager {
	
	Map<String, DiameterPluginManager> pluginManagers;
	private static final String MODULE = "DIA-APP-PLUGIN-MNGR";
	
	public ApplicationPluginManager() {
		this.pluginManagers = new HashMap<String, DiameterPluginManager>();
	}
	
	public void registerPluginManager(String applicationIdentifier,DiameterPluginManager diameterPluginManager){
		if(applicationIdentifier!=null && applicationIdentifier.trim().length()>0 && diameterPluginManager!=null){
			pluginManagers.put(applicationIdentifier, diameterPluginManager);
		}
	}
	
	public void applyInPlugins(String applicationIdentifier,DiameterRequest diameterRequest,
			DiameterAnswer diameterAnswer, ISession session) {
		DiameterPluginManager diameterPluginManager = pluginManagers.get(applicationIdentifier);
		if(diameterPluginManager!=null)
			diameterPluginManager.applyInPlugins(diameterRequest,diameterAnswer, session);
		else{
			LogManager.getLogger().debug(MODULE, "No diameter plugins to apply for Application : "+applicationIdentifier);
		}
	}
	
	public void applyOutPlugins(String applicationIdentifier,DiameterRequest diameterRequest, 
			DiameterAnswer diameterAnswer, ISession session) {
		DiameterPluginManager diameterPluginManager = pluginManagers.get(applicationIdentifier);
		if(diameterPluginManager!=null)
			diameterPluginManager.applyOutPlugins(diameterRequest,diameterAnswer, session);
		else{
			LogManager.getLogger().debug(MODULE, "No diameter plugins to apply for Application : "+applicationIdentifier);
		}
	}

}
