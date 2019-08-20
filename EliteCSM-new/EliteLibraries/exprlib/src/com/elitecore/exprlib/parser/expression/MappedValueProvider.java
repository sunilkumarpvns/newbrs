package com.elitecore.exprlib.parser.expression;

import java.util.HashMap;
import java.util.Map;

/**
 * A support value provider that provides value object mapped to a particular key. Any value provider
 * that need to support mapped lookup of value should extend this class. 
 *   
 * @author narendra.pathai
 *
 */
public abstract class MappedValueProvider implements ValueProvider {
	private Map<String, Object> parameters = new HashMap<String, Object>();
	
	public MappedValueProvider(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * @return value mapped with given key, null otherwise. 
	 */
	@Override
	public Object getValue(String key) {
		return parameters.get(key);
	}
	
	/**
	 * Maps the key to the value provided.
	 * @param key a non-null key that will be used while looking up the value.
	 * @param value any object.
	 */
	public void setValue(String key, Object value) {
		parameters.put(key, value);
	}
}
