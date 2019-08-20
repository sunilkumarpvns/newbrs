package com.elitecore.elitesm.web.cli.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("commandlineinterface")
public interface CLIService extends RemoteService {
	
	String getResponce(String command);

	void init(String adminHost, Integer adminPort);
	
}
