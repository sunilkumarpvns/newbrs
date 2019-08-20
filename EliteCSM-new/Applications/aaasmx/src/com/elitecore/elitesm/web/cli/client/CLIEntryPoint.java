package com.elitecore.elitesm.web.cli.client;


import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.cli.client.ui.EliteCLITerminal;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CLIEntryPoint implements EntryPoint {



	private final CLIServiceAsync cliService=GWT.create(CLIService.class);
	private static final boolean logEnabled = false;


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
			
		Widget logWidget = Log.getLogger(DivLogger.class).getWidget();
		logWidget.setVisible(false);
		
		
	    Dictionary javascriptVariableDictionary = Dictionary.getDictionary("jsVars"); 
		
		String adminHost = javascriptVariableDictionary.get("adminHost");
		String strAdminPort = javascriptVariableDictionary.get("adminPort");
		Integer  adminPort=Integer.parseInt(strAdminPort);
		/*
		 *  mbean call get response
		 */
		
		AsyncCallback<Void> callBackHandler= new AsyncCallback<Void>(){

			public void onFailure(Throwable caught) {
			}

			public void onSuccess(Void result) {
				
			}
		};
		cliService.init(adminHost,adminPort,callBackHandler);
		new EliteCLITerminal(cliService);
		
	}

	
}
