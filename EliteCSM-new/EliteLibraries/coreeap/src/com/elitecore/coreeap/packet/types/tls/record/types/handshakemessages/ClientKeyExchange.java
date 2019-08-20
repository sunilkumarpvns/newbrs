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

public class ClientKeyExchange implements ITLSHandshakeMessage{
	private static final String MODULE = "CLIENTKEYEXCHANGE MESSAGE";
	private final int DEFAULT_CLIENT_KEY_EXCHANGE_LENGTH=2;
	private int length;
	private byte[] keyExchangeValue;
	
	public ClientKeyExchange() {
	
	}
	 
	public ClientKeyExchange(byte[] messageData) {	
		//parseMessage(messageData);
		setBytes(messageData);
	}	
    /***  
     * 0                    2                   4                   6                    8 (in Bytes)           
     * -----------------------------------------------------------------------------------
     * |	length			| 				EncryptedPreMasterSecret...								
     * | 					|
     * -----------------------------------------------------------------------------------
     * |
     * |						EncryptedPreMasterSecret.... ( depends on length)
     * -----------------------------------------------------------------------------------
     * Reference = [ RFC-2246 -  The TLS Protocol Version 1.0]
     * Length 					= Section 4.3-Vectors
     * EncryptedPreMasterSecret	= Section 7.4.7.1-RSA encrypted premaster secret message 
     */ 
	 
	public int getKeyExchangeLength(){
		return(this.length);
	}
	
	public byte[] getKeyExchangeValue() {
		return this.keyExchangeValue;
	}
	
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n" +" Key Exchange Value = ");
		if(this.length > 0)
			strBuilder.append(Utility.bytesToHex(this.keyExchangeValue));
		else
			strBuilder.append(" ? ");
		return(strBuilder.toString());
	}

	public byte[] getBytes() {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try {
			writeTo(buffer);
		} catch (IOException e) {		
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during method call - getBytes, reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return(buffer.toByteArray());
	}	

	public void setBytes(byte[] handshakeMessageBytes) {
		if(handshakeMessageBytes == null)
			throw new IllegalArgumentException("Invalid handshake message, reason : client key exchange message is null");
		if(handshakeMessageBytes.length < DEFAULT_CLIENT_KEY_EXCHANGE_LENGTH)
			throw new IllegalArgumentException("Invlaid handshake message, reason : incomplete client key excahgne message");
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
		// TODO Auto-generated method stub	
		int byteRead=0;		
			this.length = sourceStream.read();
			if(this.length == -1)
				return -1;			
			this.length = this.length << 8;
			this.length = this.length | (int)(sourceStream.read() & 0xFF);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte[] bufferBytes = new byte[this.length];
			int byteReadLocal = 0 ;
			byteReadLocal = sourceStream.read(bufferBytes);
			if(byteReadLocal < this.length){
				//TODO - throw exception
			}
			buffer.write(bufferBytes,0,byteReadLocal);
			byteRead = byteRead + byteReadLocal;
			
			if(buffer.toByteArray().length < this.length)
				throw new IllegalArgumentException("Illegal EncryptedPreMasterSecret Length. : " + this.length);

			this.keyExchangeValue = new byte[this.length];
			System.arraycopy(buffer.toByteArray(),0,this.keyExchangeValue,0,buffer.toByteArray().length);
		return(byteRead+2);
	}

	public void writeTo(OutputStream out) throws IOException {
		// TODO Auto-generated method stub
		out.write((byte)(this.length >>> 8) & 0xFF );		
		out.write((byte)(this.length) & 0xFF);
		if(this.keyExchangeValue != null)
			out.write(this.keyExchangeValue,0,this.keyExchangeValue.length);
	}
	public Object clone()throws CloneNotSupportedException{		
		ClientKeyExchange clientKeyExchange = null;		
		clientKeyExchange= (ClientKeyExchange)super.clone();
		clientKeyExchange.keyExchangeValue = null;
		return(clientKeyExchange);
	}

	public int getType() {
		return HandshakeMessageConstants.ClientKeyExchange.value;
	}
}
 
