package com.elitecore.diameterapi.core.common.peer;

import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

/**
 * 
 * @author narendra.pathai
 * @author sanjay.dhamelia
 */
public interface ResponseListener {
	
	ResponseListener NO_RESPONSE_LISTENER = new ResponseListener() {
		private static final String MODULE = "NO-RES-LSTNR";
		
		@Override
		public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Response recieved from host: " + hostIdentity);
			}
		}
		
		@Override
		public void requestTimedout(String hostIdentity, DiameterSession session) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Request timedout from host: " + hostIdentity);
			}
		}
	};

	public void requestTimedout(@Nonnull String hostIdentity, @Nonnull DiameterSession session);
	
	public void responseReceived(@Nonnull DiameterAnswer diameterAnswer, @Nonnull String hostIdentity, @Nonnull DiameterSession session);
	
	
	
	class RetryableResultCode {
		private ResultCode resultCode;
		
		public RetryableResultCode(DiameterAnswer answer) {
			IDiameterAVP resultCodeAVP = answer.getAVP(DiameterAVPConstants.RESULT_CODE);
			
			if (resultCodeAVP != null) {
				resultCode = ResultCode.fromCode((int) resultCodeAVP.getInteger());
			}
		}
		
		public boolean isRetryable() {
			return RETRYABLE_RESULT_CODES.contains(resultCode);
		}
		
		public @Nullable ResultCode getResultCode() {
			return resultCode;
		}
	}
	
	EnumSet<ResultCode> RETRYABLE_RESULT_CODES = EnumSet.of(ResultCode.DIAMETER_TOO_BUSY);
	
}
