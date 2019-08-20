package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.tls.HandshakeMessageConstants;

public class ServerKeyExchange implements ITLSHandshakeMessage{

	private static final String MODULE = "SERVER_KEY_EXCHANGE";
	
	private byte[] byteServerParams;
	private byte[] byteSignature;
	
	/***
	 * 0                    2                   4                   6                    8 (in Bytes)           
     * -----------------------------------------------------------------------------------
     * |	First Server	| 			First Server Parameter according to length....									
     * |  Parameter Length	|
     * -----------------------------------------------------------------------------------
     * |	Second Server	| 			Second Server Parameter according to length....									
     * |  Parameter Length	|
     * -----------------------------------------------------------------------------------
     * |								........							
     * | 										
     * -----------------------------------------------------------------------------------
     * |		Signature						|				Signature
     * |	 									|				  Bytes
     * -----------------------------------------------------------------------------------
     * 
     * Reference = [ RFC-2246 -  The TLS Protocol Version 1.0]
     * EncryptedPreMasterSecret	= Section 7.4.3  Server Key Exchange 
	 */
	 
	public ServerKeyExchange() {
		//super(ITLSHandshakeMessageConstants.SERVER_KEY_EXCHANGE_CODE);				
		byteSignature = new byte[0];
	}	 	
	
	public void setServerParams(byte[] serverParams) {
		this.byteServerParams = new byte[serverParams.length];
		System.arraycopy(serverParams,0,this.byteServerParams,0,serverParams.length);
	}
	/**
	 *	Signature bytes should include length of two bytes. 
	 * 
	 * @param signatureBytes
	 */
	public void setSignature(byte[] signatureBytes) {
		if(signatureBytes == null) 
			throw new IllegalArgumentException("Invalid value NULL for signature");
		
		this.byteSignature = signatureBytes;
	}
	 
	public String toString() {
		StringBuffer strBuilder = new StringBuffer();
		strBuilder.append(" Server Params = ");
		if(this.byteServerParams != null)
			strBuilder.append(Utility.bytesToHex(this.byteServerParams));
		else
			strBuilder.append("?");	
		strBuilder.append(" Server Signature = ");
		if(this.byteSignature != null)
			strBuilder.append(Utility.bytesToHex(this.byteSignature));
		else
			strBuilder.append("?");
		return(strBuilder.toString());
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
		
	}

	public int readFrom(InputStream sourceStream) throws IOException {
		return(0);
	}

	public void writeTo(OutputStream out) throws IOException {
		out.write(byteServerParams);
		out.write(byteSignature);
	}

	public Object clone()throws CloneNotSupportedException{
		ServerKeyExchange serverKeyExchange = (ServerKeyExchange)super.clone();
		serverKeyExchange.byteServerParams = new byte[0];
		serverKeyExchange.byteSignature = new byte[0];
		return serverKeyExchange;
	}

	public int getType() {
		return HandshakeMessageConstants.ServerKeyExchange.value;
	}
}
 
