package com.elitecore.aaa.radius.service.auth.handlers;

import java.util.Iterator;

import com.elitecore.aaa.core.subscriber.SubscriberProfileRepositoryAware;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.handlers.RadiusChainHandler;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.subscriber.RadiusSubscriberProfileRepository;
import com.elitecore.core.commons.InitializationFailedException;

public class AuthorizationChainHandler extends RadiusChainHandler<RadAuthRequest, RadAuthResponse, RadAuthServiceHandler> implements RadAuthServiceHandler, SubscriberProfileRepositoryAware {
	@Override
	public void init() throws InitializationFailedException {
		
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}

	@Override
	protected RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> getExecutor(RadAuthRequest request) {
		return request.getExecutor();
	}

	@Override
	public void setSubscriberProfileRepository(RadiusSubscriberProfileRepository spr) {
		Iterator<RadAuthServiceHandler> iterator = iterator();
		while(iterator.hasNext()) {
			RadAuthServiceHandler next = iterator.next();
			if(next instanceof SubscriberProfileRepositoryAware) {
				((SubscriberProfileRepositoryAware)next).setSubscriberProfileRepository(spr);
			}
		}
	}
}
