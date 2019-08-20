package com.elitecore.netvertex.core.test.exception;

@SuppressWarnings("serial")
public class AttributeNotFoundException extends Exception {

	private String attributeId;
	public AttributeNotFoundException(String id,String message, Throwable cause) {
		super(message, cause);
	}

	public AttributeNotFoundException(String id,String message) {
		super(message);
	}

	public AttributeNotFoundException(String id,Throwable cause) {
		super(cause);
	}
	
	public String getAttributeId(){
		return attributeId;
	}

}
