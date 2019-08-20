package com.elitecore.netvertexsm.datamanager.servermgr.dictionary.exception;

public class UnknownAttributeDataTypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnknownAttributeDataTypeException() {
		super();
	}

	public UnknownAttributeDataTypeException(String message) {
		super(message);
	}
	
	public UnknownAttributeDataTypeException(String message,Throwable cause){
		super(message,cause);
	}

   public UnknownAttributeDataTypeException(Throwable cause){
	   super(cause);
   }
}