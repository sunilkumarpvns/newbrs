package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.rx;

import javax.annotation.Nonnull;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.EventMapping;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

public class RxEventMapping implements EventMapping {

    @Override
    public void apply(@Nonnull DiameterRequest diameterRequest, @Nonnull PCRFRequest pcrfRequest) {

        if (diameterRequest.getCommandCode() == CommandCode.SESSION_TERMINATION.getCode()) {
            pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_STOP);
        } else if (diameterRequest.getCommandCode() == CommandCode.AUTHENTICATION_AUTHORIZATION.getCode()) {
            pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
            pcrfRequest.addPCRFEvent(PCRFEvent.AUTHORIZE);
            pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_START);
        } else {
            pcrfRequest.addPCRFEvent(PCRFEvent.UNKNOWN);
        }
    }
}
