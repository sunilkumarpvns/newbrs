package com.elitecore.aaa.diameter.service;

import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.services.AAAServiceContext;
import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurationDetail;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAsyncRequestExecutor;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;



public interface DiameterServiceContext extends AAAServiceContext {
	public AAAServerContext getServerContext();
	public DiameterServiceConfigurationDetail getDiameterServiceConfigurationDetail();
	public ESCommunicator getDriver(String driverInstanceId);
	public DriverConfiguration getDriverConfiguration(String driverInstanceId);
	public ServicePolicy<ApplicationRequest> selectServicePolicy(ApplicationRequest appRequest);
	public void resumeRequestInAsync(Session session, ApplicationRequest request, ApplicationResponse response);
	public void resumeRequestInAsync(Session session, ApplicationRequest request, ApplicationResponse response,
			DiameterAsyncRequestExecutor unitOfWork);
	public IDiameterStackContext getStackContext();
	public void resumeRequestInSync(DiameterSession originatorSession, ApplicationRequest originalRequest,
			ApplicationResponse originalResponse);
	public SessionReleaseIndiactor getSessionReleaseIndicator();
}
