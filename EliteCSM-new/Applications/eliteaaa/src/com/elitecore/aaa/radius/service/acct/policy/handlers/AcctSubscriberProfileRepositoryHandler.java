package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.SubscriberProfileRepositoryHandler;
import com.elitecore.aaa.radius.subscriber.RadiusSubscriberProfileRepository;
import com.elitecore.aaa.radius.subscriber.conf.RadiusSubscriberProfileRepositoryDetails;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * This handler is used to fetch the subscriber profile in the accounting flow.
 * 
 * @author narendra.pathai
 *
 */
public class AcctSubscriberProfileRepositoryHandler 
extends SubscriberProfileRepositoryHandler<RadAcctRequest, RadAcctResponse>
implements RadAcctServiceHandler {

	private static final String MODULE = "ACCT-SPR-HANDLR";
	private final RadAcctServiceContext serviceContext;
	private final RadiusSubscriberProfileRepositoryDetails data;

	public AcctSubscriberProfileRepositoryHandler(
			RadAcctServiceContext serviceContext,
			RadiusSubscriberProfileRepositoryDetails data) {
		this.serviceContext = serviceContext;
		this.data = data;
	}

	@Override
	public boolean isEligible(RadAcctRequest request, RadAcctResponse response) {
		return true;
	}

	@Override
	public void init() throws InitializationFailedException {
		RadiusSubscriberProfileRepository spr = new RadiusSubscriberProfileRepository(serviceContext, data);
		spr.init();
		setSubscriberProfileRepository(spr);
	}

	@Override
	protected void actionOnProfileNotFound(RadAcctRequest request, 
			RadAcctResponse response) {
		RadiusProcessHelper.dropResponse(response);
		
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Subscriber profile not found, " 
					+ "dropping request");
		}
	}

	@Override
	protected String getModule() {
		return MODULE;
	}
}
