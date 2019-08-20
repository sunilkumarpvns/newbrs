package com.elitecore.aaa.radius.systemx.esix.udp;

import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.udp.UDPCommunicatorGroup;

public interface RadUDPCommGroup extends UDPCommunicatorGroup {
	void handleRequest(byte[] requestBytes, String secret, RadResponseListener responseListner, ISession session);
}
