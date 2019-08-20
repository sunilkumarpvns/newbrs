package com.elitecore.classicrating.datamanager.rating;

import com.elitecore.classicrating.blmanager.base.RatingBLException;

public class RateNotFoundException extends RatingBLException {

	public RateNotFoundException(String message) {
		super(message);
	}

	public RateNotFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RateNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public RateNotFoundException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
