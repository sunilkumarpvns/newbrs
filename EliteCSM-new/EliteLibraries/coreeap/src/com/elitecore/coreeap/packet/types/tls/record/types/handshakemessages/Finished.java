package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.tls.HandshakeMessageConstants;


public class Finished implements ITLSHandshakeMessage{
	private static final String MODULE = "FINISHED MESSAGE";
	private byte[] verifyData;
	
	public Finished() {
		
	} 
	 
	public Finished(byte[] messageData) {
		setBytes(messageData);
	}

	public byte[] getVerifyData() {
		return verifyData;
	}
	 
	public void setVerifyData(byte[] verifyData) {
		this.verifyData = new byte[verifyData.length];
		System.arraycopy(verifyData, 0, this.verifyData, 0, verifyData.length);
	}
	 
	public byte[] getBytes() {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			writeTo(buffer);
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during method call - getBytes, reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		return buffer.toByteArray();
	}	

	public void setBytes(byte[] handshakeMessageBytes) {
		// TODO Auto-generated method stub
		ByteArrayInputStream in = new ByteArrayInputStream(handshakeMessageBytes);		
		try {
			readFrom(in);
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during method call - setBytes, reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}					
	}

	public int readFrom(InputStream sourceStream) throws IOException {
					
			this.verifyData = new byte[12];
			sourceStream.read(this.verifyData);
		return(12);
	}

	public void writeTo(OutputStream out) throws IOException {
		if(this.verifyData != null)
			out.write(this.verifyData,0,this.verifyData.length);
	}
	public Object clone()throws CloneNotSupportedException{		
		Finished finished = null;
		
		finished= (Finished)super.clone();
		finished.verifyData = null;
		return(finished);
	}

	public int getType() {
		return HandshakeMessageConstants.Finished.value;
	}
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n" +" Verify Data = ");
		if(this.verifyData != null)
			strBuilder.append(Utility.bytesToHex(this.verifyData));
		else
			strBuilder.append("?");
		return(strBuilder.toString());
	}

}
 
