package com.elitecore.coreeap.util.constants.fsm.events;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum SimEvents implements IEnum {
	SimResponseIdentityReceived,
	//SimStartReceived,
	SimStartReceivedWithPseudonymId,
	SimStartReceivedWithPermanentId,
	SimStartReceivedWithFastReauthId,
	SimStartReceivedWithUnknownId,
	SimReauthentication,
	SimChallenge,
	SimRequestReceived,
	SimNAKReceived,	
	SimInvalidRequest,
	SimSuccess,
	SimFailure,	
	SimUnconditionalEvent
}
