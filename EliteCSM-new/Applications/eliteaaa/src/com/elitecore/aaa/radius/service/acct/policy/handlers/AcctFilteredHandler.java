package com.elitecore.aaa.radius.service.acct.policy.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.base.policy.handler.FilteredHandler;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AcctFilteredHandler extends FilteredHandler<RadAcctRequest, RadAcctResponse>
implements RadAcctServiceHandler {

	public AcctFilteredHandler(String rulesetString, RadAcctServiceHandler handler) {
		super(rulesetString, handler);
	}
}
