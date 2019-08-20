package com.elitecore.test.command;

import com.elitecore.commons.base.Optional;
import com.elitecore.test.ExecutionContext;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;


public interface SendCommandContext extends ExecutionContext {
	
	public static final String SEND_DATA = "senddata";

	Optional<DiameterPacket> getSendPacketDetail();
}
