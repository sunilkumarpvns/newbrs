package com.elitecore.config.exception;

/**
 * This exception, designed for common use by elitecore classes, 
 * is thrown when an invalid parameter is configured or passed 
 * to a method.
 *
 * @author Manjil Purohit
 * @version 1.0, 04/01/2013
 */

public class InvalidParameterException extends Exception {

    private static final long serialVersionUID = -857968536935667808L;

    /**
     * Constructs an InvalidParameterException with no detail message.
     * A detail message is a String that describes this particular
     * exception.
     */
    public InvalidParameterException() {
    	super();
    }

    /**
     * Constructs an InvalidParameterException with the specified
     * detail message.  A detail message is a String that describes
     * this particular exception.
     *
     * @param msg the detail message.  
     */
    public InvalidParameterException(String msg) {
    	super(msg);
    }
}