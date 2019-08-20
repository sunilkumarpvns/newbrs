package com.elitecore.coreradius.commons.attributes;

/**
 * Is thrown attribute type received in the request packet could not be 
 * located through radius dictionary. 
 * @author baiju
 *
 */
public class AttributeTypeNotFoundException extends RuntimeException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AttributeTypeNotFoundException() {
		super("Could not locate required attribute type from the Dictionary.");
	}

	public AttributeTypeNotFoundException(String message) {
		super(message);
	}
	
}
