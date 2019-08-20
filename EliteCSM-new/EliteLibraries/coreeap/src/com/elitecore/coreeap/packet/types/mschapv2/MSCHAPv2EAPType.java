package com.elitecore.coreeap.packet.types.mschapv2;

import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.packet.types.mschapv2.packet.IMSCHAPv2Type;
import com.elitecore.coreeap.packet.types.mschapv2.packet.MSCHAPv2TypeDictionary;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.mschapv2.OpCodeConstants;

public class MSCHAPv2EAPType extends EAPType {

	private static final int HEADER_LENGTH = 4;
	private int opCode;
	private IMSCHAPv2Type msCHAPv2Type;
	
	public MSCHAPv2EAPType(){
		super(EapTypeConstants.MSCHAPv2.typeId);
	}
	
	public byte[] toBytes(){
		byte[] returnBytes = null;
		if(msCHAPv2Type != null){
			returnBytes = new byte[msCHAPv2Type.toBytes().length + 2];
			returnBytes[0] = (byte)this.getType();
			returnBytes[1] = (byte)this.getOpCode();
			System.arraycopy(msCHAPv2Type.toBytes(), 0, returnBytes, 2, msCHAPv2Type.toBytes().length);
		}else{
			returnBytes = new byte[2];
			returnBytes[0] = (byte)this.getType();
			returnBytes[1] = (byte)this.getOpCode();
		}
		return returnBytes;
	}
	
	public String toString(){
		StringBuilder returnString = new StringBuilder();
		returnString.append("  Type=");
		returnString.append(this.getType());
		returnString.append("  OpCode =");
		returnString.append(this.getOpCode());
		if(this.msCHAPv2Type != null ){							
			returnString.append(this.msCHAPv2Type.toString());
		}
		return returnString.toString();
	}
	
	@Override
	public byte[] getData() {
		return toBytes();
	}

	@Override
	public void setData(byte[] data) {
		if(data != null){
			//The length of the data bytes MUST be >= 18 ( 1-ValueSize, 16-Value, 1 or More-Name)
			if(data.length >= 1){
				//For MD5-Challenge the ValueSize MUST be 16.
				if(OpCodeConstants.isValid((int)(data[0] & 0xFF))){
					this.opCode = (int)(data[0] & 0xFF);
					if(data.length > 1){
						msCHAPv2Type = MSCHAPv2TypeDictionary.getInstance().createMSCHAPv2Type(this.opCode);
						msCHAPv2Type.setIdentifier((int)(data[1] & 0xFF));					
						int msLength = data[2];
						msLength = (msLength << 8) | (data[3] & 0xFF);
						msCHAPv2Type.setMsLength(msLength);
						byte[] valueBuffer = new byte[msLength-HEADER_LENGTH];
						System.arraycopy(data, HEADER_LENGTH,valueBuffer, 0, valueBuffer.length);
						msCHAPv2Type.setValueBuffer(valueBuffer);
					}
				}else{
					throw new IllegalArgumentException("Invalid OpCode.");
				}
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
		MSCHAPv2EAPType newObject = (MSCHAPv2EAPType)super.clone();
		if (this.msCHAPv2Type!=null){			 
			newObject.msCHAPv2Type = (IMSCHAPv2Type) this.msCHAPv2Type.clone();
		}
		return newObject;
	}

	public int getOpCode() {
		return opCode;
	}

	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}

	public IMSCHAPv2Type getMsCHAPv2Type() {
		return msCHAPv2Type;
	}

	public void setMsCHAPv2Type(IMSCHAPv2Type msCHAPv2Type) {
		this.msCHAPv2Type = msCHAPv2Type;
	}

}
