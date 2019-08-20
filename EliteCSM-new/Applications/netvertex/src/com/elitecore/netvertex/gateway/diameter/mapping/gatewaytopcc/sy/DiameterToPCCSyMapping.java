package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.sy;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFResponseMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class DiameterToPCCSyMapping implements DiameterToPCCPacketMapping {

    private static final String MODULE = "DIAMETER-TO-PCC-SY-MAPPING";

    @Override
    public void apply(PCRFResponseMappingValueProvider valueProvider) {

        DiameterAnswer diameterPacket = valueProvider.getDiameterAnswer();
        PCRFResponse pcrfResponse = valueProvider.getPcrfResponse();

        String sessionIdVal = diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID);

        if (pcrfResponse.getAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val) == null) {
            ResultCodeCategory resultCodeCategory = ResultCodeCategory.getResultCodeCategory(diameterPacket.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger());
            if (resultCodeCategory == ResultCodeCategory.RC1XXX || resultCodeCategory == ResultCodeCategory.RC2XXX){

                pcrfResponse.setAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val, sessionIdVal);
            }
        }


        if(diameterPacket.isResponse()){
            IDiameterAVP resultCodeAVP = diameterPacket.getAVP(DiameterAVPConstants.RESULT_CODE);
            if(resultCodeAVP != null){
                long resultCodeVal = resultCodeAVP.getInteger();

                if(resultCodeVal == ResultCode.DIAMETER_SUCCESS.code){
                    pcrfResponse.setAttribute(PCRFKeyConstants.SY_COMMUNICATION.val, PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);
                } else if(resultCodeVal == ResultCode.DIAMETER_UNKNOWN_SESSION_ID.code){
                    pcrfResponse.setAttribute(PCRFKeyConstants.SY_COMMUNICATION.val, PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_SESSION_ID.val);

                } else if(resultCodeVal == ResultCode.DIAMETER_USER_UNKNOWN.code){
                    pcrfResponse.setAttribute(PCRFKeyConstants.SY_COMMUNICATION.val, PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val);

                } else {
                    pcrfResponse.setAttribute(PCRFKeyConstants.SY_COMMUNICATION.val, String .valueOf(resultCodeVal));
                }
            }else {
                if(getLogger().isLogLevel(LogLevel.WARN))
                    getLogger().warn(MODULE, "Add "+PCRFKeyValueConstants.SY_COMMUNICATION_FAIL.val +" val for Sycommunication key in PCRFRequest for SySessionId: "+ diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID)+". Reason: Result-Code("
                            + DiameterAVPConstants.RESULT_CODE + ") AVP not found from DiameterResponse" );
                pcrfResponse.setAttribute(PCRFKeyConstants.SY_COMMUNICATION.val, PCRFKeyValueConstants.SY_COMMUNICATION_FAIL.val);
            }
        }




        if((diameterPacket.isResponse() && diameterPacket.getCommandCode() != CommandCode.SPENDING_LIMIT.code)
                || (diameterPacket.isRequest() && diameterPacket.getCommandCode() != CommandCode.SPENDING_STATUS_NOTIFICATION.code)){
            //no need to provide log
            return;
        }

        List<IDiameterAVP> policyCounterStatusReportAVPs = diameterPacket.getAVPList(DiameterAVPConstants.TGPP_POLICY_COUNTER_STATUS_REPORT);

        if(policyCounterStatusReportAVPs == null || policyCounterStatusReportAVPs.isEmpty()){
            if(getLogger().isLogLevel(LogLevel.WARN))
                getLogger().debug(MODULE, "PolicyCounterStatus AVP not found form " + CommandCode.fromCode(diameterPacket.getCommandCode()));
            return;
        }

        for(IDiameterAVP diameterAVP : policyCounterStatusReportAVPs){
            AvpGrouped policyCounterStatusReportAVP = (AvpGrouped) diameterAVP;

            IDiameterAVP policyCounterIdAVP = policyCounterStatusReportAVP.getSubAttribute(DiameterAVPConstants.TGPP_POLICY_COUNTER_IDENTIFIER);
            if(policyCounterIdAVP == null){
                continue;
            }

            IDiameterAVP policyCounterStatusAVP = policyCounterStatusReportAVP.getSubAttribute(DiameterAVPConstants.TGPP_POLICY_COUNTER_STATUS);
            if(policyCounterStatusAVP == null){
                continue;
            }

            String id = policyCounterIdAVP.getStringValue();
            String status = policyCounterStatusAVP.getStringValue();

            pcrfResponse.setAttribute(PCRFKeyConstants.SY_COUNTER_PREFIX.val+id, status);
        }
    }
}
