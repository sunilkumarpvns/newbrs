package com.elitecore.aaa.radius.service.acct.handlers;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.handlers.RadVendorSpecificHandler;

public interface RadAcctVendorSpecificHandler extends RadVendorSpecificHandler {

	public boolean isEligible (RadAcctRequest request);
	public void handleRequest(RadAcctRequest request,RadAcctResponse response);
}
