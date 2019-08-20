package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * Represents various strategies that should be applied when transient components of
 * service policy are not in a healthy state.
 * 
 * @author narendra.pathai
 *
 */
@XmlEnum
public enum AcctResponseBehaviors {
	/**
	 * Will send the response without any local processing
	 */
	@XmlEnumValue(value = "RESPONSE")
	RESPONSE {
		@Override
		public void apply(RadServiceRequest request, RadServiceResponse response) {
			response.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
			response.setFurtherProcessingRequired(false);
			response.setProcessingCompleted(true);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Applying response behavior RESPONSE, continuing further processing");
			}
		}
	},
	/**
	 * Will drop the request
	 */
	@XmlEnumValue(value = "DROP")
	DROP {
		@Override
		public void apply(RadServiceRequest request, RadServiceResponse response) {
			RadiusProcessHelper.dropResponse(response);
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Applying response behavior DROP, dropping request");
			}
		}
	};
	
	public static final String MODULE = "ACCT-RESPONSE-BEHAVIORS";
	public abstract void apply(RadServiceRequest request, RadServiceResponse response);
}
