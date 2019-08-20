package com.elitecore.aaa.radius.service.acct.handlers;

import static com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.EsiType.ACCT;
import static com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.EsiType.CHARGING_GATEWAY;
import static com.elitecore.aaa.radius.systemx.esix.udp.impl.RadiusEsiGroup.EsiType.CORRELATED_RADIUS;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.AcctAsyncRequestExecutors.AcctResponseReceivedExecutor;
import com.elitecore.aaa.radius.service.base.policy.handler.ExternalCommunicationHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.GroupExternalCommunicationEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.RadiusEsiGroupData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommGroup;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.AsyncRequestExecutor;

public class RadAcctGroupProxyHandler extends ExternalCommunicationHandler<RadAcctRequest, RadAcctResponse> implements RadAcctServiceHandler {
	
	private static final String MODULE = "ACCT-GROUP-PROXY-HNDLR";
	
	private final RadiusEsiGroupData radiusEsiGroupData;
	
	public RadAcctGroupProxyHandler(RadAcctServiceContext serviceContext,
			GroupExternalCommunicationEntryData data) {
		super(serviceContext, data);
		
		this.radiusEsiGroupData = data.getRadiusESIGroupData();
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing Rad Accounting proxy handler for radius esi group: " + radiusEsiGroupData.getName());
		}
		super.init();
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Successfully initialized Rad Accounting proxy handler for radius esi group: " + radiusEsiGroupData.getName());
		}
	}
	
	protected AsyncRequestExecutor<RadAcctRequest, RadAcctResponse> newResponseReceivedExecutor(
			RadUDPRequest remoteRequest, RadUDPResponse remoteResponse) {
		return new AcctResponseReceivedExecutor(remoteResponse);
	}
	
	@Override
	protected boolean includeInfoAttributes() {
		return false;
	}


	@Override
	protected String getModule() {
		return MODULE;
	}
	
	@Override
	public boolean isEligible(RadAcctRequest request, RadAcctResponse response) {
		return true;
	}
	
	@Override
	protected RadUDPCommGroup createGroup() throws InitializationFailedException {

		String esiType = radiusEsiGroupData.getEsiType();

		if (ACCT.typeName.equalsIgnoreCase(esiType) || CORRELATED_RADIUS.typeName.equalsIgnoreCase(esiType) || CHARGING_GATEWAY.typeName.equalsIgnoreCase(esiType)) {
			return getServiceContext().getServerContext().getRadiusESIGroupFactory()
					.getOrCreateGroupInstance(getServiceContext().getServerContext(), radiusEsiGroupData);
		} else {
			throw new InitializationFailedException("Radius esi group with ESI type: " + esiType + " is not supported in accounting service flow");
		}
	}
	
}
