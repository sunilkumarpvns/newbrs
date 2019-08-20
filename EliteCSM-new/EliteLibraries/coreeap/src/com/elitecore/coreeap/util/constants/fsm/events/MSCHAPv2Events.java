package com.elitecore.coreeap.util.constants.fsm.events;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum MSCHAPv2Events implements IEnum {
	MSCHAPv2ResponseIdentityReceived,
	MSCHAPv2RequestReceived,
	MSCHAPv2NAKReceived,
	MSCHAPv2InvalidRequest,
	MSCHAPv2ValidRequest,
	MSCHAPv2Success,
	MSCHAPv2Failure,
	MSCHAPv2ResponseGenerated,
	MSCHAPv2UnconditionalEvent
}
