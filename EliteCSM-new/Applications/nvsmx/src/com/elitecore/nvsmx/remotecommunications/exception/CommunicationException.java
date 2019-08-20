package com.elitecore.nvsmx.remotecommunications.exception;

public class CommunicationException extends Exception{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 
     */
    public CommunicationException() {
        super();
    }
    
    /**
     * @param message
     */
    public CommunicationException( String message ) {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public CommunicationException( String message ,
                                   Throwable cause ) {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public CommunicationException( Throwable cause ) {
        super(cause);
    }
    
}
