package com.elitecore.coreradius.commons.packet;

public abstract class BaseRadiusPacket implements IRadiusPacket, Cloneable {
	
	private static final long serialVersionUID = 1L;
	private long packetCreationTimeMillis;
	
	public BaseRadiusPacket(){
		packetCreationTimeMillis = System.currentTimeMillis();
	}
	
    public long creationTimeMillis() {
    	return packetCreationTimeMillis;
    }
    
}
