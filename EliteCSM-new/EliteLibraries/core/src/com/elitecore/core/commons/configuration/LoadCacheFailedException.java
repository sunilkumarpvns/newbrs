package com.elitecore.core.commons.configuration;

public class LoadCacheFailedException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public LoadCacheFailedException(){
		super("Error while loading the cache.");
	}
	
	public LoadCacheFailedException(String message) {
		super(message);
	}
	
	public LoadCacheFailedException(String message, Throwable e) {
		super(message,e);
	}
	public LoadCacheFailedException(Throwable e) {
		super(e);
	}
}
