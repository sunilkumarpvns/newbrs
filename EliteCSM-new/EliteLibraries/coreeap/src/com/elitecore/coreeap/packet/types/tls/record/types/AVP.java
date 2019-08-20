package com.elitecore.coreeap.packet.types.tls.record.types;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.Utility;
@SuppressWarnings("unused")
public class AVP {
	private static final String MODULE = "APPLICATION_AVP";
	private static final int VM_FLAG = 192;
	private static final int V_FLAG = 128;	
	private static final int M_FLAG = 64;	
	private static final int NO_FLAG = 0;
	private static final int AVP_ID_LENGTH=4;
	private static final int AVP_FLAG_LENGTH=1;
	private static final int AVP_VENDORID_LENGTH=4;
	private static final int AVP_LENGTH_LENGTH=3;
	private int id;
	private int flag;
	private int length;
	private int vendorID;
	private byte[] value;
	public AVP() 
	{
		
	}
	public AVP(byte[] avpData)
	{
		setBytes(avpData);
	}
	
	public void setBytes(byte[] avpData){
		ByteArrayInputStream in = new ByteArrayInputStream(avpData);	
		try {
			readFrom(in);
		} catch (IOException e) {		
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during building AVP Object,reason : " +e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}	
		
	}
	
	public int readFrom(InputStream sourceStream) throws IOException{
		int byteRead=0,bRead=0;		
		bRead = sourceStream.read();
		if(bRead == -1){
			return(-1);					
		}
		this.id = bRead & 0xFF;
		this.id = this.id << 8;
		this.id = this.id | (sourceStream.read() & 0xFF);
		this.id = this.id << 8;
		this.id = this.id | (sourceStream.read() & 0xFF);
		this.id = this.id << 8;
		this.id = this.id | (sourceStream.read() & 0xFF);
		byteRead = byteRead + 4;
		
		this.flag = sourceStream.read() & 0xFF;
		byteRead = byteRead + 1;		

		this.length = sourceStream.read() & 0xFF;
		this.length = this.length << 8;
		this.length = this.length | (sourceStream.read() & 0xFF);
		this.length = this.length << 8;
		this.length = this.length | (sourceStream.read() & 0xFF);			
		byteRead = byteRead + 3;
		
		int tempLength = 0;
		if(this.flag == VM_FLAG || this.flag== V_FLAG)// Vendor-Specific bit is set
		{
			this.vendorID = sourceStream.read() & 0xFF;
			this.vendorID = this.vendorID << 8;
			this.vendorID = this.vendorID | (sourceStream.read() & 0xFF);
			this.vendorID = this.vendorID << 8;
			this.vendorID = this.vendorID | (sourceStream.read() & 0xFF);
			this.vendorID = this.vendorID << 8;
			this.vendorID = this.vendorID | (sourceStream.read() & 0xFF);
			byteRead = byteRead + 4;			
		}		
		tempLength = this.length - byteRead;		
		
		if(tempLength % 4 != 0)
		{
			tempLength = tempLength + (4-(tempLength%4));
		}

		this.value = new byte[tempLength];		
		bRead = sourceStream.read(this.value);
		if(bRead < tempLength){
			//TODO - Throw exception
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Length does not match with available data ");
		}else if(bRead == -1){
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Could not find value");
			return(-1);
		}
		byteRead = byteRead + bRead ;
		return(byteRead);
	}

	public byte[] getBytes(){
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			writeTo(buffer);
			buffer.close();
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during building AVP Object,reason : " +e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}		
		return buffer.toByteArray();	
	}
	public void writeTo(OutputStream out) throws IOException{
		
		out.write((byte)(this.id >>> 24));
		out.write((byte)(this.id >>> 16));
		out.write((byte)(this.id >>> 8));
		out.write((byte)(this.id));
		out.write((byte)this.flag);
		out.write((byte)(this.length>>>16));
		out.write((byte)(this.length>>>8));
		out.write((byte)(this.length));
		if(this.flag == VM_FLAG || this.flag== V_FLAG){// Vendor-Specific bit is set
			out.write((byte)(this.vendorID >>> 24));
			out.write((byte)(this.vendorID >>> 16));
			out.write((byte)(this.vendorID >>> 8));
			out.write((byte)(this.vendorID));
		}
		if(this.value != null){
			out.write(this.value);
		}	
	}	

	public int getFlag()
	{
		return (this.flag);
	}
	public int getLength()
	{
		return (this.length);
	}
	public int getCode()
	{
		return(this.id);
	}
	public int getVendorID(){
		return(this.vendorID);
	}
	public byte[] getValue()
	{
//		int valueLength = this.value.length;
//		int actualValueLength = valueLength;
//		for(int i=valueLength; i>0; i--){
//			if(this.value[i-1] == 0){
//				actualValueLength--;
//			}else{
//				break;
//			}
//		}

		int actualValueLength = getLength();
		if(this.flag == VM_FLAG || this.flag== V_FLAG){// Vendor-Specific bit is set
			actualValueLength -= 12;
		}else {
			actualValueLength -= 8;
		}

		byte[] actualValue = new byte[actualValueLength];
		System.arraycopy(this.value, 0, actualValue, 0, actualValueLength);		

		return(actualValue);
	}
	
	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n" +" ID  = " + this.id);		
		strBuilder.append("\n" +" Flag = " + this.flag);		
		strBuilder.append("\n" +" Length = " + this.length);		
		
		if(flag == VM_FLAG || flag== V_FLAG)// Vendor-Specific bit is set
		{
			strBuilder.append("\n" +" Vendor ID = " + this.vendorID);
		}
		if(this.value != null)
		{
			strBuilder.append("\n" +" value = " + Utility.bytesToHex(getValue()));
		}
		return(strBuilder.toString());
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public void setFlag(int flag) {
		this.flag = flag;		
	}
	public void setValue(byte[] value) {
		this.value = value;		
	}
	public void refreshHeader() {
		this.length = 8 + this.value.length;
		if(this.flag == VM_FLAG || this.flag == V_FLAG)
			this.length += 4;		
	}
	public void setVendorID(int vendorID) {
		this.vendorID = vendorID;
	}
}
