package com.elitecore.coreeap.util.constants.fsm.events;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum ApplicationEvents implements IEnum{	
	AppEAPRequestReceived,
	AppMSCHAPv2RequestReceived,
	AppCHAPRequestReceived,
	AppPAPRequestReceived,
	AppMSCHAPRequestReceived
}
