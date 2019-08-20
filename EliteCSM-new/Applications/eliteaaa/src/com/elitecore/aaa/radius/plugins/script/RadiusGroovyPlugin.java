package com.elitecore.aaa.radius.plugins.script;

import com.elitecore.aaa.radius.plugins.core.BaseRadPlugin;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

/**
 * This is a wrapper Object that stores the external GROOVY plugin which is compiled and 
 * instantiated by <code>ExternalScriptsManager</code> and is provided to service specific 
 * Plugin manager. 
 * 
 * @author narendra.pathai
 *
 */
public class RadiusGroovyPlugin extends BaseRadPlugin<RadServiceRequest, RadServiceResponse> {
	private RadiusScriptPlugin scriptPlugin;
	public RadiusGroovyPlugin(RadiusScriptPlugin scriptPlugin, PluginContext pluginContext,
			PluginInfo pluginInfo) {
		super(pluginContext, pluginInfo);
		this.scriptPlugin = scriptPlugin;
	}

	@Override
	public void handlePreRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		try{
			scriptPlugin.handlePreRequest(serviceRequest, serviceResponse, argument, callerID);
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
		}
	}

	@Override
	public void handlePostRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		try{
			scriptPlugin.handlePostRequest((RadServiceRequest)serviceRequest, (RadServiceResponse)serviceResponse, argument, callerID);
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
		}
	}

	@Override
	public void init() throws InitializationFailedException {
		try{
			scriptPlugin.init();
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
			throw new InitializationFailedException(ex.getMessage());
		}
	}

}
