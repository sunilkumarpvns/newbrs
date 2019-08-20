package com.elitecore.test.radius.testcase.data;

public class AttributeData {
	
	private int attributeId;
	private byte[] attributeBytes;
	
	public AttributeData (int attributeId, byte[] attributeBytes){
		this.attributeId = attributeId;
		this.attributeBytes = attributeBytes;
	}
	
	public byte[] getAttributeBytes() {
		return attributeBytes;
	}
	public void setAttributeBytes(byte[] attributeBytes) {
		this.attributeBytes = attributeBytes;
	}
	public int getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}
	
	
}
