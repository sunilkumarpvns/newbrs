package com.elitecore.netvertex.core.util;

import static com.elitecore.commons.logging.LogManager.getLogger;

import javax.annotation.Nonnull;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.corenetvertex.commons.Predicate;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

public class RequestNumberPredicateForRequest implements Predicate<PCRFRequest, SessionData>{

	private static final String MODULE = "REQ-NUM-PREDICATE";
	private static RequestNumberPredicateForRequest requestNumberPredicate;
	
	static {
		requestNumberPredicate = new RequestNumberPredicateForRequest();
	}
	
	public static RequestNumberPredicateForRequest getInstance() {
		return requestNumberPredicate;
	}
	
	@Override
	public boolean apply(@Nonnull PCRFRequest pcrfRequest, SessionData sessionData) {
		
		if (sessionData == null) {
			return true;
		}
		
		String currentRequestNumber = pcrfRequest.getAttribute(PCRFKeyConstants.REQUEST_NUMBER.val);
		if (Strings.isNullOrBlank(currentRequestNumber)) {
			return true;
		} else {
			return validateRequestNumber(sessionData, currentRequestNumber);
		}
		
	}
	
	private boolean validateRequestNumber(SessionData sessionData, String currentRequestNumber) {
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Validating core session with current request number: " + currentRequestNumber);
		}
		
		String prvRequestNumber = sessionData.getValue(PCRFKeyConstants.REQUEST_NUMBER.val);
		if (prvRequestNumber == null) {
			sessionData.addValue(PCRFKeyConstants.REQUEST_NUMBER.val, currentRequestNumber);
			return true;
		}
		
		long currentRequestNum = 0;
		long prvRequestNum = 0;
		try {
			currentRequestNum = Long.parseLong(currentRequestNumber);
			prvRequestNum = Long.parseLong(prvRequestNumber);
		} catch (NumberFormatException e) {
			getLogger().trace(e);
			return true;
		}
		/*
		 Checking request number with previous request number, for validation.
		 If request numbers are not in sequence, then session cache will be refreshed.
		 */ 
		if (currentRequestNum == (prvRequestNum + 1)) {
			return true;
		} else {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Refreshing session cache. Reason: previous request number(" + prvRequestNum + ")" + 
							" and current request number(" + currentRequestNum + ")" + " are not in successive order");
			}
			return false;
		}
	}

}
