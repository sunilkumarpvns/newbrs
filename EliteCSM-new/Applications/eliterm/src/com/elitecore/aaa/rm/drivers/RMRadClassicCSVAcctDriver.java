package com.elitecore.aaa.rm.drivers;

import com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.drivers.RadClassicCSVAcctDriver;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RMRadClassicCSVAcctDriver extends RadClassicCSVAcctDriver implements RMChargingDriver{

	private static String MODULE = "RM-RAD-CLASSIC-CSV-ACCT-DRVR";
	
	public RMRadClassicCSVAcctDriver(AAAServerContext serverContext,
			ClassicCSVAcctDriverConfiguration driverConfiguration) {
	
		super(serverContext, driverConfiguration);
	}

	@Override
	public void handleRequest(ServiceRequest request, ServiceResponse response)
			throws DriverProcessFailedException {
		RadServiceRequest serviceRequest = (RadServiceRequest)request;
		RadServiceResponse serviceResponse = (RadServiceResponse)response;
		if(serviceRequest.getPacketType()  == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
			((RadServiceResponse)response).setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
			handleAccountingRequest(request);
		}else{
			serviceResponse.setProcessingCompleted(true);
			serviceResponse.setFurtherProcessingRequired(false);
			serviceResponse.markForDropRequest();
			LogManager.getLogger().warn(MODULE, "Invalid packet received. Packet Type: " +serviceRequest.getPacketType());			
		}
	}
	
	@Override
	public int getType() {
		return DriverTypes.RM_CLASSIC_CSV_ACCT_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RM_CLASSIC_CSV_ACCT_DRIVER.name();
	}
	
}
