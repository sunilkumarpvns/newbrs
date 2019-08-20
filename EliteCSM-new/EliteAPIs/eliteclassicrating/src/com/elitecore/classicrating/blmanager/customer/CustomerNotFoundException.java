/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author Raghu G
 *  Last Modified October 1, 2008
 */

/*
 * NoPackageDefinitionFoundException.java 
 * This class is used to throw Exception when no package is found for given search criteria
 * 
 */

package com.elitecore.classicrating.blmanager.customer;

import com.elitecore.classicrating.blmanager.base.RatingBLException;

public class CustomerNotFoundException extends RatingBLException {

	private static final long serialVersionUID = 1L;

	public CustomerNotFoundException() {
	}

	public CustomerNotFoundException(String message) {
		super(message);
	}

	public CustomerNotFoundException(Throwable throwable) {
		super(throwable);
	}

	public CustomerNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
