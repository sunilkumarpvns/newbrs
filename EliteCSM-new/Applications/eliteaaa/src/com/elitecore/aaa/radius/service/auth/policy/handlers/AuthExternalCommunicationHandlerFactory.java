package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.radius.conf.RadESConfiguration.RadESTypeConstants;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthProxyHandler;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.ExternalCommunicationHandler.CommunicatorExceptionPolicy;
import com.elitecore.aaa.radius.service.base.policy.handler.ExternalCommunicationHandler.MaximumFailuresPolicy;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CommunicatorData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CommunicatorGroupData;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.commons.base.Optional;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthExternalCommunicationHandlerFactory {
	private final RadAuthServiceContext serviceContext;

	public AuthExternalCommunicationHandlerFactory(RadAuthServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}
	
	public RadAuthServiceHandler createHandler(ExternalCommunicationEntryData data) throws InitializationFailedException {
		return create(data, new MaximumFailuresPolicy(data.getCommunicatorGroupData().getGroupSize()));
	}
	
	public RadAuthServiceHandler createHandler(ExternalCommunicationEntryData data, CommunicatorExceptionPolicy exceptionPolicy) throws InitializationFailedException {
		return create(data, exceptionPolicy);
	}

	private RadAuthServiceHandler create(ExternalCommunicationEntryData data, CommunicatorExceptionPolicy exceptionPolicy) throws InitializationFailedException {
		Optional<Integer> optionalGroupType = determineType(data.getCommunicatorGroupData());
		if (optionalGroupType.isPresent() == false) {
			throw new InitializationFailedException("Unable to determine type of communicator, Reason no valid communicator found.");
		}
		
		int groupType = optionalGroupType.get();
		RadESTypeConstants type = RadESTypeConstants.get(groupType);
		if (type == null) {
			throw new InitializationFailedException("Unknown type: " + groupType + " for external communication");
		}
		
		RadAuthServiceHandler handler;
		if (RadESTypeConstants.IP_POOL_SERVER == type) {
			handler = new AuthIPPoolCommunicationHandler(serviceContext, data);
		} else if (RadESTypeConstants.RAD_AUTH_PROXY == type) {
			handler = new RadAuthProxyHandler(serviceContext, data, exceptionPolicy);
		} else if (RadESTypeConstants.SESSION_MANAGER == type) {
			handler = new AuthConcurrentLoginCommunicationHandler(serviceContext, data);
		} else if (RadESTypeConstants.CHARGING_GATEWAY == type) {
			handler = new AuthChargingGatewayCommunicationHandler(serviceContext, data);
		} else if (RadESTypeConstants.PREPAID_SERVER == type) {
			handler = new AuthPrepaidChargingCommunicationHandler(serviceContext, data);
		} else {
			throw new InitializationFailedException("Communication with type: " + type.name + " is not supported");
		}
		return handler;
	}
	
	
	private Optional<Integer> determineType(CommunicatorGroupData data) throws InitializationFailedException {
		Integer type = null;
		for (CommunicatorData serverData : data.getCommunicatorDataList()) {
			Optional<DefaultExternalSystemData> esData = serviceContext.getServerContext().getServerConfiguration().getRadESConfiguration().getESData(String.valueOf(serverData.getId()));
			if (esData.isPresent()) {
				type = esData.get().getEsiType();
				break;
			}
		}
		return Optional.of(type);
	}
}
