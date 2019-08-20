package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import javax.annotation.Nonnull;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class EventRequestValidator implements RequestValidator {

    private static final String MODULE = "EVENT-REQUEST-VAL";

    @Override
    public ValidationResult validate(@Nonnull DiameterRequest request) {

        IDiameterAVP requestTypeAVP = request.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
        long requestType = requestTypeAVP.getInteger();
        if (requestType != DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST) {
            return ValidationResult.success();
        }

        IDiameterAVP requestedActionAVP = request.getAVP(DiameterAVPConstants.REQUESTED_ACTION);

        // Currently, OCS takes CHECK BALANCE as default-- review HARSH, SUBHASH
        if (requestedActionAVP == null) {
            return ValidationResult.fail(createAnswerWithMissingAVP(request, DiameterAVPConstants.REQUESTED_ACTION));
        }

        if (isValidRequestedAction(requestedActionAVP.getInteger()) == false) {
            return ValidationResult.fail(createAnswerWithInvalidAVPValue(request, DiameterAVPConstants.REQUESTED_ACTION));
        }

        ValidationResult validateRequestAVPs = validateRequestAVPs(request);
        if(validateRequestAVPs.getResult() == ValidationResult.Result.FAIL){
            return validateRequestAVPs;
        }

        return ValidationResult.success();
    }

    private boolean isValidRequestedAction(long requestedAction) {
        return requestedAction == DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_CHECK_BALANCE
                || requestedAction == DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_DIRECT_DEBITING
                || requestedAction == DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_PRICE_ENQUIRY
                || requestedAction == DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_REFUND_ACCOUNT;
    }

    private ValidationResult validateRequestAVPs(DiameterRequest request){

        AvpGrouped msccAVP = (AvpGrouped) request.getAVP(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL);
        if(msccAVP == null){
            if(LogManager.getLogger().isInfoLogLevel())
                LogManager.getLogger().info(MODULE, "Skipping event processing. Reason: Multiple-Service-Credit-Control AVP not found");
            return ValidationResult.fail(createAnswerWithMissingAVP(request, DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL));
        }

        AvpGrouped requestedServiceUnitsAVP = (AvpGrouped) msccAVP.getSubAttribute(DiameterAVPConstants.REQUESTED_SERVICE_UNIT);

        if (requestedServiceUnitsAVP == null) {
            if (LogManager.getLogger().isInfoLogLevel())
                LogManager.getLogger().info(MODULE, "Skipping event processing, Reason: Requested-Service-Unit AVP not found");
            return ValidationResult.fail(createAnswerWithMissingAVP(request, DiameterAVPConstants.REQUESTED_SERVICE_UNIT));
        }

        IDiameterAVP serviceSpecificUnitsAVP = requestedServiceUnitsAVP.getSubAttribute(DiameterAVPConstants.CC_SERVICE_SPECIFIC_UNITS);

        if (serviceSpecificUnitsAVP == null) {
            if (LogManager.getLogger().isInfoLogLevel())
                LogManager.getLogger().info(MODULE, "Skipping event processing, Reason: CC-Service-Specific-Units AVP not found");
            return ValidationResult.fail(createAnswerWithMissingAVP(request, DiameterAVPConstants.CC_SERVICE_SPECIFIC_UNITS));
        }
        return ValidationResult.success();
    }

    private DiameterAnswer createAnswerWithMissingAVP(DiameterRequest request, String failedAVP) {

        String sessionId = request.getAVPValue(DiameterAVPConstants.SESSION_ID);

        if (getLogger().isWarnLogLevel()) {
            getLogger().warn(MODULE, "Unable to process Gy request with session-ID:"
                    + sessionId + ". Reason: " + failedAVP + " not found");
        }

        DiameterAnswer answer = new DiameterAnswer(request, ResultCode.DIAMETER_MISSING_AVP);
        DiameterUtility.addFailedAVP(answer, DiameterDictionary.getInstance().getAttribute(failedAVP));

        return answer;
    }

    private DiameterAnswer createAnswerWithInvalidAVPValue(DiameterRequest request, String failedAVPStr) {

        String sessionId = request.getAVPValue(DiameterAVPConstants.SESSION_ID);
        IDiameterAVP failedAVP = request.getAVP(failedAVPStr);

        if (getLogger().isWarnLogLevel()) {
            getLogger().warn(MODULE, "Unable to process Gy request with session-ID:"
                    + sessionId + " Received invalid " + failedAVPStr + " value: " + failedAVP.getStringValue());
        }

        DiameterAnswer answer = new DiameterAnswer(request, ResultCode.DIAMETER_INVALID_AVP_VALUE);
        DiameterUtility.addFailedAVP(answer, failedAVP);

        return answer;
    }
}
