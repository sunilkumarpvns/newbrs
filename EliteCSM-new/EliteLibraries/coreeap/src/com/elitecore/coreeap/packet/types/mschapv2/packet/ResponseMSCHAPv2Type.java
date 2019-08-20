package com.elitecore.coreeap.packet.types.mschapv2.packet;


public class ResponseMSCHAPv2Type extends BaseMSCHAPv2Type{
	private final static int DEFAULT_LENGTH = 18; //valueSize(1) + response(16) + name(?) 
	
	public ResponseMSCHAPv2Type(){
		super(new byte[18]);
	}
	
	public int getValueSize(){
		byte[] valueBuffer = getValueBuffer();
		if(valueBuffer != null){			
			return (byte)valueBuffer[0];
		}
		return 0;
	}
	
	public void setValueSize(int valueSize){
		byte[] valueBuffer = getValueBuffer();
		if(valueBuffer == null){
			valueBuffer = new byte[DEFAULT_LENGTH];
		}
		valueBuffer[0] = (byte)valueSize;
		setValueBuffer(valueBuffer);
		setMsLength(valueBuffer.length + 3);
	}
	
	public byte[] getResponse(){
		byte[] valueBuffer = getValueBuffer();
		if(valueBuffer != null){		
			byte[] responseBytes = new byte[getValueSize()];
			System.arraycopy(valueBuffer, 1, responseBytes, 0, getValueSize());
			return responseBytes;
		}
		return null;
	}
	
	public void setResponse(byte[] responseBytes){
		if(responseBytes != null && responseBytes.length == getValueSize()){
			byte[] valueBuffer = getValueBuffer();
			if(valueBuffer == null){
				valueBuffer = new byte[DEFAULT_LENGTH];			
			}		
			System.arraycopy(responseBytes, 0, valueBuffer, 1, getValueSize());
			setValueBuffer(valueBuffer);
			setMsLength(valueBuffer.length + 3);
		}else{
			//TODO - Throw Exception
		}	
	}
	
	public byte[] getName(){
		byte[] valueBuffer = getValueBuffer();
		if(valueBuffer != null){		
			byte[] nameBytes = new byte[valueBuffer.length - (getValueSize()+1)];
			System.arraycopy(valueBuffer, getValueSize()+1, nameBytes, 0, nameBytes.length);
			return nameBytes;
		}
		return null;
	}
	
	public void setName(byte[] nameBytes){
		if(nameBytes != null ){
			byte[] valueBuffer = getValueBuffer();
			if(valueBuffer == null){
				valueBuffer = new byte[DEFAULT_LENGTH + nameBytes.length - 1];			
			}		
			System.arraycopy(nameBytes, 0, valueBuffer, DEFAULT_LENGTH, nameBytes.length);
			setValueBuffer(valueBuffer);
			setMsLength(valueBuffer.length + 3);
		}else{
			//TODO - Throw Exception
		}
		
	}
	
	public byte[] getPeerChallenge(){
		byte[] responseBytes = getResponse();
		byte[] peerchallenge = new byte[16];
		System.arraycopy(responseBytes, 0, peerchallenge, 0, 16);
		return peerchallenge;
	}
	
	public byte[] getNTResponse(){
		byte[] responseBytes = getResponse();
		byte[] ntResponse = new byte[24];
		System.arraycopy(responseBytes, 24, ntResponse, 0, 24);
		return ntResponse;
	}
	
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

}
