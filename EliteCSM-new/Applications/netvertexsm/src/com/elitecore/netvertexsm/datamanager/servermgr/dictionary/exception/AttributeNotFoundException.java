/**
 * 
 */
package com.elitecore.netvertexsm.datamanager.servermgr.dictionary.exception;


/**
 * @author pratik.chauhan
 *
 */
public class AttributeNotFoundException extends Exception {

	public AttributeNotFoundException() {
		super();
	}

	public AttributeNotFoundException(String message) {
		super(message);
	}

	public AttributeNotFoundException(String message,Throwable cause){
		super(message,cause);
	}

   public AttributeNotFoundException(Throwable cause){
	   super(cause);
   }
}
