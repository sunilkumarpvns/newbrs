package com.elitecore.test.util;


public interface AsyncTaskContext {
	
	public void setAttribute(String key, Object attribute);
	
	public Object getAttribute(String key);
	
}
