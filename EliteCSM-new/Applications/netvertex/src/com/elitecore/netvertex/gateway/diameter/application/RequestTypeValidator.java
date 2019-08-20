package com.elitecore.netvertex.gateway.diameter.application;

import static com.elitecore.commons.logging.LogManager.getLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class RequestTypeValidator implements RequestValidator {

	private final static String MODULE = "REQ-TYPE-VAL";

	public RequestTypeValidator() {
	}

	@Override
	public @Nullable ValidationResult validate(@Nonnull DiameterRequest request) {
		IDiameterAVP requestTypeAVP = request.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);

		if (requestTypeAVP == null) {
			return ValidationResult.fail(createAnswerWithMissingAVP(request, DiameterAVPConstants.CC_REQUEST_TYPE));
		}
		long requestType = requestTypeAVP.getInteger();

		if (requestType == DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST
				|| requestType == DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST
				|| requestType == DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST) {
			return ValidationResult.success();
		}

		if (requestTypeAVP.getInteger() == DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST) {
			IDiameterAVP requestedActionAVP = request.getAVP(DiameterAVPConstants.REQUESTED_ACTION);
			
			
			// Currently, OCS takes CHECK BALANCE as default-- review HARSH, SUBHASH
			if (requestedActionAVP == null) {
				return ValidationResult.fail(createAnswerWithMissingAVP(request, DiameterAVPConstants.REQUESTED_ACTION));
			}
			
			if (isValidRequestedAction(requestedActionAVP.getInteger()) == false) {
				return ValidationResult.fail(createAnswerWithInvalidAVPValue(request, DiameterAVPConstants.REQUESTED_ACTION));
			}
			
			return ValidationResult.success();
		}

		return ValidationResult.fail(createAnswerWithInvalidAVPValue(request, DiameterAVPConstants.CC_REQUEST_TYPE));
	}

	private boolean isValidRequestedAction(long requestedAction) {
		return requestedAction == DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_CHECK_BALANCE
				|| requestedAction == DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_DIRECT_DEBITING
				|| requestedAction == DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_PRICE_ENQUIRY
				|| requestedAction == DiameterAttributeValueConstants.DIAMETER_REQUESTED_ACTION_REFUND_ACCOUNT;
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
