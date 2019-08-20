package com.elitecore.core.commons.plugins;

public class PluginInitializationFailedException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public PluginInitializationFailedException(){
		super("Error during plugin initialization.");
	}
	
	public PluginInitializationFailedException(String message){
		super(message);
	}
	
	public PluginInitializationFailedException(String message, Throwable cause){
		super(message, cause);
	}

	public PluginInitializationFailedException(Throwable cause){
		super(cause);
	}
	
}
