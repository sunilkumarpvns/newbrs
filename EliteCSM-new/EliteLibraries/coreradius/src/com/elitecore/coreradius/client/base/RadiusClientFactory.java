package com.elitecore.coreradius.client.base;

import com.elitecore.coreradius.client.coa.RadiusCOAClient;
import com.elitecore.coreradius.client.radius.DefaultRadiusClient;
import com.elitecore.coreradius.client.radius.RadiusAcctClient;
import com.elitecore.coreradius.client.radius.RadiusAuthClient;
import com.elitecore.coreradius.client.radius.RadiusStatusServerClient;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * Radius Client Factory will construct the Object of actual Radius Client type depending upon the
 * Type of Request.
 * 
 * @author tejasmudgal
 *
 */
public class RadiusClientFactory {

	public RadiusClientFactory() {
	}

	/**
	 * Returns the Actual Object of the Radius Client depending upon the
	 * type of Request.
	 * @param packetType
	 * @return IRadiusClient
	 */
	public static IRadiusClient getRadiusClient(int packetType) {
		
		if (packetType == RadiusConstants.ACCESS_REQUEST_MESSAGE )
			return new RadiusAuthClient();
		else if (packetType == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE || packetType == RadiusConstants.ACCOUNTING_MESSAGE)
			return new RadiusAcctClient();
		else if (packetType == RadiusConstants.COA_REQUEST_MESSAGE || packetType == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE)
			return new RadiusCOAClient();
		else if(packetType == RadiusConstants.STATUS_SERVER_MESSAGE)
			return new RadiusStatusServerClient();
		else
			return new DefaultRadiusClient();
	}
}
