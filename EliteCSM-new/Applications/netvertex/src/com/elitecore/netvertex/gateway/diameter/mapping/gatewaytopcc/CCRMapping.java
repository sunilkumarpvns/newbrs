package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by harsh on 6/28/17.
 */
public class CCRMapping implements DiameterToPCCPacketMapping {


    @Override
    public void apply(PCRFRequestMappingValueProvider valueProvider) {

        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();

        DiameterRequest diameterPacket = valueProvider.getDiameterRequest();

        //	set Request-Type AVP
        setRequestType(pcrfRequest, diameterPacket);

        //	set Request-Number AVP
        pcrfRequest.setAttribute(PCRFKeyConstants.REQUEST_NUMBER.getVal(), diameterPacket.getAVPValue(DiameterAVPConstants.CC_REQUEST_NUMBER));

    }

    private void setRequestType(PCRFRequest pcrfRequest, DiameterRequest diameterPacket) {
        long requestType = diameterPacket.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE).getInteger();
        if(requestType == DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST) {
            pcrfRequest.setAttribute(PCRFKeyConstants.REQUEST_TYPE.getVal(), PCRFKeyValueConstants.REQUEST_TYPE_INITIAL_REQUEST.val);
        } else if(requestType == DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST) {
            pcrfRequest.setAttribute(PCRFKeyConstants.REQUEST_TYPE.getVal(), PCRFKeyValueConstants.REQUEST_TYPE_UPDATE_REQUEST.val);
        } else if(requestType == DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST) {
            pcrfRequest.setAttribute(PCRFKeyConstants.REQUEST_TYPE.getVal(), PCRFKeyValueConstants.REQUEST_TYPE_TERMINATION_REQUEST.val);
        } else if(requestType == DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST) {
            pcrfRequest.setAttribute(PCRFKeyConstants.REQUEST_TYPE.getVal(), PCRFKeyValueConstants.REQUEST_TYPE_EVENT_REQUEST.val);
        }
    }

    /**
     * Created by harsh on 6/28/17.
     */
    public static class GxEventMapping implements EventMapping {

        @Override
        public void apply(@Nonnull DiameterRequest diameterRequest, @Nonnull PCRFRequest pcrfRequest) {

            if(diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE).getInteger() == DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST){
                pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_START);
                pcrfRequest.addPCRFEvent(PCRFEvent.AUTHORIZE);
                pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);

            }else if(diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE).getInteger() == DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST){
                pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_UPDATE);
                pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);

                List<IDiameterAVP> diameterAVPs = diameterRequest.getAVPList(DiameterAVPConstants.TGPP_CHARGING_RULE_REPORT);
                boolean flag = true;
                if(diameterAVPs != null && !diameterAVPs.isEmpty()){
                    for(IDiameterAVP diameterAVP : diameterAVPs){
                        AvpGrouped chargingRuleReportAVP = (AvpGrouped) diameterAVP;
                        IDiameterAVP chargingRuleStatusAVP = chargingRuleReportAVP.getSubAttribute(DiameterAVPConstants.TGPP_PCC_RULE_STATUS);
                        if(chargingRuleStatusAVP != null && chargingRuleStatusAVP.getInteger() == DiameterAttributeValueConstants.TGPP_PCC_RULE_STATUS_INACTIVE){
                            flag = false;
                            break;
                        }
                    }
                }
                if(flag){
                    pcrfRequest.addPCRFEvent(PCRFEvent.AUTHORIZE);
                }

            }else if(diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE).getInteger() == DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST){
                pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_STOP);
            }
        }
    }
}
