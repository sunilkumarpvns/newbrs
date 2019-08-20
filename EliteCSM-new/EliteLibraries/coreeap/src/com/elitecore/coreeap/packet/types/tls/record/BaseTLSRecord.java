package com.elitecore.coreeap.packet.types.tls.record;

public abstract class BaseTLSRecord implements ITLSRecord {
	
	/*ITLSRecordType tlsRecordType;
	
	public void setTLSRecordType(ITLSRecordType tlsRecordType){
		this.tlsRecordType = tlsRecordType;
	}
	public ITLSRecordType getTLSRecordType(){
		return this.tlsRecordType;
	}*/
	public Object clone()throws CloneNotSupportedException{
		return(super.clone());
	}
}
