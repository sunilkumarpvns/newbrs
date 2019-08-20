package com.elitecore.client.util.constants;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

/**
 * 
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
public enum EapEvents implements IEnum{
	RequestIdentityReceived,
	FragmentReceived,
	TLSMethodResponse,
	OtherMethodResponse,
	IgnoreReceivedPacket,
	MethodNotSupported
}
