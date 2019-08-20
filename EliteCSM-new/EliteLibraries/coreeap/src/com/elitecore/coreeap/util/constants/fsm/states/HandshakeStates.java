package com.elitecore.coreeap.util.constants.fsm.states;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum HandshakeStates implements IEnum{
	INITIALIZED,
	RECEIVED,
	DISCARD,
	CERTIFICATE,
	CERTIFICATE_REQUEST,
	SERVERHELLO_DONE,
	CERTIFICATE_VERIFY,
	CLIENT_KEY_EXCHANGE,
	FINISHED,
	SUCCESS,
	FAILURE,
	ALERT
}
