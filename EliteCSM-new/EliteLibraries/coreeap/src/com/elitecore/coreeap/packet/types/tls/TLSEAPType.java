package com.elitecore.coreeap.packet.types.tls;


import java.util.ArrayList;
import java.util.List;

import com.elitecore.coreeap.packet.InvalidEAPTypeException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.tls.TLSFlagConstants;

public class TLSEAPType extends EAPType {
	private int flagValue; 
	 
	private int iTLSMessageLength;	 
	 
	private byte[] tlsDataBytes = null;
	 
	public TLSEAPType() {
		super(EapTypeConstants.TLS.typeId);
		this.flagValue=0;
		this.iTLSMessageLength = 0;		
	}
	 
	public TLSEAPType(byte[] data) throws InvalidEAPTypeException {	
		super(data);			
		byte[] tlsData = super.getData();
		int readLength = 1;
		this.flagValue = (int)(tlsData[0] & 0xFF) & 224;
		if(this.flagValue >= 128){
			this.iTLSMessageLength = Utility.readint(tlsData, 1, 4);
			readLength = readLength + 4;
		}
		System.arraycopy(tlsData, readLength, this.tlsDataBytes, 0, tlsData.length - readLength);
	}
	 
	public void setTLSType(int type){
		this.setType(type);
	}
	public int getFlagValue() {
		return this.flagValue;
	}
	 
	public void setFlagValue(int flagValue) {
		this.flagValue = flagValue;
	}
	 
	public int getTLSMessageLength() {
		return this.iTLSMessageLength;
	}
	 
	public void setTLSMessageLength(int TLSMessageLength) {
		this.iTLSMessageLength = TLSMessageLength;
	}
	 
/*	public void addRecords(ProcessTLSResult tlsRecords) {
		this.TLSRecords.addAll(tlsRecords.getTLSRecord());
	}
	
	public Collection getRecords(){
		return this.TLSRecords;
	}*/
	 
	public void setTLSData(byte[] data) {
		if(data != null){
			if(data.length > 3){
				this.tlsDataBytes = new byte[data.length];				
				System.arraycopy(data, 0, this.tlsDataBytes, 0, data.length);
			}else{
				throw new IllegalArgumentException("Minimum 3 bytes of data required.");
			}
		}else{
			throw new IllegalArgumentException("Data null.");
		}
	}
	 
	public byte[] getTLSData() {
		return this.tlsDataBytes;
	}	
	
	public List<byte[]> getTLSRecords(){
		List<byte[]> tlsRecords = new ArrayList<byte[]>();
		if(this.tlsDataBytes != null)
		{
			int lengthOfAllRecords = this.tlsDataBytes.length;
			int lengthRead = 0;			
			byte[] singleRecordData = null;
			
			while(lengthRead < lengthOfAllRecords){
				
				int length = Utility.readint(this.tlsDataBytes, lengthRead + 3, 2);
//				Logger.logTrace(MODULE, "Length in getTLSRecords : "+length );
//				Logger.logTrace(MODULE, "Length Read : "+lengthRead);
				singleRecordData = new byte[length + 5];
				//TLSRecord receivedRecord = TLSRecordFactory.getInstance().createTLSRecord((int)(this.data[0] & 0xFF));
				
				System.arraycopy(this.tlsDataBytes, lengthRead, singleRecordData, 0, length + 5);			
				//receivedRecord(singleRecordData);
				// TODO Modified			 
				lengthRead += singleRecordData.length;
				tlsRecords.add(singleRecordData);
			}
		}
		return tlsRecords;
	}
	
	
	public void setData(byte[] data){
		super.setData(data);
		if(data != null){
			//The length of the data bytes MUST be >= 1 ( 1-flag)
			if(data.length >= 1){
					int readLength = 1;
					this.flagValue = (int)(data[0] & 0xFF) & 224;
					if(this.flagValue >= 128)
					{
						this.iTLSMessageLength = Utility.readint(data,1,4);						
						readLength = readLength + 4;
					}														
					this.tlsDataBytes = new byte[data.length - readLength];
					System.arraycopy(data, readLength, this.tlsDataBytes, 0, data.length - readLength);
			}else{
				throw new IllegalArgumentException("Length less than minimum required length.");
			}
		}else{
			throw new IllegalArgumentException("Data null.");
		}
	}	
	
	public byte[] getData() {
		return super.getData();
	}
	
	public byte[] toBytes(){
		
		byte[] returnBytes = null;
		//int tempLength;	
						
		if(this.tlsDataBytes == null){
			
		returnBytes = new byte[2];
		returnBytes[0] = (byte)super.getType();
		returnBytes[1] = (byte)this.getFlagValue();
		return returnBytes;
		}
		else
		{
			if(this.flagValue >= 128){
				returnBytes = new byte[6 + this.tlsDataBytes.length];
				returnBytes[0] = (byte)super.getType();
				returnBytes[1] = (byte)this.getFlagValue();		
				returnBytes[5] = (byte)this.iTLSMessageLength;
				//tempLength = this.TLSMessageLength >>> 8;
				returnBytes[4] = (byte)(this.iTLSMessageLength >>> 8);
				//tempLength = tempLength >>> 8;
				returnBytes[3] = (byte)(this.iTLSMessageLength >>> 16);
				//tempLength = tempLength >>> 8;
				returnBytes[2] = (byte)(this.iTLSMessageLength >>> 24);				
				System.arraycopy(this.tlsDataBytes, 0, returnBytes, 6, this.tlsDataBytes.length);				
			}else {
				returnBytes = new byte[2 + this.tlsDataBytes.length];
				returnBytes[0] = (byte)super.getType();
				returnBytes[1] = (byte)this.getFlagValue();		
				System.arraycopy(this.tlsDataBytes, 0, returnBytes, 2, this.tlsDataBytes.length);			
			}

		}		
		return returnBytes;
	}
	
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(" Type = " + EapTypeConstants.getName(getType()) + "(" + getType() +")");
		strBuilder.append("\n" +" FlagValue = " + TLSFlagConstants.getName(this.flagValue) + "(" + this.flagValue + ")");
		
		return strBuilder.toString();
	}
	
/*	protected Object clone() throws CloneNotSupportedException {
		TLSEAPType newObject = (TLSEAPType)super.clone();
		newObject.flagValue = this.flagValue;
		newObject.TLSMessageLength = this.TLSMessageLength;
		if (this.data!=null){
			newObject.data= new byte[this.data.length];
			System.arraycopy(this.data, 0, newObject.data, 0, this.data.length);
		}
		
		return newObject;
	}
	
	private static void printBytes(String title, byte[] byteArray){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(title);
		strBuilder.append("-->");
		for (int i = 0; i < byteArray.length; i++) {
			byte b = byteArray[i];
			strBuilder.append((b & 0xff)+" ");
		}
		Logger.logInfo("TLSEAPType ", strBuilder.toString());
	}*/
	
	public Object clone() throws CloneNotSupportedException {
		TLSEAPType eapType = (TLSEAPType)super.clone();
		if (this.tlsDataBytes!=null){
			eapType.tlsDataBytes = new byte[this.tlsDataBytes.length];
			System.arraycopy(this.tlsDataBytes,0,eapType.tlsDataBytes,0,this.tlsDataBytes.length);
		}
		return eapType;
	}
	
}
 
