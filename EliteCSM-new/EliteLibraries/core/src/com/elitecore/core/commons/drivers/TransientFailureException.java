package com.elitecore.core.commons.drivers;

/**
 * All the problems in initializing external resources should be considered as
 * Transient failure and all drivers must throw this exception in case of any
 * such error. These failures are treated as <i>temporary</i> by the system.
 * 
 * <p>Some examples of external dependencies are: Database Connectivity, TCP/UDP
 * connectivity and such.
 * 
 * @author narendra.pathai
 *
 */
public class TransientFailureException extends DriverInitializationFailedException {

	private static final long serialVersionUID = -4235436632572914860L;
	
	public TransientFailureException(String message, Throwable cause) {
		super(message, cause);
	}
}
