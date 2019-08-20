package com.elitecore.coreeap.util.constants.fsm.events;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum EapEvents implements IEnum{
	EapRespIdentityReceived,
	EapPacketIsNotResponse,
	EapNakReceived,
	EapMethodNotDefined,
	EapMethodRespReceived,
	EapFailureEvent,
	EapSuccessEvent,
	EapPacketIgnored,
	EapException
}
