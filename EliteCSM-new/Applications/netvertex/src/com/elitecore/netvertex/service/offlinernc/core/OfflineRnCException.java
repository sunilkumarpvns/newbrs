package com.elitecore.netvertex.service.offlinernc.core;

public class OfflineRnCException extends Exception {

	private static final long serialVersionUID = 1L;
	private final OfflineRnCErrorCodes code;

	public OfflineRnCException(OfflineRnCErrorCodes code , OfflineRnCErrorMessages rncErrorMessage) {
		super(rncErrorMessage.message());
		this.code = code;
	}
	
	public OfflineRnCException(OfflineRnCErrorCodes code , String reason) {
		super(reason);
		this.code = code;
	}
	
	public OfflineRnCException(OfflineRnCErrorCodes code , OfflineRnCErrorMessages rncErrorMessage, String reason) {
		super(rncErrorMessage.message() + "-" + reason);
		this.code = code;
	}

	public OfflineRnCException(OfflineRnCErrorCodes code, OfflineRnCErrorMessages rnCErrorMessage, Exception cause) {
		super(rnCErrorMessage.message(), cause);
		this.code = code;
	}
	
	public OfflineRnCErrorCodes getCode() {
		return code;
	}
	
}
