package com.elitecore.coreeap.packet.types.tls.record.attribute;

public class ContentType {
	
	private int type;

	public ContentType(){
		
	}
	
	public ContentType(int type){
		setType(type);
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("[ Content Type = " + this.type + " ]");
		return(strBuilder.toString());
	}
	
}
