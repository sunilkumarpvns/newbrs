package com.elitecore.ssp.util.exception;

public class DataManagerException extends Exception {
    
    private static final long serialVersionUID = 1L;
    private String sourceField;

    public DataManagerException() {
        super("Error in Data Manager.");
    }

    public DataManagerException(String message) {
        super(message);    
    }

    public DataManagerException(String message,String sourceField) {
        super(message);
    	this.sourceField = sourceField;        
    }

    public DataManagerException(String message, Throwable cause) {
        super(message,cause);    
    }
    
    public DataManagerException(String message,String sourceField,Throwable cause)
    {
        super(message,cause);
        this.sourceField = sourceField;
     }
   
    public DataManagerException(Throwable cause) {
        super(cause);    
    }
    
    public String getSourceField(){
    	return this.sourceField;
    }
}
