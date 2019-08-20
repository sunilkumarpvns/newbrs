package com.elitecore.coreeap.util.constants.fsm.events;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum GtcEvents implements IEnum {
	GtcResponseIdentityReceived,
	GtcRequestReceived,
	GtcNAKReceived,
	GtcInvalidRequest,	
	GtcResponseGenerated
}
