package com.elitecore.netvertexsm.datamanager.policymgr.policygroup.exception;

public class ValueAlreadyExistsInDBException extends Exception {
	public ValueAlreadyExistsInDBException() {
		super();
	}

	public ValueAlreadyExistsInDBException(String message) {
		super(message);
	}

	public ValueAlreadyExistsInDBException(String message,Throwable cause){
		super(message,cause);
	}

   public ValueAlreadyExistsInDBException(Throwable cause){
	   super(cause);
   }
}
