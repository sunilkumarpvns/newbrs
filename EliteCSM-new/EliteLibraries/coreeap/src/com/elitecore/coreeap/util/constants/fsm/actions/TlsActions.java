package com.elitecore.coreeap.util.constants.fsm.actions;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum TlsActions implements IEnum{
	Initialize,
	HandleReceivedAck,	
	HandleAlert,
	HandleApplicationRecord,
	HandleHS_CCS_HS,
	DiscardRequest,	
	BuildFailure,
	HandleFragmentPacket,	
	HandleHandshakeMessage,
	ProcessRequest,
	GenerateStart,
	BuildSuccess	
}
