package com.elitecore.core.util.cli.cmd.logmonitorext;


public interface Expression<T,V> {
	
	public boolean evaluate(T request, V response);
}
