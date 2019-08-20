package com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.List;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class RejectBehavior extends DefaultResponseBehavior {

	private static final String MODULE = "REJECT-RESPONSE-BEHAVIOR";
	public static final ResultCode DEFAULT_RESULT_CODE = ResultCode.DIAMETER_UNABLE_TO_COMPLY;
	public static final String DEFAULT_ERROR_MESSAGE = DiameterErrorMessageConstants.DEFAULT_RESPONSE_BEHAVIOR_APPLIED;

	private ResultCode resultCode = DEFAULT_RESULT_CODE;
	private String errorMessage = DEFAULT_ERROR_MESSAGE;

	public RejectBehavior(String parameter) {
		checkNotNull(parameter, "Response behavior parameter is null");
		
		List<String> tokens = tokenize(parameter);
		
		checkArgument(tokens.size() > 0, "Default Response Behavior Parameter is blank");
		
		try {
			parseResultCode(tokens);
			parseErrorMessage(tokens);
		} catch (ParserException ex) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Using default values of Result Code and Error Message, Reason: " + ex.getMessage());
			}
		}
	}

	private void parseErrorMessage(List<String> tokens) {
		if (tokens.size() == 2) {
			errorMessage = tokens.get(1);
		} else {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "No error message configured. Using default value for error message ");
			}
		}
	}

	private void parseResultCode(List<String> tokens) throws ParserException {
		String resultCodeToken = tokens.get(0);
		try {
			
			int intResultCode = Integer.parseInt(resultCodeToken);

			if (ResultCode.isValid(intResultCode)) {
				this.resultCode = ResultCode.fromCode(intResultCode);
			} else {
				throw new ParserException("Configured Result code: " + intResultCode + ", in Default response behavior parameter, is invalid."); 
			}
		} catch (NumberFormatException ex){
			throw new ParserException("Configured Result code: " + resultCodeToken + ", in Default response behavior parameter, is non numeric.", ex);
		}
	}

	private List<String> tokenize(String parameter) {
		return Splitter.on(',').trimTokens().split(parameter);
	}

	@Override
	public void apply(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse) {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Applying default response behavior REJECT for policy " + applicationRequest.getApplicationPolicy().getPolicyName());
		}
		DiameterProcessHelper.rejectResponse(
				applicationResponse, resultCode, errorMessage);
	}
}
