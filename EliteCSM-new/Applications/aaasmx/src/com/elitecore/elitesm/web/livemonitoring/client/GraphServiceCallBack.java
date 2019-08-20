package com.elitecore.elitesm.web.livemonitoring.client;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GraphServiceCallBack implements AsyncCallback<List<Long[]>>{
	
	private static final String MODULE = "Graph Service CallBack";
	
	AbstractChartPanel chartPanel = null;
	
	GraphServiceCallBack(AbstractChartPanel chartPanel){
		this.chartPanel =chartPanel;
	}
	public void onSuccess(List<Long[]> graphData) {
		try{
			Log.info(MODULE+" : called graph service callback");
			chartPanel.updateData(graphData);
		}catch(Exception e){
			e.printStackTrace();
			Log.debug(MODULE+" : error in updating data", e);
		}
	}
	public void onFailure(Throwable caught) {
		Log.error(MODULE+" : "+caught.getMessage());
	}

	
}
