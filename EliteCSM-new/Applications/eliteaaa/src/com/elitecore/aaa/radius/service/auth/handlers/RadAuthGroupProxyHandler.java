package com.elitecore.aaa.radius.service.auth.handlers;

import static com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.EsiType.AUTH;
import static com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.EsiType.CHARGING_GATEWAY;
import static com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.EsiType.CORRELATED_RADIUS;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.AuthAsyncRequestExecutors.AuthRequestTimeoutExecutor;
import com.elitecore.aaa.radius.service.auth.handlers.AuthAsyncRequestExecutors.AuthResponseReceivedExecutor;
import com.elitecore.aaa.radius.service.base.policy.handler.ExternalCommunicationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.GroupExternalCommunicationEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadiusEsiGroupData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommGroup;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.AsyncRequestExecutor;

public class RadAuthGroupProxyHandler extends ExternalCommunicationHandler<RadAuthRequest, RadAuthResponse> implements RadAuthServiceHandler {

	private static final String MODULE = "AUTH-GROUP-PROXY-HNDLR";

	private final RadiusEsiGroupData radiusEsiGroupData;

	public RadAuthGroupProxyHandler(RadAuthServiceContext serviceContext, GroupExternalCommunicationEntryData data) {
		super(serviceContext, data);

		this.radiusEsiGroupData = data.getRadiusESIGroupData();
	}

	@Override
	public void init() throws InitializationFailedException {
		if(LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing Auth Proxy Communication handler for radius esi group: " + radiusEsiGroupData.getName());
		}
		
		super.init();
		
		if(LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Auth Proxy Communication handler for radius esi group: " + radiusEsiGroupData.getName());
		}
	}

	@Override
	protected String getModule() {
		return MODULE;
	}

	@Override
	protected boolean includeInfoAttributes() {
		return false;
	}

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	@Override
	protected RadUDPCommGroup createGroup() throws InitializationFailedException {

		String esiType = radiusEsiGroupData.getEsiType();

		if (AUTH.typeName.equalsIgnoreCase(esiType) || CORRELATED_RADIUS.typeName.equalsIgnoreCase(esiType) || CHARGING_GATEWAY.typeName.equalsIgnoreCase(esiType)) {
			return getServiceContext().getServerContext().getRadiusESIGroupFactory()
					.getOrCreateGroupInstance(getServiceContext().getServerContext(), radiusEsiGroupData);
		} else {
			throw new InitializationFailedException("Radius esi group with ESI type: " + esiType + " is not supported in authentication service flow");
		}

	}

	@Override
	protected AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> newResponseReceivedExecutor(
			RadUDPRequest remoteRequest, RadUDPResponse remoteResponse) {
		return new AuthResponseReceivedExecutor(remoteRequest, remoteResponse);
	}

	@Override
	protected AsyncRequestExecutor<RadAuthRequest, RadAuthResponse> newRequestTimeoutExecutor(
			RadUDPRequest remoteRequest) {
		return  new AuthRequestTimeoutExecutor(getData().isAcceptOnTimeout());
	}
}
