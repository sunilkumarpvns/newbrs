/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.explicitrouting.exception;

import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

/**
 * @author pulin
 *
 */
public class ExplicitRoutingFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private ResultCode resultCode = ResultCode.DIAMETER_ER_NOT_AVAILABLE;
	public ExplicitRoutingFailedException() {

	}

	public ExplicitRoutingFailedException(ResultCode resultCode, String message) {
		super(message);
		this.resultCode = resultCode;
	}
	
	/**
	 * @param message
	 */
	public ExplicitRoutingFailedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ExplicitRoutingFailedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ExplicitRoutingFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExplicitRoutingFailedException(ResultCode resultCode,
			Throwable e) {
		super(e);
		this.resultCode = resultCode;
	}

	public ResultCode getResultCode(){
		return resultCode;
	}
}
