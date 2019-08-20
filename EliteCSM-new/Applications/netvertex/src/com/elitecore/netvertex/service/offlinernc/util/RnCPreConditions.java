package com.elitecore.netvertex.service.offlinernc.util;

import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public class RnCPreConditions {

	private RnCPreConditions() {
		// suppress creation
	}
	
	public static String checkKeyNotNull(RnCRequest request, OfflineRnCKeyConstants key) throws OfflineRnCException {
		String value = request.getAttribute(key);
		if (value == null) {
			throw new OfflineRnCException(OfflineRnCErrorCodes.INVALID_EDR, OfflineRnCErrorMessages.INVALID_EDR, 
					OfflineRnCErrorMessages.MISSING_KEY + "-" + key.getName());
		}
		
		return value;
	}
	
	public static String checkKeyNotNull(RnCRequest request, String key) throws OfflineRnCException {
		String value = request.getAttribute(key);
		if (value == null) {
			throw new OfflineRnCException(OfflineRnCErrorCodes.INVALID_EDR, OfflineRnCErrorMessages.INVALID_EDR, 
					OfflineRnCErrorMessages.MISSING_KEY + "-" + key);
		}
		
		return value;
	}
	
	public static String checkKeyNotNull(RnCResponse response, OfflineRnCKeyConstants key) throws OfflineRnCException {
		String value = response.getAttribute(key);
		if (value == null) {
			throw new OfflineRnCException(OfflineRnCErrorCodes.INVALID_EDR, OfflineRnCErrorMessages.INVALID_EDR, 
					OfflineRnCErrorMessages.MISSING_KEY + "-" + key.getName());
		}
		
		return value;
	}
	
	public static String checkKeyNotNull(RnCResponse response, String key) throws OfflineRnCException {
		String value = response.getAttribute(key);
		if (value == null) {
			throw new OfflineRnCException(OfflineRnCErrorCodes.INVALID_EDR, OfflineRnCErrorMessages.INVALID_EDR, 
					OfflineRnCErrorMessages.MISSING_KEY + "-" + key);
		}
		
		return value;
	}
}
