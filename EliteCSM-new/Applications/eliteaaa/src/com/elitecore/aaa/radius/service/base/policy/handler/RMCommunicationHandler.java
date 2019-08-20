package com.elitecore.aaa.radius.service.base.policy.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.base.policy.handler.conf.ExternalCommunicationEntryData;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * This is a support class for all handlers that communicate with Resource Manager type
 * of external systems, namely IP Pool, Concurrent Login and such. All communications 
 * occurring through this class include info attributes.
 * 
 * <p>
 * This class copies all attributes from remote response to the original service response.
 * 
 * @author narendra.pathai
 *
 * @param <T> type of request packet
 * @param <V> type of response packet
 */
public abstract class RMCommunicationHandler<T extends RadServiceRequest, V extends RadServiceResponse> extends ExternalCommunicationHandler<T, V> {

	public RMCommunicationHandler(RadServiceContext<T, V> serviceContext, ExternalCommunicationEntryData data) {
		super(serviceContext, data, CommunicatorExceptionPolicy.CONTINUE);
	}
	
	@Override
	protected final boolean includeInfoAttributes() {
		return true;
	}

	protected void processPacket(IRadiusPacket responseFromRM, RadServiceRequest authRequest, RadServiceResponse authResponse) {
		if (responseFromRM.getPacketType() == RadiusConstants.ACCESS_REJECT_MESSAGE) {
			authResponse.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			authResponse.setResponseMessage(responseFromRM.getRadiusAttribute(RadiusAttributeConstants.REPLY_MESSAGE).getStringValue());
		} else {
			Collection <IRadiusAttribute>attrList = responseFromRM.getRadiusAttributes(true);
			Iterator <IRadiusAttribute>iterator = attrList.iterator();
			while (iterator.hasNext()) {
				IRadiusAttribute attribute = (IRadiusAttribute)iterator.next();
				if (attribute.isVendorSpecific()) {
					List<IRadiusAttribute> list = (ArrayList<IRadiusAttribute>)((VendorSpecificAttribute)attribute).getAttributes();
					if (list != null) {
						for (IRadiusAttribute radiusAttribute : list) {
							authResponse.addAttribute(radiusAttribute);
						}
					}
				} else if(attribute.getID() != RadiusAttributeConstants.REPLY_MESSAGE) {
					authResponse.addAttribute(attribute);
				}
			}
		}
	}
}
