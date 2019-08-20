package com.elitecore.aaa.radius.threegpp.acct;

import com.elitecore.aaa.core.threegpp.Base3GPPHandler;
import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctVendorSpecificHandler;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceContext;

public class RadAcct3GPP2Handler extends Base3GPPHandler implements RadAcctVendorSpecificHandler {

	public RadAcct3GPP2Handler(ServiceContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws InitializationFailedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEligible(RadAcctRequest request) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void handleRequest(RadAcctRequest request, RadAcctResponse response) {
		// TODO Auto-generated method stub
		
	}

}
