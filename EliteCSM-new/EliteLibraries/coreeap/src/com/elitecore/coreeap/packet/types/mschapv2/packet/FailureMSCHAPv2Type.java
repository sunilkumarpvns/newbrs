package com.elitecore.coreeap.packet.types.mschapv2.packet;

public class FailureMSCHAPv2Type extends BaseMSCHAPv2Type{
	
	public FailureMSCHAPv2Type(){
		
	}
	
	public byte[] getMessage(){
		return getValueBuffer();
	}
	
	public void setMessage(byte[] messageBytes){
		setValueBuffer(messageBytes);
		setMsLength(messageBytes.length + 4);
	}

	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

}
