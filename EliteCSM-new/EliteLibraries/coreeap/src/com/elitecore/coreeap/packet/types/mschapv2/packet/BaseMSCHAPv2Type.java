package com.elitecore.coreeap.packet.types.mschapv2.packet;

public class BaseMSCHAPv2Type  implements IMSCHAPv2Type{
	private final static int HEADER_LENGTH = 3;
	private int identifier;
	private int msLength;
	private byte[] valueBuffer;
	
	public BaseMSCHAPv2Type(){
		this.valueBuffer = new byte[0];
	}
	
	public BaseMSCHAPv2Type(byte[] valueBuffer){
		this.valueBuffer = valueBuffer;
	}
	public byte[] toBytes(){
		byte[] returnBytes = new byte[HEADER_LENGTH + valueBuffer.length];		
		returnBytes[0] = (byte)this.getIdentifier();
		byte [] lengthArray = { (byte)((getMsLength() >>> 8) & 0xFF), ((byte)(getMsLength() & 0xFF))};
		returnBytes[1] = lengthArray[0];
		returnBytes[2] = lengthArray[1];
		System.arraycopy(valueBuffer, 0, returnBytes, HEADER_LENGTH, this.valueBuffer.length);		
		return returnBytes;
	}
	
	public String toString(){
		StringBuilder returnString = new StringBuilder();
		returnString.append("  Identifier =");
		returnString.append(this.getIdentifier());
		returnString.append("  MS-Length =");
		returnString.append(this.getMsLength());
		if(this.valueBuffer != null ){			
			returnString.append("  Value =");		
			returnString.append(bytesToHex(this.valueBuffer));
		}
		return returnString.toString();
	}
	
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public int getMsLength() {
		return msLength;
	}

	public void setMsLength(int msLength) {
		this.msLength = msLength;
	}
	
	public Object clone() throws CloneNotSupportedException{
		BaseMSCHAPv2Type newObject = (BaseMSCHAPv2Type) super.clone();
		if(valueBuffer != null){
			newObject.valueBuffer = new byte[this.valueBuffer.length];
			System.arraycopy(this.valueBuffer, 0, newObject.valueBuffer, 0, this.valueBuffer.length);
		}
		return super.clone();
	}

	public byte[] getValueBuffer() {
		return valueBuffer;
	}

	public void setValueBuffer(byte[] data) throws IllegalArgumentException {
		this.valueBuffer = data;
	}
	private String bytesToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        buf.append("0x");
        for (int i = 0; i < data.length; i++) {
        	buf.append(byteToHex(data[i]));
        	//buf.append("\\");
        }
        return (buf.toString());
    }

	private String byteToHex(byte data) {
		StringBuffer buf = new StringBuffer();
		buf.append(toHexChar((data >>> 4) & 0x0F));
		buf.append(toHexChar(data & 0x0F));
		return buf.toString();
	}
	
	private char toHexChar(int i) {
        if ((0 <= i) && (i <= 9)) {
            return (char) ('0' + i);
        } else {
            return (char) ('a' + (i - 10));
        }
    }

}
