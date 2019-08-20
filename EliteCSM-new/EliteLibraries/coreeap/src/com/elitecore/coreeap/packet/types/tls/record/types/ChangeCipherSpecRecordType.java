package com.elitecore.coreeap.packet.types.tls.record.types;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.constants.tls.TLSRecordConstants;

public class ChangeCipherSpecRecordType extends BaseTLSRecordType{
	private final static String MODULE = "TLS_CCS_RECORD";
	private int changeCipherSpecType;
	
	public ChangeCipherSpecRecordType(){
		
	}
	
	public ChangeCipherSpecRecordType(int type){
		setChangeCipherSpecType(type);
	}
	
	public void setBytes(byte[] messageData){
		ByteArrayInputStream in = new ByteArrayInputStream(messageData);
		try {
			readFrom(in);
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during method call - setBytes ,reason :"+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	public int readFrom(InputStream sourceStream) throws IOException{
		setChangeCipherSpecType(sourceStream.read() & 0xFF);
		return(1);
	}
	public byte[] getBytes(){
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			writeTo(buffer);
			buffer.close();
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during method call - getBytes ,reason :"+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}		
		return buffer.toByteArray();
	}
	
	public void writeTo(OutputStream out) throws IOException{		
		out.write((byte)getChangeCipherSpecType() & 0xFF);
	}
	
	public int getChangeCipherSpecType() {
		return changeCipherSpecType;
	}
	
	public void setChangeCipherSpecType(int type) {
		this.changeCipherSpecType = type;
	}
	
	public Object clone()throws CloneNotSupportedException{
		ChangeCipherSpecRecordType changeCipherSpecRecordType = (ChangeCipherSpecRecordType)super.clone();
		changeCipherSpecRecordType.changeCipherSpecType = 1;
		return(changeCipherSpecRecordType);
	}

	public int getType() {
		return TLSRecordConstants.ChangeCipherSpec.value;
	}
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n" +" Change Cipher Spec Message");
		return(strBuilder.toString());
	}
}
