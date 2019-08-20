package com.elitecore.elitesm.web.livemonitoring.client;



import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gchart.client.GChart;
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GraphEntryPoint implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	
	static { 
	    GChart.setCanvasFactory(new GWTCanvasBasedCanvasFactory());
	  } 
	
	private static final String SERVER_ERROR = "An error occurred while "
		+ "attempting to contact the server. Please check your network "
		+ "connection and try again.";
	private final GraphServiceAsync graphService = GWT.create(GraphService.class);
	private boolean logEnabled = false;
	
	
	
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		Log.setUncaughtExceptionHandler();
		
      		DeferredCommand.addCommand(new Command() {
		      public void execute() {
		    	  graphNewData();
		      }
		    });
	}


	
	public void graphNewData(){
		try {

			// fetch server instances.
			Log.debug("Entered in GraphEntryPoint...");
			if(!logEnabled){
				Widget logWidget = Log.getLogger(DivLogger.class).getWidget();
				logWidget.setVisible(false);
			}
			
			
			Dictionary javascriptVariableDictionary = Dictionary.getDictionary("jsVars"); 
			
			String serverId = javascriptVariableDictionary.get("serverId");
			String graphType = javascriptVariableDictionary.get("graphType");
			Log.debug("serverId = "+serverId);
			Log.debug("graphType = "+graphType);
			
			final ServerInstanceAsyncCallBack callback = new ServerInstanceAsyncCallBack(graphType,logEnabled) ;
			Log.debug("calling method for getting server instancegraphType");
			graphService.getServerInstance(serverId, callback);
			ServerInstanceBean  serverInstanceBean = callback.getServerInstanceBean();
			Log.debug("ServerInstanceBean = "+serverInstanceBean);
			
			
		}catch(Exception e){
			Log.debug("error in loading components", e);
		}
	}
	
}
