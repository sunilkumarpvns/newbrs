package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.CoADMHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.CoADMHandlerEntryData;

public class AcctCoADMHandler extends CoADMHandler<RadAcctRequest, RadAcctResponse> 
implements RadAcctServiceHandler {

	private static String MODULE = "ACCT-COA-DM-HNDLR";

	public AcctCoADMHandler(RadAcctServiceContext serviceContext,
			CoADMHandlerEntryData data) {
		super(serviceContext, data);
	}

	public String getModuleName() {
		return MODULE;
	}
}
