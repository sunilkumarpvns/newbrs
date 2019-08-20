package com.elitecore.diameterapi.diameter.common.fsm.peer.enums;

import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;

public enum DiameterPeerEvent implements IEventEnum{
	Start(true),
	RConnCER(true),
	IRcvConnAck(true),
	IRcvConnNack(true),
	Timeout(true),
	IRcvCEA(true),
	IPeerDisc(true),
	IRcvNonCEA(true),
	RPeerDisc(true),
	WinElection(true),
	SendMessage(false),
	RrcvMessage(false),
	IrcvMessage(false),
	RRcvDWR(false),
	RRcvDWA(false),
	Stop(true),
	RRcvDPR(true),
	RRcvCER(false),
	RRcvCEA(false),
	IRcvDWR(false),
	IRcvDWA(false),
	IRcvDPR(true),
	IRcvCER(false),
	IRcvDPA(true),
	RRcvDPA(true);

	
	public final boolean isSync;

	DiameterPeerEvent(boolean isSync) {
		this.isSync = isSync;
	}
	
	public boolean isSync() {
		return isSync;
	}
	
	public int eventOrdinal() {
		return super.ordinal();
	}
	
	public static final DiameterPeerEvent[] VALUES = values();
}
