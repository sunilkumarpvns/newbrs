package com.elitecore.coreeap.util.constants.fsm.events;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum MD5Events implements IEnum{	
	MD5ResponseIdentityReceived,
	MD5RequestReceived,
	MD5NAKReceived,
	MD5InvalidRequest,	
	MD5ResponseGenerated	
}
