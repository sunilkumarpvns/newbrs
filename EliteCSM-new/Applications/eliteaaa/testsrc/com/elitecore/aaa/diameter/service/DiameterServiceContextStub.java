package com.elitecore.aaa.diameter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.plugins.PluginRequestHandler;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurationDetail;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAsyncRequestExecutor;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;

// TODO implement the bare minimum requirements
public class DiameterServiceContextStub implements DiameterServiceContext {
	
	private Map<String, ESCommunicator> driverIdToDriverInstance = new HashMap<String, ESCommunicator>();
	
	@Override
	public PluginRequestHandler createPluginRequestHandler(
			List<PluginEntryDetail> prePluginList,
			List<PluginEntryDetail> postPluginList) {
		return null;
	}

	@Override
	public AAAServerContext getServerContext() {
		return null;
	}

	@Override
	public DiameterServiceConfigurationDetail getDiameterServiceConfigurationDetail() {
		return null;
	}

	@Override
	public ESCommunicator getDriver(String driverInstanceId) {
		return driverIdToDriverInstance.get(driverInstanceId);
	}

	@Override
	public DriverConfiguration getDriverConfiguration(String driverInstanceId) {
		return null;
	}

	@Override
	public ServicePolicy<ApplicationRequest> selectServicePolicy(
			ApplicationRequest appRequest) {
		return null;
	}

	@Override
	public void resumeRequestInAsync(Session session,
			ApplicationRequest request, ApplicationResponse response) {
		
	}

	@Override
	public void resumeRequestInAsync(
			Session session,
			ApplicationRequest request,
			ApplicationResponse response,
			DiameterAsyncRequestExecutor unitOfWork) {
		
	}

	@Override
	public IDiameterStackContext getStackContext() {
		return null;
	}

	@Override
	public void resumeRequestInSync(DiameterSession originatorSession,
			ApplicationRequest originalRequest,
			ApplicationResponse originalResponse) {
		
	}

	@Override
	public SessionReleaseIndiactor getSessionReleaseIndicator() {
		return null;
	}

	public void addDriver(String driverInstanceId, ESCommunicator driver) {
		this.driverIdToDriverInstance.put(driverInstanceId, driver);
	}
}
