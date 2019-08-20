package com.elitecore.diameterapi.diameter.stack.application;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

public interface SessionReleaseIndiactor {

	public boolean isEligible(DiameterPacket diameterPacket);
	
}
