/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.fsm.peer;

import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

/**
 * @author pulindani
 *
 */
public class CapabilitiesExchangeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private ResultCode resultCode; 
	
	/**
	 * 
	 */
	public CapabilitiesExchangeException() {
	}

	/**
	 * @param message
	 */
	public CapabilitiesExchangeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CapabilitiesExchangeException(Throwable cause) {
		super(cause);
	}
	
	public CapabilitiesExchangeException(ResultCode resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CapabilitiesExchangeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ResultCode getResultCode() {
		return resultCode;
	}	


}
