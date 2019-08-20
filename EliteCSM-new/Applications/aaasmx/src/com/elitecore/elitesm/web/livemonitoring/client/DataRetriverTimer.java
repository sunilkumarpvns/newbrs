package com.elitecore.elitesm.web.livemonitoring.client;

import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

class DataRetriverTimer extends Timer{
	private final int MAX_FAILURE_TIMES=25;
	private ServerInstanceBean serverInstanceBean;
	private int failureTimes=0;
	private AsyncCallback<List<Long[]>> callback;
	private GraphServiceAsync graphService ;
	private String graphType;
	
	public DataRetriverTimer(GraphServiceAsync graphService , ServerInstanceBean serverInstanceBean ,String graphType,AsyncCallback<List<Long[]>> callback){
		this.serverInstanceBean = serverInstanceBean;
		this.callback = callback;
		this.graphService = graphService;
		this.graphType = graphType;
	}
	public void run() {
		try{
			graphService.getGraphData(serverInstanceBean,graphType, callback);
			failureTimes=0;
		}catch(Exception e){
			if(failureTimes>MAX_FAILURE_TIMES){
				this.cancel();
				Log.info("Cancelling the service.");
			}
			failureTimes++;
		}
	}
	public int getCurrentFailureTimes(){
		return failureTimes;
	}
}
