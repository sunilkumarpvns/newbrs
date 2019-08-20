package com.elitecore.core.imdg;

public class ImdgInstanceFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public ImdgInstanceFailedException() {
		super("Failed Initialization.");
    }
   
    public ImdgInstanceFailedException(String message) {
        super(message);    
    }
   
    public ImdgInstanceFailedException(String message, Throwable cause) {
        super(message, cause);    
    }
   
    public ImdgInstanceFailedException(Throwable cause) {
        super(cause);    
    }
}
