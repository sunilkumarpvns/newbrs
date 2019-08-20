package com.elitecore.aaa.radius.sessionx.data;

public final class PropertyType {
	public static final int ACCESS_REQ = 1;
	public static final int ACCESS_RES = 2;
	private int packetType = ACCESS_REQ;
	private String propertyName;
	
	PropertyType() {
		//suppress the use of default constructor
	}
	
	public PropertyType(String propertyName, int packetType){
		this.propertyName = propertyName;
		this.packetType = packetType;
	}
	
	public int getPacketType(){
		return this.packetType;
	}
	
	public String getPropertyName(){
		return this.propertyName;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nPacket Type: " + this.packetType);
		buffer.append("\nProperty Name: " + this.propertyName);
		return buffer.toString();
	}
}
