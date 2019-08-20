package com.elitecore.aaa.radius.wimax.acct;

import com.elitecore.aaa.core.conf.SPIKeyConfiguration;
import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.wimax.BaseWimaxHandler;
import com.elitecore.aaa.core.wimax.WimaxRequest;
import com.elitecore.aaa.core.wimax.WimaxResponse;
import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctVendorSpecificHandler;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceContext;

public class RadAcctWimaxHandler extends BaseWimaxHandler<RadAcctRequest, RadAcctResponse> implements RadAcctVendorSpecificHandler {

	public RadAcctWimaxHandler(ServiceContext context, WimaxConfiguration wimaxConfiguration, SPIKeyConfiguration spiKeyConfiguration, 
			WimaxSessionManager wimaxSessionManager, EAPSessionManager eapSessionManager, KeyManager keyManager) {
		super(context, wimaxConfiguration, spiKeyConfiguration, wimaxSessionManager, eapSessionManager, keyManager);
		
	}

	@Override
	public void init() throws InitializationFailedException {
		
		
	}

	@Override
	public boolean isEligible(RadAcctRequest request) {
		return false;
	}

	@Override
	public void handleRequest(RadAcctRequest request, RadAcctResponse response) {

		
	}

	@Override
	public WimaxRequest formWimaxRequest(RadAcctRequest request) {
		
		return null;
	}

	@Override
	public WimaxResponse formWimaxResponse(RadAcctRequest request, RadAcctResponse response) {
		
		return null;
	}

	@Override
	public void createServiceResponse(RadAcctRequest request,
			RadAcctResponse response, WimaxRequest wimaxRequest,
			WimaxResponse wimaxResponse) {
		
		
	}

}
