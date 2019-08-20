package com.elitecore.netvertex.gateway.diameter.application;

import static com.elitecore.commons.logging.LogManager.getLogger;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;

/**
 * LicenseLimitValidator will checks for License Limit on DIAMETER_INITIAL_REQUEST only.
 * 
 * IF limit is exceeded THEN
 * 	It will check for OverLoad Action Configuration
 * 	In case of REJECT Action, 
 * 		Fail Validation Result will be generated + Diameter Answer will be generated with overload result code.
 * 	In case of DROP Action,
 * 		Drop Validation Result will be generated
 * 
 * 
 * @author Chetan.Sankhala
 */
public class LicenseLimitValidator implements RequestValidator {

	private final static String MODULE = "REQ-VALIDATOR";
	private DiameterGatewayControllerContext context;

	public LicenseLimitValidator(@Nonnull DiameterGatewayControllerContext context) {
		this.context = context;
	}

	@Override
	public ValidationResult validate(DiameterRequest request) {

		if (isInitialRequest(request) == false) {
			return ValidationResult.success();
		}

		long currentMPM = context.getCurrentMessagePerMinute();
		long licencedMPM = context.getServerContext().getLicencedMessagePerMinute();
		if (licencedMPM > 0 && currentMPM > licencedMPM) {
			OverloadAction overloadAction = context.getActionOnOverload();

			if (overloadAction == OverloadAction.REJECT) {
				return ValidationResult.fail(createAnswerOverloadResultCode(request));
			} else {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Dropping request with session-ID: " + request.getAVPValue(DiameterAVPConstants.SESSION_ID)
							+ ". Reason: Current load exceed valid TPS");
				}
				context.getStackContext().updateDiameterStatsPacketDroppedStatistics(request, request.getRequestingHost());
				return ValidationResult.drop();
			}
		}

		return ValidationResult.success();
	}

	public boolean isInitialRequest(DiameterRequest request) {
		IDiameterAVP requestTypeAVP = request.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
		return (requestTypeAVP.getInteger() == DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST);
	}

	private DiameterAnswer createAnswerOverloadResultCode(DiameterRequest diameterRequest) {

		String sessionId = diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID);

		int resultCode = context.getOverloadResultCode();

		if (getLogger().isWarnLogLevel()) {
			getLogger().warn(MODULE, "Sending " + resultCode + " for rejected Diameter Initial Request"
					+ " for Session-ID: " + sessionId + ". Reason: Current load exceed valid TPS");
		}

		DiameterAnswer answer = new DiameterAnswer(diameterRequest);
		answer.addAvp(DiameterAVPConstants.RESULT_CODE, resultCode);
		return answer;
	}

}
