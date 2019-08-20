package com.elitecore.core.commons.drivers;

/**
 * This exception must be thrown from any driver which tried to process the request and 
 * the processing times out, then in that case this exception should be thrown.
 * <br/>
 * The timeout field supplied to this exception must be in MilliSeconds (ms)
 * @author narendra.pathai
 *
 */
public class DriverProcessTimeoutException extends DriverProcessFailedException {
	
	private static final long serialVersionUID = 1L;
	
	//this field must be in milliseconds (ms)
	private long timeoutDurationInMilliSeconds;

	/**
	 * 
	 * @param message the message that is to be conveyed by the module
	 * @param timeoutDuration the timeout count after which the exception is being thrown 
	 * <b>in Milliseconds (ms)</b>
	 */
	public DriverProcessTimeoutException(String message, long timeoutDuration){
		super(message);
		this.timeoutDurationInMilliSeconds = timeoutDuration;
	}

	/**
	 * 
	 * @param message message the message that is to be conveyed by the module
	 * @param timeoutDuration the timeout count after which the exception is being thrown 
	 * <b>in Milliseconds (ms)</b>
	 * @param cause
	 */
	public DriverProcessTimeoutException(String message, long timeoutDuration, Throwable cause){
		super(message, cause);
		this.timeoutDurationInMilliSeconds = timeoutDuration;
	}
	
	/**
	 * 
	 * @param timeoutDuration the timeout count after which the exception is being thrown 
	 * <b>in Milliseconds (ms)</b>
	 * @param cause
	 */
	public DriverProcessTimeoutException(long timeoutDuration, Throwable cause){
		super(cause);
		this.timeoutDurationInMilliSeconds = timeoutDuration;
	}
	
	/**
	 * This method can be used to fetch the timeout duration in Milliseconds (ms) after which
	 * the driver processing timed out.
	 * 
	 * @return the value in milliseconds of the timeout duration specific to the driver that 
	 * throws the exception
	 */
	public long getTimeoutDuration(){
		return this.timeoutDurationInMilliSeconds;
	}
	
}
