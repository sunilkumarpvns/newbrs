package com.elitecore.aaa.radius.plugins.core;

import com.elitecore.aaa.core.plugins.PluginRequestHandler;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public interface RadPluginRequestHandler extends PluginRequestHandler {

	public void handlePreRequest(RadServiceRequest request,RadServiceResponse response, ISession session);
	public void handlePostRequest(RadServiceRequest request,RadServiceResponse response, ISession session);

}
