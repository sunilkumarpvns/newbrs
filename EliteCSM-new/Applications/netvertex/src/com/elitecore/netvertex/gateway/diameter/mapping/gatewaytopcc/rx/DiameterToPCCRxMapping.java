package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.rx;

import java.util.ArrayList;
import java.util.List;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.EventMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.rx.RxEventMapping;
import com.elitecore.netvertex.gateway.diameter.af.MediaComponent;
import com.elitecore.netvertex.gateway.diameter.af.MissingAPVException;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class DiameterToPCCRxMapping implements DiameterToPCCPacketMapping {

    private EventMapping eventMapping;

    public DiameterToPCCRxMapping() {
        this.eventMapping = new RxEventMapping();
    }

	@Override
	public void apply(PCRFRequestMappingValueProvider valueProvider) {

		PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();
		DiameterRequest diameterPacket = valueProvider.getDiameterRequest();

		if (diameterPacket.getAVP(DiameterAVPConstants.FRAMED_IPV6_PREFIX) != null) {
			pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_IPV6.getVal(), diameterPacket.getAVPValue(DiameterAVPConstants.FRAMED_IPV6_PREFIX)
					.substring(5, 22));
		}

		IDiameterAVP sipForkingIndication = diameterPacket.getAVP(DiameterAVPConstants.SIP_FORKING_INDICATION);
		if (sipForkingIndication != null) {
			if (sipForkingIndication.getInteger() == DiameterAttributeValueConstants.SIP_FORKING_INDICATION_SEVERAL_DIALOGUES) {
				pcrfRequest.setAttribute(PCRFKeyConstants.SIP_FORKING_INDICATION.val, PCRFKeyValueConstants.SIP_FORKING_SEVERAL_DIALOGUES.val);
			} else {
				pcrfRequest.setAttribute(PCRFKeyConstants.SIP_FORKING_INDICATION.val, PCRFKeyValueConstants.SIP_FORKING_SINGLE_DIALOGUES.val);
			}
		}

		IDiameterAVP serviceURNAvp = diameterPacket.getAVP(DiameterAVPConstants.TGPP_SERVICE_URN);
		if (serviceURNAvp != null) {
			pcrfRequest.setAttribute(PCRFKeyConstants.SERVICE_URN.val, serviceURNAvp.getStringValue());
			pcrfRequest.setAttribute(PCRFKeyConstants.IMS_EMERGENCY_SESSION.val, PCRFKeyValueConstants.IMS_EMERGENCY_SESSION_TRUE.val);
		}

		setMediaComponent(valueProvider);
        eventMapping.apply(diameterPacket, pcrfRequest);
	}

    private void setMediaComponent(PCRFRequestMappingValueProvider valueProvider) {

	    DiameterPacket diameterPacket = valueProvider.getDiameterRequest();

        List<IDiameterAVP> mediaCompDescAVPs = diameterPacket.getAVPList(DiameterAVPConstants.TGPP_MEDIA_COMPONENT_DESCRIPTION);
        if(mediaCompDescAVPs != null && mediaCompDescAVPs.isEmpty() == false) {

            List<MediaComponent> mediaComponents = new ArrayList<MediaComponent>();
            for(int i=0; i<mediaCompDescAVPs.size(); i++) {
                try {
                    ///FIXME PROVIDE FROM CONTEXT -- HARSH PATEL
                    mediaComponents.addAll(MediaComponent.newMediaComponent((AvpGrouped)mediaCompDescAVPs.get(i), diameterPacket.getAsDiameterRequest(), true));
                } catch (MissingAPVException e) {
                    getLogger().trace(e);
                }
            }


            valueProvider.getPCRFRequest().setMediaComponents(mediaComponents);
        }
    }

}
