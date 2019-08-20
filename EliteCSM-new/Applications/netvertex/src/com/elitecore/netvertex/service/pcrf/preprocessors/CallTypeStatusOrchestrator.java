package com.elitecore.netvertex.service.pcrf.preprocessors;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFRequestHandlerPreProcessor;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import org.apache.commons.lang.StringUtils;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class CallTypeStatusOrchestrator implements PCRFRequestHandlerPreProcessor {

    private static final String MODULE = "CALL-TYPE-STATUS-ORCHESTRATOR";

    private final NetVertexServerContext serverContext;

    public CallTypeStatusOrchestrator(NetVertexServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void process(PCRFRequest request, PCRFResponse response) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Call-Type status orchestrator process started");
        }

        SystemParameterConfiguration callingConfiguration = serverContext.getServerConfiguration().getSystemParameterConfiguration();

        String callingOperator = callingConfiguration.getOperator();
        String callingCountry = callingConfiguration.getCountry();
        if (Strings.isNullOrEmpty(callingOperator) || Strings.isNullOrEmpty(callingCountry)) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping call-type status orchestrator process. Reason: Operator or Country is not configured in System Parameter");
            }

            return;
        }

        identifyCallTypeStatus(response, callingOperator, callingCountry);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Call-Type status orchestrator process completed");
        }

    }

    private void identifyCallTypeStatus(PCRFResponse response, String callingOperator, String callingCountry) {

        if(PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_TRUE.val.equals(response.getAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val))){
            response.setAttribute(PCRFKeyConstants.CS_CALLTYPE.val, PCRFKeyValueConstants.CALLTYPE_INTERNATIONAL_ROAMING.val);
            return;
        }

        String calledCountry = response.getAttribute(PCRFKeyConstants.CS_CALLED_COUNTRY.getVal());
        if(Strings.isNullOrEmpty(calledCountry) == false && callingCountry.equals(calledCountry) == false){
            response.setAttribute(PCRFKeyConstants.CS_CALLTYPE.val, PCRFKeyValueConstants.CALLTYPE_INTERNATIONAL.val);
            return;
        }

        String calledOperator = response.getAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal());

        if(StringUtils.isNotEmpty(calledOperator)) {
          if(callingOperator.equals(calledOperator) == false){
              response.setAttribute(PCRFKeyConstants.CS_CALLTYPE.val, PCRFKeyValueConstants.CALLTYPE_OFFNET.val);
              return;
          } else {
              response.setAttribute(PCRFKeyConstants.CS_CALLTYPE.val, PCRFKeyValueConstants.CALLTYPE_ONNET.val);
          }
        }

    }
}
