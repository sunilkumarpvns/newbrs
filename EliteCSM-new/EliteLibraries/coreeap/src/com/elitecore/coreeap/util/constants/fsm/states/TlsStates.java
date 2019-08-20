package com.elitecore.coreeap.util.constants.fsm.states;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum TlsStates implements IEnum{
	INITIALIZE,
	ACK,	
	ALERT,
	APPLICATION,
	CCS,
	DISCARD,	
	FAILURE,
	FRAGMENT,	
	HANDSHAKE,
	RECEIVED,
	START,
	SUCCESS,
}
