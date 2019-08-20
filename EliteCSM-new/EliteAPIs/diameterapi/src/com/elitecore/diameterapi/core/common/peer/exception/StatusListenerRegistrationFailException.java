package com.elitecore.diameterapi.core.common.peer.exception;

public class StatusListenerRegistrationFailException extends Exception {
	
	
	private ListenerRegFailResultCode resultCode;
	
	private static final long serialVersionUID = 1L;

    public StatusListenerRegistrationFailException(ListenerRegFailResultCode resultCode) {
    	super();
    	setResultCode(resultCode);
    }

    public StatusListenerRegistrationFailException(String message, ListenerRegFailResultCode resultCode) {
        super(message);
        setResultCode(resultCode);
    }


    public StatusListenerRegistrationFailException(String message, Throwable cause,ListenerRegFailResultCode resultCode) {
        super(message, cause);
        setResultCode(resultCode);
    }

    public StatusListenerRegistrationFailException(Throwable cause, ListenerRegFailResultCode resultCode) {
        super(cause);
        setResultCode(resultCode);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ", Result-Code: " + resultCode;
    }
    
    
    private void setResultCode(ListenerRegFailResultCode resultCode){
       this.resultCode = resultCode;
    }
 
    
    public ListenerRegFailResultCode getResultCode(){
    	return resultCode;
    }
    
    public enum ListenerRegFailResultCode{
    	STACK_NOT_INITIALIZED ("STACK NOT INITIALIZED",ResultCodeCategory.TEMPORARY_CATEGORY),
    	STARTUP_IN_PROGRESS ("STARTUP IN PROGRESS",ResultCodeCategory.TEMPORARY_CATEGORY),
    	STOP_CALLED("STOP CALLED",ResultCodeCategory.PERMENENT_CATEGORY),
    	PEER_NOT_FOUND("PEER NOT FOUND",ResultCodeCategory.PERMENENT_CATEGORY),
    	OTHER("OTHER",ResultCodeCategory.PERMENENT_CATEGORY),
    	UNKNOWN("UNKNOWN",ResultCodeCategory.PERMENENT_CATEGORY);
    	
    	
    	public final String value;
    	public final ResultCodeCategory category;
    	
    	private ListenerRegFailResultCode(String value,ResultCodeCategory resultCodeCategory){
    		this.value = value;
    		this.category = resultCodeCategory;
    	}
    }
    
    public enum ResultCodeCategory{
    	PERMENENT_CATEGORY,
    	TEMPORARY_CATEGORY;
    }


}
