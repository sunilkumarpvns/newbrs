package com.elitecore.aaa.core.drivers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * This tuple stores Database Type and value pairs.
 * 
 * @author narendra.pathai
 *
 */
public class DBTypeAndValueTuple {
	
	private final String type;
	private final String value;
	
	/**
	 * Creates database type to value tuple
	 * @param type any non-null type
	 * @param value any value (can be null)
	 */
	public DBTypeAndValueTuple(@Nonnull String type, @Nullable String value){
		this.type = type;
		this.value = value;
	}
	
	public @Nonnull String getType() {
		return this.type;
	}
	
	public @Nullable String getValue(){
		return this.value;
	}
}
