package com.elitecore.core.serverx.internal.tasks;


public interface AsyncTaskContext {
	
	public void setAttribute(String key, Object attribute);
	
	public Object getAttribute(String key);
	
}
