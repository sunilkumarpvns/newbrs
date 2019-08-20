/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 3, 2008
 *	@author Raghu G
 *  Last Modified October 3, 2008
 */

/*
 *  AppContextNotDefinedException.java
 * Used to throw an exception whenever the IRatingAppContext is not defined
 */

package com.elitecore.classicrating.core;


public class AppContextNotDefinedException extends Exception {


	private static final long serialVersionUID = 1L;
	/**
	 *  Constructor 
	 */
	public AppContextNotDefinedException() {
	}
	/**
	 *  Constructor 
	 *  @param  message :  A String that contains error message
	 */
	public AppContextNotDefinedException(String message) {
		super(message);
	}
	/**
	 *  Constructor 
	 * @param  throwable: Object of type Throwable
	 */
	public AppContextNotDefinedException(Throwable throwable) {
		super(throwable);
	}

	/**
	 *  Constructor 
	 *  @param  message :  A String that contains error message
	 * @param  throwable: Object of type Throwable
	 */
	public AppContextNotDefinedException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
