package com.elitecore.netvertex.core;


public interface Validator<T> {
	
	public void validate(T t) throws Exception;

}
