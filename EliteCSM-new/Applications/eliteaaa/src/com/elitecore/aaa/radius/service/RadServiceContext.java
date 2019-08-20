/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */


package com.elitecore.aaa.radius.service;

import java.util.List;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.services.AAAServiceContext;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.systemx.esix.ESCommunicator;


/**
 * 
 * @author baiju
 *
 */
public interface RadServiceContext<T extends RadServiceRequest, V extends RadServiceResponse> extends AAAServiceContext {

	public AAAServerContext getServerContext();
	
	public void submitAsyncRequest(T serviceRequest, V serviceResponse, AsyncRequestExecutor<T, V> requestExecutor);
	
	public ESCommunicator getDriver(String driverInstanceId);
	
	public String getServiceIdentifier() ;
	
	@Override
	public RadPluginRequestHandler createPluginRequestHandler(List<PluginEntryDetail> prePluginList, List<PluginEntryDetail> postPluginList);
}
