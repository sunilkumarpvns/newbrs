package com.elitecore.coreeap.packet.types.tls.record.types;

import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;



public abstract class BaseTLSRecordType implements ITLSRecordType {

	private ProtocolVersion protocolVersion;
	
	public String toString(){
		//TODO Auto-generated method stub
		return (null);
	}
	public Object clone()throws CloneNotSupportedException{
		return(super.clone());
	}
	
	/**
	 * 
	 * @param protocolVersion Minor protocol version value
	 * 							for Tls1.0 ---> 1,
	 * 							for Tls1.1 ---> 2,
	 * 							for Tls1.2 ---> 3
	 * 								
	 */
	public void setProtocolVersion(ProtocolVersion protocolVersion){
		this.protocolVersion = protocolVersion;
	}
	
	public ProtocolVersion getProtocolVersion(){
		return this.protocolVersion;
	}
	
}
