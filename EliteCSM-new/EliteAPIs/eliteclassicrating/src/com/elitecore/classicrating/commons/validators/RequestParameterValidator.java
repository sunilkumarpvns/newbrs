/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 15, 2008
 *	
 *
 */
package com.elitecore.classicrating.commons.validators;

import com.elitecore.classicrating.base.IBaseConstant;
import com.elitecore.classicrating.blmanager.base.MandatoryParameterMissingException;
import com.elitecore.classicrating.commons.request.RequestParameters;
import com.elitecore.commons.logging.LogManager;

public class RequestParameterValidator {

    //private static Log4jLogger Logger = Log4jLogger.getInstance();
    private static final String MODULE = "Validate Parameters";

    public static void validateParameters(RequestParameters request) throws MandatoryParameterMissingException {
        LogManager.getLogger().trace(MODULE, "Into validateAuthorizationParameters");
        if (request != null && request.size() > 0) {
            validateBasicParameters(request);
            if (request.get(IBaseConstant.SERVICE_TYPE).equalsIgnoreCase(IBaseConstant.VOIP)) {
                validateVOIPParameters(request);
            } else if (request.get(IBaseConstant.SERVICE_TYPE).equalsIgnoreCase(IBaseConstant.DATA)) {
                validateDataParameters(request);
            } else {
                throw new MandatoryParameterMissingException("Service Type " + request.get(IBaseConstant.SERVICE_TYPE) + " not supported.");
            }
        } else {
            throw new MandatoryParameterMissingException("Request Parameters is null");
        }
        LogManager.getLogger().trace(MODULE, "Out of validateAuthorizationParameters with successful validation");
    }

    public static void validateBasicParameters(RequestParameters request) throws MandatoryParameterMissingException {
        LogManager.getLogger().trace(MODULE, "Into validateBasicParameters");
        
        
        if (request.get(IBaseConstant.SERVICE_TYPE) == null || request.get(IBaseConstant.SERVICE_TYPE).length() == 0) {
            throw new MandatoryParameterMissingException(IBaseConstant.SERVICE_TYPE + IBaseConstant.MANDATORY_MSG);
        }
        if (request.get(IBaseConstant.USERID) == null || request.get(IBaseConstant.USERID).length() == 0) {
            throw new MandatoryParameterMissingException(IBaseConstant.USERID + IBaseConstant.MANDATORY_MSG);
        }
        
        if (request.get(IBaseConstant.REQUEST_TYPE).equals(IBaseConstant.ACCOUNTING)) {
            
        	if (request.get(IBaseConstant.ACCT_SESSION_ID) == null || request.get(IBaseConstant.ACCT_SESSION_ID).length() == 0) {
        		throw new MandatoryParameterMissingException(IBaseConstant.ACCT_SESSION_ID + IBaseConstant.MANDATORY_MSG);
        	}
        }
        
        LogManager.getLogger().trace(MODULE, "Out of validateBasicParameters with successful validation");
    }

    public static void validateVOIPParameters(RequestParameters request) throws MandatoryParameterMissingException {
        LogManager.getLogger().trace(MODULE, "Into validateVOIPParameters");


        if ((request.get(IBaseConstant.CALLED_STATION_ID) == null || request.get(IBaseConstant.CALLED_STATION_ID).length() == 0)) {
            throw new MandatoryParameterMissingException(IBaseConstant.CALLED_STATION_ID + IBaseConstant.MANDATORY_MSG);
        }


        if (request.get(IBaseConstant.REQUEST_TYPE).equals(IBaseConstant.ACCOUNTING)) {
        	
        	
            if ((request.get(IBaseConstant.ACCT_SESSION_TIME) == null || request.get(IBaseConstant.ACCT_SESSION_TIME).length() == 0)) {
                throw new MandatoryParameterMissingException(IBaseConstant.ACCT_SESSION_TIME + IBaseConstant.MANDATORY_MSG);
            }

            if ((request.get(IBaseConstant.CALL_START) == null || request.get(IBaseConstant.CALL_START).length() == 0)) {
                throw new MandatoryParameterMissingException(IBaseConstant.CALL_START + IBaseConstant.MANDATORY_MSG);
            }

            if ((request.get(IBaseConstant.CALL_END) == null || request.get(IBaseConstant.CALL_END).length() == 0)) {
                throw new MandatoryParameterMissingException(IBaseConstant.CALL_END + IBaseConstant.MANDATORY_MSG);
            }
        }
        LogManager.getLogger().trace(MODULE, "Out of validateVOIPParameters with successful validation");
    }

    public static void validateDataParameters(RequestParameters request) throws MandatoryParameterMissingException {
        LogManager.getLogger().trace(MODULE, "Into validateDataParameters ");

        if (request.get(IBaseConstant.REQUEST_TYPE).equals(IBaseConstant.ACCOUNTING)) {
        	
        	
            if ((request.get(IBaseConstant.ACCT_INPUT_OCTETS) == null || request.get(IBaseConstant.ACCT_INPUT_OCTETS).length() == 0) &&
                    (request.get(IBaseConstant.ACCT_OUTPUT_OCTETS) == null || request.get(IBaseConstant.ACCT_OUTPUT_OCTETS).length() == 0)) {
                throw new MandatoryParameterMissingException("Either " + IBaseConstant.ACCT_INPUT_OCTETS + " or " + IBaseConstant.ACCT_OUTPUT_OCTETS + IBaseConstant.MANDATORY_MSG);
            }
            
            if ((request.get(IBaseConstant.ACCT_SESSION_TIME) == null || request.get(IBaseConstant.ACCT_SESSION_TIME).length() == 0)) {
                throw new MandatoryParameterMissingException(IBaseConstant.ACCT_SESSION_TIME + IBaseConstant.MANDATORY_MSG);
            }

            if ((request.get(IBaseConstant.CALL_START) == null || request.get(IBaseConstant.CALL_START).length() == 0)) {
                throw new MandatoryParameterMissingException(IBaseConstant.CALL_START + IBaseConstant.MANDATORY_MSG);
            }

            if ((request.get(IBaseConstant.CALL_END) == null || request.get(IBaseConstant.CALL_END).length() == 0)) {
                throw new MandatoryParameterMissingException(IBaseConstant.CALL_END + IBaseConstant.MANDATORY_MSG);
            }
        }
        LogManager.getLogger().trace(MODULE, "Out of validateDataParameters with successful validation");
    }
}
