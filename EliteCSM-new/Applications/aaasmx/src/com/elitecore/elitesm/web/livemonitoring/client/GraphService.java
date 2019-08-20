package com.elitecore.elitesm.web.livemonitoring.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("graph")
public interface GraphService extends RemoteService{
	ServerInstanceBean getServerInstance(String serverId) throws IllegalArgumentException;
	List<Long[]> getGraphData(ServerInstanceBean serverInstanceBean,String graph);
}
