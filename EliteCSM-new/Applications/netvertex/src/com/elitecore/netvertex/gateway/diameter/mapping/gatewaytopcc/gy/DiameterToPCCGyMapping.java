package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gy;

import java.util.Objects;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.core.data.ServiceGuide;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.EventMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class DiameterToPCCGyMapping implements DiameterToPCCPacketMapping {

	private final EventMapping eventMapping;
	private final MSCCMapping msccMapping;

	public DiameterToPCCGyMapping() {

		eventMapping = new GyEventMapping();
		msccMapping = new MSCCMapping();
	}

	@Override
	public void apply(PCRFRequestMappingValueProvider valueProvider) {

		DiameterRequest diameterRequest = valueProvider.getDiameterRequest();
		PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();
		
		msccMapping.apply(valueProvider);
		
		if (diameterRequest.getAVP(DiameterAVPConstants.EVENT_TIMESTAMP) != null) {
			pcrfRequest.setAttribute(PCRFKeyConstants.EVENT_TIMESTAMP.getVal(), diameterRequest.getAVPValue(DiameterAVPConstants.EVENT_TIMESTAMP));
		}

		AvpGrouped serviceInformation = (AvpGrouped) diameterRequest.getAVP(DiameterAVPConstants.TGPP_SERVICE_INFORMATION);
		if (serviceInformation != null) {
			mapTGPPImsInformationAvps(pcrfRequest, serviceInformation);
			mapServiceInfoAvps(serviceInformation, pcrfRequest);
		}

		if (diameterRequest.getAVP(DiameterAVPConstants.MULTIPLE_SERVICES_INDICATOR) != null) {
			long multipleServicesIndicator = diameterRequest.getAVP(DiameterAVPConstants.MULTIPLE_SERVICES_INDICATOR).getInteger();
			if (multipleServicesIndicator == DiameterAttributeValueConstants.DIAMETER_MULTIPLE_SERVICE_INDICATOR_SUPPORTED) {
				pcrfRequest
						.setAttribute(PCRFKeyConstants.MULTIPLE_SERVICE_INDICATOR.val, PCRFKeyValueConstants.MULTIPLE_SERVICE_INDICATOR_MULTIPLE_SERVICES_SUPPORTED.val);
			} else {
				pcrfRequest
						.setAttribute(PCRFKeyConstants.MULTIPLE_SERVICE_INDICATOR.val, PCRFKeyValueConstants.MULTIPLE_SERVICE_INDICATOR_MULTIPLE_SERVICES_NOT_SUPPORTED.val);
			}
		} else {
			pcrfRequest
					.setAttribute(PCRFKeyConstants.MULTIPLE_SERVICE_INDICATOR.val, PCRFKeyValueConstants.MULTIPLE_SERVICE_INDICATOR_MULTIPLE_SERVICES_NOT_SUPPORTED.val);
		}

		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));

		applyServiceGuiding(pcrfRequest, valueProvider);

		applyConfSpecificMappings(valueProvider.getConfiguration(), pcrfRequest);

		eventMapping.apply(diameterRequest, pcrfRequest);

		applyEventRequestMapping(pcrfRequest, diameterRequest);
	}

	private void applyEventRequestMapping(PCRFRequest pcrfRequest, DiameterRequest diameterRequest) {

		if (DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST != diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE).getInteger()) {
			return;
		}

		long requestedAction = diameterRequest.getAVP(DiameterAVPConstants.REQUESTED_ACTION).getInteger();
		if (DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_DIRECT_DEBITING == requestedAction) {
			pcrfRequest.setAttribute(PCRFKeyConstants.REQUESTED_ACTION.val, PCRFKeyValueConstants.REQUESTED_ACTION_DIRECT_DEBITING.val);
		} else if (DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_REFUND_ACCOUNT == requestedAction) {
			pcrfRequest.setAttribute(PCRFKeyConstants.REQUESTED_ACTION.val, PCRFKeyValueConstants.REQUESTED_ACTION_REFUND_ACCOUNT.val);
		} else if (DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_CHECK_BALANCE == requestedAction) {
			pcrfRequest.setAttribute(PCRFKeyConstants.REQUESTED_ACTION.val, PCRFKeyValueConstants.REQUESTED_ACTION_CHECK_BALANCE.val);
		} else if (DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_PRICE_ENQUIRY == requestedAction) {
			pcrfRequest.setAttribute(PCRFKeyConstants.REQUESTED_ACTION.val, PCRFKeyValueConstants.REQUESTED_ACTION_PRICE_ENQUIRY.val);
		}
	}

	private void applyServiceGuiding(PCRFRequest pcrfRequest, PCRFRequestMappingValueProvider valueProvider){

		for(ServiceGuide serviceGuide: valueProvider.getConfiguration().getServiceGuides()){

			if(serviceGuide.getCondition() == null || serviceGuide.getCondition().evaluate(valueProvider)){

				pcrfRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, serviceGuide.getServiceId());

				if (CommonConstants.DATA_SERVICE_ALIAS.equals(serviceGuide.getServiceId())) {
					pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GY.val);
				} else {
					pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.RO.val);
				}

				break;
			}
		}
	}

	private void mapTGPPImsInformationAvps(PCRFRequest pcrfRequest, AvpGrouped serviceInformation) {
		AvpGrouped imsInformation = (AvpGrouped) serviceInformation.getSubAttribute(DiameterAVPConstants.TGPP_IMS_INFORMATION);
		if (Objects.isNull(imsInformation)) {
			return;
		}

		addMappingWithStripPrefix(imsInformation, pcrfRequest, DiameterAVPConstants.TGPP_CALLING_PARTY_ADDRESS, PCRFKeyConstants.CS_CALLING_STATION_ID.getVal());
		addMappingWithStripPrefix(imsInformation, pcrfRequest, DiameterAVPConstants.TGPP_CALLED_PARTY_ADDRESS, PCRFKeyConstants.CS_CALLED_STATION_ID.getVal());
		addMapping(imsInformation, pcrfRequest, DiameterAVPConstants.TGPP_NUMBER_PORTABILITY_ROUTING_INFORMATION, PCRFKeyConstants.LRN.getVal());

	}

	private void mapServiceInfoAvps(AvpGrouped serviceInformation, PCRFRequest pcrfRequest) {
		AvpGrouped psInformation = (AvpGrouped) serviceInformation.getSubAttribute(DiameterAVPConstants.TGPP_PS_INFORMATION);

		if(Objects.isNull(psInformation)) {
			return;
		}
        addMapping(psInformation,pcrfRequest,DiameterAVPConstants.TGPP_SGSN_MCC_MNC,PCRFKeyConstants.CS_SGSN_MCC_MNC.getVal());
		addMapping(psInformation,pcrfRequest,DiameterAVPConstants.TGPP_IMSI_MCC_MNC,PCRFKeyConstants.SUB_MCC_MNC.getVal());
	}

	private void applyConfSpecificMappings(DiameterGatewayConfiguration configuration, PCRFRequest pcrfRequest) {
		pcrfRequest.setAttribute(PCRFKeyConstants.REVALIDATION_MODE.val, configuration.getRevalidationMode().val);
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val, configuration.getName());
	}

	private void addMapping(AvpGrouped avpGrouped, PCRFRequest request, String avpId, String pcrfKey) {
		IDiameterAVP attribute = avpGrouped.getSubAttribute(avpId);
		if(attribute != null) {
			request.setAttribute(pcrfKey, attribute.getStringValue());
		}
	}

	private void addMappingWithStripPrefix(AvpGrouped avpGrouped, PCRFRequest request, String avpId, String pcrfKey) {
		IDiameterAVP attribute = avpGrouped.getSubAttribute(avpId);

		if (attribute != null) {
			String attributeStringValue = attribute.getStringValue();

			attributeStringValue = attributeStringValue.startsWith(CommonConstants.STRIP_PREFIX) ? attributeStringValue.substring(CommonConstants.STRIP_PREFIX.length())
					: attributeStringValue;

			request.setAttribute(pcrfKey, attributeStringValue);
		}
	}
}
