package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gy;

import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.EventMapping;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;


import static com.elitecore.corenetvertex.constants.PCRFEvent.AUTHENTICATE;
import static com.elitecore.corenetvertex.constants.PCRFEvent.DIRECT_DEBITING;
import static com.elitecore.corenetvertex.constants.PCRFEvent.REFUND_ACCOUNT;

/**
 * 
 * @author Jay Trivedi
 *
 */

public class GyEventMapping  implements EventMapping {

    @Override
    public void apply(DiameterRequest diameterRequest, PCRFRequest pcrfRequest) {
       
        if(diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE).getInteger() == DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST){
            pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_START);
            pcrfRequest.addPCRFEvent(AUTHENTICATE);
			pcrfRequest.addPCRFEvent(PCRFEvent.QUOTA_MANAGEMENT);
        }else if(diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE).getInteger() == DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST){
            pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_UPDATE);
            pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
			pcrfRequest.addPCRFEvent(PCRFEvent.QUOTA_MANAGEMENT);
        }else if(diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE).getInteger() == DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST){
            pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_STOP);
            pcrfRequest.addPCRFEvent(AUTHENTICATE);
			pcrfRequest.addPCRFEvent(PCRFEvent.QUOTA_MANAGEMENT);
        }else if(diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE).getInteger() == DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST) {
			pcrfRequest.addPCRFEvent(AUTHENTICATE);
            long requestedAction = diameterRequest.getAVP(DiameterAVPConstants.REQUESTED_ACTION).getInteger();
            if (DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_DIRECT_DEBITING == requestedAction) {
                pcrfRequest.addPCRFEvent(DIRECT_DEBITING);
            } else if(DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_REFUND_ACCOUNT == requestedAction){
                pcrfRequest.addPCRFEvent(REFUND_ACCOUNT);
            }
		}
    }
}
