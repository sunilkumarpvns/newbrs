package com.elitecore.netvertex.core.util.exception;

public class TagParsingException extends Exception {

	private static final long serialVersionUID = 5537968711226647018L;

	public TagParsingException(String message) {
		super(message);
	}
	
	public TagParsingException(Throwable throwable) {
		super(throwable);
	}
	
	public TagParsingException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
