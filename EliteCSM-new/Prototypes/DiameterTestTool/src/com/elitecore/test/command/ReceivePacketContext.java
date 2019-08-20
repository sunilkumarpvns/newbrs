package com.elitecore.test.command;

import com.elitecore.commons.base.Optional;
import com.elitecore.test.ExecutionContext;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;

public interface ReceivePacketContext extends ExecutionContext {
	
	public static final String RCVD_REQ = "receivedrequest";
	
	public Optional<DiameterPacket> getReceivedPacketDetail();

}
