package com.elitecore.coreeap.util.constants.fsm.events;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum HandshakeEvents implements IEnum {
	HSRequestReceived,
	HSInvalidRequest,
	HSClientHelloReceived,
	HSGenerateServerHello,
	HSCertificateReceived,
	HSGenerateCertificate,
	HSGenerateServerKeyExchange,
	HSGenerateCertificateRequest,
	HSGenerateServerHelloDone,
	HSCertificateVerifyReceived,
	HSClientKeyExchangeReceived,
	HSFinishedReceived,
	HSGenerateFinished,
	HSGenerateAlert,
	HSSuccess, //This identify the successful completion of the handshake negotiation
	HSFailure,//This identify the Failure of the handshake negotiation
	HSDone, //This identify the reponse has been generated
	HSClientHelloWithSessionResumptionReceived,
	HSGenerateFinishMessageReceived ;

}
