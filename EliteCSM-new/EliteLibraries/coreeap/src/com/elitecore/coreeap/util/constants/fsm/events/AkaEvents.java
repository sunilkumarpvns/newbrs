package com.elitecore.coreeap.util.constants.fsm.events;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum AkaEvents implements IEnum {
	AkaResponseIdentityReceived,
	AkaRequestReceived,
	AkaSynchronizationFailureReceived,
	AkaNAKReceived,	
	AkaInvalidRequest,
	AkaSuccess,
	AkaFailure,	
	AkaUnconditionalEvent
}
