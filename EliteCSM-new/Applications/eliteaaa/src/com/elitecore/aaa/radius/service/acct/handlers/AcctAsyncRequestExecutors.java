package com.elitecore.aaa.radius.service.acct.handlers;

import java.util.List;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.packet.IRadiusPacket;

public class AcctAsyncRequestExecutors {

	private AcctAsyncRequestExecutors() {
	}

	public static class AcctResponseReceivedExecutor implements AsyncRequestExecutor<RadAcctRequest, RadAcctResponse> {
		private RadUDPResponse udpResponse;

		public AcctResponseReceivedExecutor(RadUDPResponse udpResponse) {
			this.udpResponse = udpResponse;
		}

		@Override
		public void handleServiceRequest(RadAcctRequest serviceRequest,
				RadAcctResponse serviceResponse) {

			IRadiusPacket proxyResponse = udpResponse.getRadiusPacket();
			List<IRadiusAttribute> radiusAttributes = proxyResponse.getRadiusAttributes();
			for (IRadiusAttribute radAttribute : radiusAttributes) {
				if (radAttribute.isVendorSpecific()) {
					List<IRadiusAttribute> list = ((VendorSpecificAttribute)radAttribute).getAttributes();
					if (list != null){
						for (IRadiusAttribute radiusAttribute : list) {
							serviceResponse.addAttribute(radiusAttribute);
						}
					}
				} else {					
					serviceResponse.addAttribute(radAttribute);
				}
			}
			serviceResponse.setPacketType(proxyResponse.getPacketType());
			serviceResponse.setProcessingCompleted(true);

		}
	}

}
