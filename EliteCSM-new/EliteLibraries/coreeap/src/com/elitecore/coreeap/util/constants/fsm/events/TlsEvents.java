package com.elitecore.coreeap.util.constants.fsm.events;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum TlsEvents implements IEnum{
	TlsRequestReceived,
	TlsAckReceived,	
	TlsAlertRaised,
	TlsAlertReceived,	
	TlsApplicationRecordRecevied,
	TlsHS_CCS_HSReceived,
	TlsFailure,	
	TlsFragmentedPacketReceived,	
	TlsHandshakeRecordReceived,
	TlsInvalidRequest,
	TlsNakReceived,			
	TlsResponseIdentityReceived,
	TlsSuccess,
	TlsUnconditionalEvent,
}
