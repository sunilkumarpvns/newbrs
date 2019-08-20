package com.elitecore.diameterapi.diameter.translator.operations.data;

public class AttributeMapping {
	
	private Key<?> sourceKey;
	private Key<?> destinationKey;

	public AttributeMapping(Key<?> sourceKey, Key<?> destinationKey) {
		this.destinationKey = destinationKey;
		this.sourceKey = sourceKey;
	}
	
	public Key<?> getSourceKey() {
		return sourceKey;
	}

	public Key<?> getDestinationKey() {
		return destinationKey;
	}

}
