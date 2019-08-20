package com.elitecore.diameterapi.core.common.session;

public enum SessionFactoryType {
	INMEMORY(1),
	HAZELCAST(2),
	NULLSESSION(3);
	
	private int factoryId;
	
	private SessionFactoryType(int factoryId) {
		this.factoryId = factoryId;
	}
	
	
}
