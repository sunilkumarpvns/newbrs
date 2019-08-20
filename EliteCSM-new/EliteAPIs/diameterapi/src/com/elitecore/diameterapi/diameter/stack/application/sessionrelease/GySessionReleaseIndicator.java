package com.elitecore.diameterapi.diameter.stack.application.sessionrelease;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;

public class GySessionReleaseIndicator extends AppDefaultSessionReleaseIndicator {
	
	@Override
	protected boolean checkCCResponseForSessionRemoval(DiameterAnswer diameterAnswer) {
		if (superCheckCCResponseForSessionRemoval(diameterAnswer)) {
			return true;
		}

		/**
		 * Event Request does not require session, So Event Request will be
		 * eligible for session release
		 */
		IDiameterAVP requestType = diameterAnswer.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
		if (requestType.getInteger() == DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST) {

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Eligible to remove session. Reason: CC-Request-Type is EVENT_REQUEST ("
						+ DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST + ")");
			}

			return true;
		}

		return false;
	}

	@VisibleForTesting
	boolean superCheckCCResponseForSessionRemoval(DiameterAnswer diameterAnswer) {
		return super.checkCCResponseForSessionRemoval(diameterAnswer);
	}
}