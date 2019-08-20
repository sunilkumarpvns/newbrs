package com.elitecore.coreeap.packet.types.aka;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.packet.InvalidEAPTypeException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.packet.types.sim.SIMAttributeDictionary;
import com.elitecore.coreeap.packet.types.sim.attributes.ISIMAttribute;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.sim.message.SIMMessageTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AkaEapType extends EAPType {	

	private int akaMessageType;
	private byte[] akaDataBytes; 
	
	public byte[] getAkaDataBytes() {
		return akaDataBytes;
	}

	public void setAkaDataBytes(byte[] akaDataBytes) {
		this.akaDataBytes = akaDataBytes;
	}

	public AkaEapType() {
		super(EapTypeConstants.AKA.typeId);
		this.akaMessageType = 0;
	}

	public AkaEapType(byte[] byteArray) throws InvalidEAPTypeException {
		super(byteArray);
		if(super.getType() == EapTypeConstants.AKA.typeId){			
			byte[] simData = super.getData();
			setAkaMessageType(simData[0]);
			int readLength = 3;
			this.akaDataBytes = new byte[simData.length - readLength]; 
			System.arraycopy(simData, readLength, this.akaDataBytes, 0, simData.length - readLength);
		}else{
			throw new InvalidEAPTypeException("EAP Type : not AKA");
		}
	}

	public void setData(byte[] data){
		super.setData(data);
		if(data != null){
			setAkaMessageType(data[0]);
			int readLength = 3;
			this.akaDataBytes = new byte[data.length - readLength]; 
			System.arraycopy(data, readLength, this.akaDataBytes, 0, data.length - readLength);			
		}
	}
	public byte[] toBytes(){
		byte[] returnBytes = null;
		if(this.akaDataBytes == null){
			returnBytes = new byte[4];
			returnBytes[0] = (byte)super.getType();
			returnBytes[1] = (byte)getAkaMessageType();
			return returnBytes;
		}else{
			returnBytes = new byte[4 + this.akaDataBytes.length];
			returnBytes[0] = (byte)super.getType();
			returnBytes[1] = (byte)getAkaMessageType();
			System.arraycopy(this.akaDataBytes, 0, returnBytes, 4, this.akaDataBytes.length);
		}
		return returnBytes;
	}
	
	public String toString(){
		StringBuilder returnString = new StringBuilder();
		returnString.append(" Type = " + EapTypeConstants.getName(getType()) + "(" + getType() +")");
		returnString.append("\n  AKA Message Type=");
		returnString.append(SIMMessageTypeConstants.getName(this.getAkaMessageType()) + "(" + this.getAkaMessageType() + ")");
		returnString.append("\n  AKA Attributes = ");
		List<byte[]> akaAttributeList = getAkaAttributes();
		for(byte[] bt : akaAttributeList){			
			ISIMAttribute simAttribute = SIMAttributeDictionary.getInstance().getAttribute((bt[0] & 0xFF));
			simAttribute.setBytes(bt);
			returnString.append("\n" + simAttribute);
		}
		//returnString.append(Utility.bytesToHex(this.simDataBytes));
		return returnString.toString();
	}		

	/* (non-Javadoc)
	 * @see com.elitecore.radius.eap.packet.EAPType#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		AkaEapType newObject = (AkaEapType)super.clone();
		if (this.akaDataBytes!=null){
			newObject.akaDataBytes = new byte[this.akaDataBytes.length];
			System.arraycopy(this.akaDataBytes,0,newObject.akaDataBytes,0,this.akaDataBytes.length);
		}
		return newObject;
	}

	public int getAkaMessageType() {
		return akaMessageType;
	}

	public void setAkaMessageType(int subType) {
		this.akaMessageType = subType;
	}	
	
	public List<byte[]> getAkaAttributes(){
		List<byte[]> akaRecords = new ArrayList<byte[]>();
		if(this.akaDataBytes != null)
		{
			int lengthOfAllRecords = this.akaDataBytes.length;
			int lengthRead = 0;			
			byte[] singleRecordData = null;
			
			while(lengthRead < lengthOfAllRecords){				
				int length = this.akaDataBytes[lengthRead+1] & 0xFF;				 
				length = length * 4;
				singleRecordData = new byte[length];
				LogManager.getLogger().debug("Aka EAP Type ", "Aka Data bytes : " + TLSUtility.bytesToHex(this.akaDataBytes));
				LogManager.getLogger().debug("Aka EAP Type", "Length : " + length);
				LogManager.getLogger().debug("Aka EAP Type", "Aka Data bytes Length : " + this.akaDataBytes.length);
				LogManager.getLogger().debug("Aka EAP Type", "Length Read : " + lengthRead);				
				System.arraycopy(this.akaDataBytes, lengthRead, singleRecordData, 0, length);		
				lengthRead += singleRecordData.length;
				akaRecords.add(singleRecordData);
			}
		}
		return akaRecords;
	}	

	public List<ISIMAttribute> getAkaAttrList(){
		List<ISIMAttribute> simAttrList = new ArrayList<ISIMAttribute>();
		List<byte[]> akaAttributeList = getAkaAttributes();
		for(byte[] bt : akaAttributeList){	
//			Logger.logDebug(strModule, strMessage)
			ISIMAttribute simAttribute = SIMAttributeDictionary.getInstance().getAttribute((bt[0] & 0xFF));
			simAttribute.setBytes(bt);		
			simAttrList.add(simAttribute);
		}
		return simAttrList;
	}

	public void setAkaAttrList(List<ISIMAttribute> akaAttrList){
		this.akaDataBytes = null;
		if(akaAttrList != null){
			for(ISIMAttribute akaAttribute : akaAttrList){
				setAkaAttributes(akaAttribute.getBytes());
			}
		}
	}

	public void setAkaAttributes(byte[] akaAttr){		
		this.akaDataBytes = TLSUtility.appendBytes(this.akaDataBytes,akaAttr);
	}
}
