package com.elitecore.test;

public class EventNotSupportedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String event;
	
	


	public EventNotSupportedException(String event) {
		super();
		this.event = event;
	}

	    
    public EventNotSupportedException(String event,String message) {
    	super(message);
    	this.event = event;
    }

    
    public EventNotSupportedException(String event,String message, Throwable cause) {
        super(message, cause);
        this.event = event;
    }

    
    public EventNotSupportedException(String event,Throwable cause) {
        super(cause);
        this.event = event;
    }
    
    public String getEvent() {
		return event;
	}
    
    

}
