package com.elitecore.elitesm.web.livemonitoring.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GraphServiceAsync {
	
	void getServerInstance(String serverId,AsyncCallback<ServerInstanceBean> callback) throws IllegalArgumentException;
	
	void getGraphData(ServerInstanceBean serverInstanceBean, String graph, AsyncCallback<List<Long[]>> callback) throws IllegalArgumentException;
}
