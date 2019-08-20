package com.elitecore.netvertex.core.util.exception;

/**
 * 
 * @author Manjil Purohit
 *
 */
public class TagValueNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private String tag;

	public TagValueNotFoundException(String message, String tag) {
		super(message);
		this.tag = tag;
	}
	
	public TagValueNotFoundException(String tag, Throwable throwable) {
		super(throwable);
		this.tag = tag;
	}
	
	public TagValueNotFoundException(String message, String tag, Throwable throwable) {
		super(message, throwable);
		this.tag = tag;
	}
	
	@Override
	public String getMessage() {
		return "Tag value not found for " + tag;
	}
	
	public String getTag() {
		return tag;
	}
	
}
