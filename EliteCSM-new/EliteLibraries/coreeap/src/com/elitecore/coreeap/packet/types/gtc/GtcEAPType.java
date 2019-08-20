package com.elitecore.coreeap.packet.types.gtc;

import com.elitecore.coreeap.packet.InvalidEAPTypeException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.util.constants.EapTypeConstants;

public class GtcEAPType extends EAPType {

	private byte[] token; 
	
	public GtcEAPType() {
		super(EapTypeConstants.GTC.typeId);
	}
    
	public GtcEAPType(byte[] byteArray) throws InvalidEAPTypeException {
		super(byteArray);
		if(super.getType() == EapTypeConstants.GTC.typeId){
			this.setType(super.getType());
		}else{
			throw new InvalidEAPTypeException("EAP Type : not GTC");
		}
	}

	public byte[] toBytes(){
		byte[] returnBytes = new byte[this.token.length + 1];
		returnBytes[0] = (byte)this.getType();		
		System.arraycopy(this.token, 0, returnBytes, 1, this.token.length);		
		return returnBytes;
	}
	
	public String toString(){
		StringBuilder returnString = new StringBuilder();
		returnString.append("  Type=");
		returnString.append(this.getType());
		if(this.token != null){
			returnString.append("  Token=");
			returnString.append(bytesToHex(this.token));
		}
		return returnString.toString();
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
	

	@Override
	public byte[] getData() {
		byte[] tempDataBytes = new byte[this.token.length];				
		if (this.token !=null){
			System.arraycopy(this.token, 0, tempDataBytes, 0, this.token.length);
		}
		return tempDataBytes;
	}

	@Override
	public void setData(byte[] data) {
		if(data != null){
			//The length of the data bytes MUST be >= 18 ( 1-ValueSize, 16-Value, 1 or More-Name)
			if(data.length >= 1){							
				this.token = new byte[data.length];
				System.arraycopy(data, 0, this.token, 0, data.length);
			}else{
				throw new IllegalArgumentException("Length less than minimum required length.");
			}
		}else{
			throw new IllegalArgumentException("Data null.");
		}
	}

	/* (non-Javadoc)
	 * @see com.elitecore.radius.eap.packet.EAPType#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		GtcEAPType newObject = (GtcEAPType)super.clone();
		if (this.token!=null){
			newObject.token = new byte[this.token.length];
			System.arraycopy(this.token, 0, newObject.token, 0, this.token.length);
		}
		return newObject;
	}

}
