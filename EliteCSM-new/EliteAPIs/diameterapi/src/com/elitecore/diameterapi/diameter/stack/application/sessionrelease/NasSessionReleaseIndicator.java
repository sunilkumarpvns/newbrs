package com.elitecore.diameterapi.diameter.stack.application.sessionrelease;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;

public class NasSessionReleaseIndicator extends AppDefaultSessionReleaseIndicator{
	
	@Override
	protected boolean checkAccountingResponseForSessionRemoval(
			DiameterAnswer diameterPacket) {
		return false;
	}
}
