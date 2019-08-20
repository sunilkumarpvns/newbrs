package com.elitecore.aaa.radius.threegpp.auth;

import com.elitecore.aaa.core.data.ClientTypeConstant;
import com.elitecore.aaa.core.threegpp.HA3GPP2Request;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

public class RadHA3GPP2RequestHandler extends HA3GPP2Request{

	public RadHA3GPP2RequestHandler(ServiceContext serviceContext) {
		super(serviceContext);
	}

	@Override
	public boolean isEligible(ServiceRequest request, ServiceResponse response) {

		String vendorType =  ClientTypeConstant.getClientType(((RadAuthResponse)response).getClientData().getVendorType());	
		return (vendorType.equalsIgnoreCase("3GPP2-HA"));
	}
}
