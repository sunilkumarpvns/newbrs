package com.elitecore.elitesm.sqlexception;

public class EliteSQLGrammerException extends Exception {
    
    private static final long serialVersionUID = 1L;
    private String sourceField;

    public EliteSQLGrammerException() {
        super("Error in Data Manager.");
    }

    public EliteSQLGrammerException(String message) {
        super(message);    
    }

    public EliteSQLGrammerException(String message,String sourceField) {
        super(message);
    	this.sourceField = sourceField;        
    }

    public EliteSQLGrammerException(String message, Throwable cause) {
        super(message,cause);    
    }
    
    public EliteSQLGrammerException(String message,String sourceField,Throwable cause)
    {
        super(message,cause);
        this.sourceField = sourceField;
     }
   
    public EliteSQLGrammerException(Throwable cause) {
        super(cause);    
    }
    
    public String getSourceField(){
    	return this.sourceField;
    }
}
