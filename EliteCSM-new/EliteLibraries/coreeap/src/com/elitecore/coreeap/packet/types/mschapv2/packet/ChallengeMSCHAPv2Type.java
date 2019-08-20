package com.elitecore.coreeap.packet.types.mschapv2.packet;


public class ChallengeMSCHAPv2Type extends BaseMSCHAPv2Type{
	private final static int DEFAULT_LENGTH = 17; //valueSize(1) + challenge(16) + name(?) 
	
	public ChallengeMSCHAPv2Type(){
		super(new byte[17]);
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
		setMsLength(valueBuffer.length + 4);
	}
	
	public byte[] getChallenge(){
		byte[] valueBuffer = getValueBuffer();
		if(valueBuffer != null){		
			byte[] challengeBytes = new byte[getValueSize()];
			System.arraycopy(valueBuffer, 1, challengeBytes, 0, getValueSize());
			return challengeBytes;
		}
		return null;
	}
	
	public void setChallenge(byte[] challengeBytes){
		if(challengeBytes != null && challengeBytes.length == getValueSize()){
			byte[] valueBuffer = getValueBuffer();
			if(valueBuffer == null){
				setValueSize(16);
				valueBuffer = new byte[DEFAULT_LENGTH];			
			}		
			System.arraycopy(challengeBytes, 0, valueBuffer, 1, getValueSize());
			setValueBuffer(valueBuffer);
			setMsLength(valueBuffer.length + 4);
		}else{
			//TODO - Throw Exception
		}	
		
	}
	
	public byte[] getName(){
		byte[] valueBuffer = getValueBuffer();
		if(valueBuffer != null){		
			byte[] nameBytes = new byte[valueBuffer.length - (getValueSize()+1)];
			System.arraycopy(valueBuffer, getValueSize(), nameBytes, 0, nameBytes.length);			
			return nameBytes;
		}
		return null;
	}
	
	public void setName(byte[] nameBytes){
		if(nameBytes != null ){
			byte[] valueBuffer = getValueBuffer();
			if(valueBuffer == null){
				valueBuffer = new byte[getValueSize() + nameBytes.length + 1];			
			}else{
				byte[] tempBuffer = new byte[valueBuffer.length + nameBytes.length ];
				System.arraycopy(valueBuffer, 0, tempBuffer, 0, valueBuffer.length);
				valueBuffer = tempBuffer;
			}
			System.arraycopy(nameBytes, 0, valueBuffer, getValueSize()+1, nameBytes.length);
			setValueBuffer(valueBuffer);
			setMsLength(valueBuffer.length + 4);
		}else{
			//TODO - Throw Exception
		}
		
	}
	
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
}
