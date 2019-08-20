package com.elitecore.test.exception;

import java.io.IOException;

public class ChennelClosedException extends IOException{

	public ChennelClosedException(String name) {
		super(name + " is close");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
