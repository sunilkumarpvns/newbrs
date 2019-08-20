package com.elitecore.diameterapi.diameter.stack.application.sessionrelease;

import javax.annotation.Nonnull;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;

/** check whether it requires to remove session based on following criteria 
 *			
 *
 * 			if( Command-Code = 'Credit-Control(CCA)')
 * 					if(request-type == Termination)
 * 						Remove session
 * 
 * 			if( Command-Code = 'Accounting')
 * 					if(acct-record-type == Stop-Record)
 * 						Remove session
 * 
 * 			if( Command-Code = 'Session Termination'(STA))
 * 						Remove session
 * 
 * 			if (Result code category is 3XXX or 5XXX)
 * 							Remove session
 * 
 */

public class AppDefaultSessionReleaseIndicator implements SessionReleaseIndiactor{
	
	public static final String MODULE = "APP-DEFAULT-SESS-RELEASE-INDICATOR";

	@Override
	public boolean isEligible(DiameterPacket diameterPacket) {
		
		if(diameterPacket.isRequest()){
			return false;
		}
	
		DiameterAnswer diameterAnswer = (DiameterAnswer) diameterPacket;
	
		if (diameterAnswer.isServerInitiated()) {
			return false;
		}
		
		boolean result = false;
		//Check for Command code is Eligible for session remove
		switch(CommandCode.getCommandCode(diameterPacket.getCommandCode())){
			case CREDIT_CONTROL :
				result = checkCCResponseForSessionRemoval(diameterAnswer);
				break;
				
			case ACCOUNTING :
				result = checkAccountingResponseForSessionRemoval(diameterAnswer);
				break;
				
			case SESSION_TERMINATION :
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eligible to remove session. Reason: Command-Code is ST("+ CommandCode.SESSION_TERMINATION.code +")");
				return true;
			default:
				break;
		}
		
		return result ? true : checkResultCodeForSessionRemoval(diameterAnswer);  
		
	}

	
	protected boolean checkCCResponseForSessionRemoval(DiameterAnswer diameterPacket){
		IDiameterAVP requestType = diameterPacket.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
		if(requestType != null){
			if(requestType.getInteger() == DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST){
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eligible to remove session. Reason: CC-Request-Type is TERMINATION (" + DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST + ")");
				
				return true;
			}
		}else{
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Eligible to remove session. Reason: Request-Type AVP not found in CC response for Session ID: "+ diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
			
			return true;
		}
		return false;
	}
	
	protected boolean checkAccountingResponseForSessionRemoval(DiameterAnswer diameterPacket){
		IDiameterAVP recordType = diameterPacket.getAVP(DiameterAVPConstants.ACCOUNTING_RECORD_TYPE);
		if(recordType != null){
			if(recordType.getInteger() == DiameterAttributeValueConstants.STOP_RECORD){
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Eligible to remove session. Reason: Accounting-Record-Type is STOP-RECORD(" + DiameterAttributeValueConstants.STOP_RECORD + ")");
				return true;
			}
		}else{
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Eligible to remove session. Reason: Accounting-Record-Type AVP not found in Accounting response for Session ID: "+ diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
			return true;
		}
		
		return false;
	}
	
	/**
	 * check on Result code to check that {@link Session} is removable or not
	 * 
	 * This method can be override to provide application specifice result code check
	 */
	protected boolean checkResultCodeForSessionRemoval(DiameterAnswer diameterAnswer){
		
		IDiameterAVP resultCode = diameterAnswer.getAVP(DiameterAVPConstants.RESULT_CODE);
		if(resultCode == null){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Eligible to remove session. Reason: Result-Code AVP not found for Session ID: "+ diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
			return true;
		}
		
		ResultCodeCategory resultCodeCategory =  ResultCodeCategory.getResultCodeCategory(resultCode.getInteger());
		if(ResultCodeCategory.RC2XXX == resultCodeCategory || ResultCodeCategory.RC1XXX == resultCodeCategory){
			return false;
		}

		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Eligible to remove session. Reason: Result-Code category is not 2XXX or 1XXX");
		return true;

	}

}
