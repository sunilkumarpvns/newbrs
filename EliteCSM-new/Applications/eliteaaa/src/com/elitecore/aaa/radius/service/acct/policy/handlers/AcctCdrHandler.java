package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.CdrHandlerEntryData;
import com.elitecore.aaa.radius.service.base.policy.handler.CdrHandler;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AcctCdrHandler extends CdrHandler<RadAcctRequest, RadAcctResponse> 
implements RadAcctServiceHandler {
	private static final String MODULE = "ACCT-CDR-HNDLR";
	
	public AcctCdrHandler(RadAcctServiceContext serviceContext, CdrHandlerEntryData cdrHandlerEntryData) {
		super(serviceContext, cdrHandlerEntryData);
	}

	public String getModuleName() {
		return MODULE;
	}
}
