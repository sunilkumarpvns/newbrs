package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.sy;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCToDiameterRequestMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalator;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class PCCToDiameterSyMapping implements PCRFToDiameterPacketMapping {

	private static final String MODULE = "SLR-MAPPING";
	private PCCToDiameterRequestMapping pccToDiameterRequestMapping;

	public PCCToDiameterSyMapping() {
		this.pccToDiameterRequestMapping = new PCCToDiameterRequestMapping();
	}

	@Override
	public void apply(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {

		PCRFResponse response = valueProvider.getPcrfResponse();

		addDestinationRealm(accumalator, response);

		addRequestType(accumalator, response);

		addSubscribetIDAVPfromPCRFResponse(response, accumalator);

		addFramedIPAddress(response, accumalator);

		pccToDiameterRequestMapping.apply(valueProvider, accumalator);
	}

	/*
	 * use value of PCRFKey = SY_GATEWAY_REALM as destination realm.
	 * 
	 * Value of "SY_GATEWAY_REALM" would be realm of gateway selected by
	 * PCRFSyHandler
	 */
	private void addDestinationRealm(AvpAccumalator accumalator,
			PCRFResponse pcrfResponse) {

		String syGatewayRealm = pcrfResponse.getAttribute(PCRFKeyConstants.SY_GATEWAY_REALM.val);
		IDiameterAVP destinationRealm = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.DESTINATION_REALM);
		if (syGatewayRealm != null) {
			destinationRealm.setStringValue(syGatewayRealm);
		} else {
			destinationRealm.setStringValue("*");
			if (getLogger().isLogLevel(LogLevel.DEBUG)) {
				getLogger().debug(MODULE, "Set * as Destination-Realm (" + DiameterAVPConstants.SUBSCRIPTION_ID
						+ ") AVP value. Reason: value for PCRFKey: "
						+ PCRFKeyConstants.SY_GATEWAY_REALM.val + " not found from PCRF Response");
			}
		}

		accumalator.add(destinationRealm);
	}

	private void addFramedIPAddress(PCRFResponse pcrfResponse, AvpAccumalator accumalator) {
		if (pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_IPV4.val) != null) {
			IDiameterAVP framedIPAddressAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.FRAMED_IP_ADDRESS);
			framedIPAddressAVP.setStringValue(pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_IPV4.val));
			accumalator.add(framedIPAddressAVP);
		}

		if (pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_IPV6.val) != null) {
			IDiameterAVP framedIPv6Address = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.FRAMED_IPV6_PREFIX);
			framedIPv6Address.setStringValue(pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_IPV6.val));
			accumalator.add(framedIPv6Address);
		}
	}

	private void addRequestType(AvpAccumalator accumalator,
			PCRFResponse pcrfResponse) {
		IDiameterAVP slRequestTypeAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.TGPP_SL_REQUEST_TYPE);
		if (pcrfResponse.getAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val) != null) {
			slRequestTypeAVP.setInteger(DiameterAttributeValueConstants.TGPP_SL_REQUET_TYPE_INTERMEDIATE_REQUEST);
		} else {
			slRequestTypeAVP.setInteger(DiameterAttributeValueConstants.TGPP_SL_REQUET_TYPE_INITIAL_REQUEST);
		}

		accumalator.add(slRequestTypeAVP);
	}

	private void addSubscribetIDAVPfromPCRFResponse(PCRFResponse pcrfResponse, AvpAccumalator accumalator) {

		try {
			String value = pcrfResponse.getAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.getVal());
			if (value != null) {
				accumalator.add(createSubscriberIdAVP(DiameterAttributeValueConstants.END_USER_E164, value));
			} else {

				value = pcrfResponse.getAttribute(PCRFKeyConstants.SUB_MSISDN.getVal());
				if (value != null) {
					accumalator.add(createSubscriberIdAVP(DiameterAttributeValueConstants.END_USER_E164, value));
				} else {
					if (getLogger().isLogLevel(LogLevel.DEBUG)) {
						getLogger().debug(MODULE, "Unable to add Subscriber-ID (" + DiameterAVPConstants.SUBSCRIPTION_ID
								+ ") AVP  for MSISDN. Reason: value not found from PCRF Response");
					}
				}
			}

			value = pcrfResponse.getAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.getVal());

			if (value != null) {
				accumalator.add(createSubscriberIdAVP(DiameterAttributeValueConstants.END_USER_IMSI, value));
			} else {
				value = pcrfResponse.getAttribute(PCRFKeyConstants.SUB_IMSI.getVal());
				if (value != null) {
					accumalator.add(createSubscriberIdAVP(DiameterAttributeValueConstants.END_USER_IMSI, value));
				} else {
					if (getLogger().isLogLevel(LogLevel.DEBUG)) {
						getLogger().debug(MODULE, "Unable to add Subscriber-ID (" + DiameterAVPConstants.SUBSCRIPTION_ID
								+ ") AVP for IMSI. Reason: value not found from PCRF Response");
					}
				}
			}

			value = pcrfResponse.getAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_SIPURI.getVal());
			if (value != null) {
				accumalator.add(createSubscriberIdAVP(DiameterAttributeValueConstants.END_USER_SIP_URI, value));
			} else {
				value = pcrfResponse.getAttribute(PCRFKeyConstants.SUB_SIP_URL.getVal());
				if (value != null) {
					accumalator.add(createSubscriberIdAVP(DiameterAttributeValueConstants.END_USER_SIP_URI, value));
				} else {
					if (getLogger().isLogLevel(LogLevel.DEBUG)) {
						getLogger().debug(MODULE, "Unable to add Subscriber-ID (" + DiameterAVPConstants.SUBSCRIPTION_ID
								+ ") AVP for SIPURI. Reason: value not found from PCRF Response");
					}
				}
			}

			value = pcrfResponse.getAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_NAI.getVal());
			if (value != null) {
				accumalator.add(createSubscriberIdAVP(DiameterAttributeValueConstants.END_USER_NAI, value));
			} else {
				if (getLogger().isLogLevel(LogLevel.DEBUG)) {
					getLogger().debug(MODULE, "Unable to add Subscriber-ID (" + DiameterAVPConstants.SUBSCRIPTION_ID
							+ ") AVP for NAI. Reason: value not found from PCRF Response");
				}
			}

		} catch (Exception ex) {
			getLogger().error(MODULE, "Error in adding Subscriber-ID-Type (" + DiameterAVPConstants.SUBSCRIPTION_ID_TYPE + ") AVP. Reason: "
					+ ex.getMessage());
			getLogger().trace(MODULE, ex);
		}

	}

	private IDiameterAVP createSubscriberIdAVP(int subscriberIdType, String subscriberIdValue) throws Exception {
		AvpGrouped subscriptionId = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
		IDiameterAVP subscriptionIdType = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
		IDiameterAVP subscriptionIdData = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
		if (subscriptionId == null) {
			throw new Exception("Subscriber-ID (" + DiameterAVPConstants.SUBSCRIPTION_ID + ") AVP. Reason: AVP not found in dictionary");
		}

		if (subscriptionIdType == null) {
			throw new Exception("Subscriber-ID-Type (" + DiameterAVPConstants.SUBSCRIPTION_ID_TYPE + ") AVP. Reason: AVP not found in dictionary");
		}

		if (subscriptionIdData == null) {
			throw new Exception("Subscriber-ID-Data (" + DiameterAVPConstants.SUBSCRIPTION_ID_DATA + ") AVP. Reason: AVP not found in dictionary");
		}

		subscriptionIdType.setInteger(subscriberIdType);
		subscriptionIdData.setStringValue(subscriberIdValue);

		subscriptionId.addSubAvp(subscriptionIdType);
		subscriptionId.addSubAvp(subscriptionIdData);

		return subscriptionId;
	}

}