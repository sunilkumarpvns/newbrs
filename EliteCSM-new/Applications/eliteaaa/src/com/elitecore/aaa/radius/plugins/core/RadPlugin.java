package com.elitecore.aaa.radius.plugins.core;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.core.commons.plugins.Plugin;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public interface RadPlugin<T extends RadServiceRequest, V extends RadServiceResponse> extends Plugin {
	
	public void handlePreRequest(T serviceRequest, V serviceResponse, String argument, PluginCallerIdentity callerID, ISession session);
	
	public void handlePostRequest(T serviceRequest, V serviceResponse, String argument, PluginCallerIdentity callerID, ISession session);
}
