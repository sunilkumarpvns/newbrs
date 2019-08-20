/**
 * 
 */
package com.elitecore.diameterapi.core.common.router.exception;

import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

/**
 * @author pulin
 *
 */
public class InvalidRoutingPacketException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private ResultCode resultCode = ResultCode.DIAMETER_UNABLE_TO_COMPLY;
	public InvalidRoutingPacketException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidRoutingPacketException(ResultCode resultCode, String message) {
		super(message);
		this.resultCode = resultCode;
	}
	
	/**
	 * @param message
	 */
	public InvalidRoutingPacketException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InvalidRoutingPacketException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidRoutingPacketException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ResultCode getResultCode(){
		return resultCode;
	}
}
