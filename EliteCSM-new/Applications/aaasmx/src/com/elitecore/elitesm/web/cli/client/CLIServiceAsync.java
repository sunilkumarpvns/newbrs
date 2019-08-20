package com.elitecore.elitesm.web.cli.client;

import com.google.gwt.user.client.rpc.AsyncCallback;



/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface CLIServiceAsync {

	void getResponce(String command,AsyncCallback<String> callback);

	void init(String adminHost, Integer adminPort, AsyncCallback<Void> callback);		
}
