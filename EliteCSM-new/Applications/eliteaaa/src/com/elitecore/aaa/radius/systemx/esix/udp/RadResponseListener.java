package com.elitecore.aaa.radius.systemx.esix.udp;

import com.elitecore.core.serverx.sessionx.inmemory.ISession;

public interface RadResponseListener {
	void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse, ISession session);
	void requestDropped(RadUDPRequest radUDPRequest);
	void requestTimeout(RadUDPRequest radUDPRequest);
}
