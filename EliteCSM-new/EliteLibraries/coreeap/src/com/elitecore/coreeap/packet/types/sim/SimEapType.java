package com.elitecore.coreeap.packet.types.sim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.packet.InvalidEAPTypeException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.packet.types.sim.attributes.ISIMAttribute;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.sim.message.SIMMessageTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class SimEapType extends EAPType {	

	private int simMessageType;
	private byte[] simDataBytes; 
	
	public SimEapType() {
		super(EapTypeConstants.SIM.typeId);
		this.simMessageType = 0;
	}

	public SimEapType(byte[] byteArray) throws InvalidEAPTypeException {
		super(byteArray);
		if(super.getType() == EapTypeConstants.SIM.typeId){			
			byte[] simData = super.getData();
			setSimMessageType(simData[0]);
			int readLength = 3;
			this.simDataBytes = new byte[simData.length - readLength]; 
			System.arraycopy(simData, readLength, this.simDataBytes, 0, simData.length - readLength);
		}else{
			throw new InvalidEAPTypeException("EAP Type : not SIM");
		}
	}

	public void setData(byte[] data){
		super.setData(data);
		if(data != null){
			setSimMessageType(data[0]);
			int readLength = 3;
			this.simDataBytes = new byte[data.length - readLength]; 
			System.arraycopy(data, readLength, this.simDataBytes, 0, data.length - readLength);			
		}
	}
	public byte[] toBytes(){
		byte[] returnBytes = null;
		if(this.simDataBytes == null){
			returnBytes = new byte[4];
			returnBytes[0] = (byte)super.getType();
			returnBytes[1] = (byte)getSimMessageType();
			return returnBytes;
		}else{
			returnBytes = new byte[4 + this.simDataBytes.length];
			returnBytes[0] = (byte)super.getType();
			returnBytes[1] = (byte)getSimMessageType();
			System.arraycopy(this.simDataBytes, 0, returnBytes, 4, this.simDataBytes.length);
		}
		return returnBytes;
	}
	
	public String toString(){
		StringBuilder returnString = new StringBuilder();
		returnString.append(" Type = " + EapTypeConstants.getName(getType()) + "(" + getType() +")");
		returnString.append("\n  SIM Message Type=");
		returnString.append(SIMMessageTypeConstants.getName(this.getSimMessageType()) + "(" + this.getSimMessageType() + ")");
		returnString.append("\n  SIM Attributes = ");
		List<byte[]> simAttributeList = getSimAttributes();
		for(byte[] bt : simAttributeList){			
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
		SimEapType newObject = (SimEapType)super.clone();
		if (this.simDataBytes!=null){
			newObject.simDataBytes = new byte[this.simDataBytes.length];
			System.arraycopy(this.simDataBytes,0,newObject.simDataBytes,0,this.simDataBytes.length);
		}
		return newObject;
	}

	public int getSimMessageType() {
		return simMessageType;
	}

	public void setSimMessageType(int subType) {
		this.simMessageType = subType;
	}	
	
	public List<byte[]> getSimAttributes(){
		List<byte[]> simRecords = new ArrayList<byte[]>();
		if(this.simDataBytes != null)
		{
			int lengthOfAllRecords = this.simDataBytes.length;
			int lengthRead = 0;			
			byte[] singleRecordData = null;
			
			while(lengthRead < lengthOfAllRecords){				
				int length = this.simDataBytes[lengthRead+1] & 0xFF;				 
				length = length * 4;
				singleRecordData = new byte[length];
				LogManager.getLogger().debug("Sim EAP Type ", "Sim Data bytes : " + TLSUtility.bytesToHex(this.simDataBytes));
				LogManager.getLogger().debug("Sim EAP Type", "Length : " + length);
				LogManager.getLogger().debug("Sim EAP Type", "Sim Data bytes Length : " + this.simDataBytes.length);
				LogManager.getLogger().debug("Sim EAP Type", "Length Read : " + lengthRead);				
				System.arraycopy(this.simDataBytes, lengthRead, singleRecordData, 0, length);		
				lengthRead += singleRecordData.length;
				simRecords.add(singleRecordData);
			}
		}
		return simRecords;
	}	

	public List<ISIMAttribute> getSimAttrList(){
		List<ISIMAttribute> simAttrList = new ArrayList<ISIMAttribute>();
		List<byte[]> simAttributeList = getSimAttributes();
		for(byte[] bt : simAttributeList){	
//			Logger.logDebug(strModule, strMessage)
			ISIMAttribute simAttribute = SIMAttributeDictionary.getInstance().getAttribute((bt[0] & 0xFF));
			simAttribute.setBytes(bt);		
			simAttrList.add(simAttribute);
		}
		return simAttrList;
	}

	public ISIMAttribute getSimAttribute (int type){
		ISIMAttribute attrb = null;
		List<ISIMAttribute> simAttrList = getSimAttrList();
		Iterator<ISIMAttribute> itr = simAttrList.iterator();
		while (itr.hasNext()){
			attrb = itr.next();
			if (attrb.getType() == type ){
				return attrb;
			}
		}
		return null;
	}

	public void setSimAttrList(List<ISIMAttribute> simAttrList){
		this.simDataBytes = null;
		if(simAttrList != null){
			for(ISIMAttribute simAttribute : simAttrList){
				setSimAttributes(simAttribute.getBytes());
			}
		}
	}

	public void setSimAttributes(byte[] simAttr){		
		this.simDataBytes = TLSUtility.appendBytes(this.simDataBytes,simAttr);
	}
}
