package com.elitecore.netvertexsm.datamanager;

public class DataManagerException extends Exception {
    
    private static final long serialVersionUID = 1L;
    private String sourceField;
    private DataManagerErrorCode errorCode = DataManagerErrorCode.UNKNOWN_ERROR;

    public DataManagerException() {
        super("Error in Data Manager.");
    }
    public DataManagerException(DataManagerErrorCode errorCode ,Throwable cause) {
    	super(cause);
    	this.errorCode = errorCode;
    }
    public DataManagerException(DataManagerErrorCode errorCode ,String message) {
    	super(message);
    	this.errorCode = errorCode;
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
    public DataManagerErrorCode getErrorCode() {
    	return errorCode;
    }
}
