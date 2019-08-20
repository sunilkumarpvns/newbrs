package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.tls.HandshakeMessageConstants;

public class ClientHello implements ITLSHandshakeMessage{		
	private static final String MODULE = "CLIENTHELLO MESSAGE";
	private final int DEFAULT_CLIENT_HELLO_LENGTH = 38;
	
	private ProtocolVersion protocolVersion; 
	private byte[] clientRandom;	  
	
	private int sessionIdLength;
	private byte[] sessionId;
		 
	private int ciphersuiteListLength;
	private List<Integer> ciphersuitesList;
	 
	private int compressionMethodListLength;
	private List<Integer> compressionMethodList;
	
	public ClientHello() {
		//super(ITLSHandshakeMessageConstants.CLIENT_HELLO_CODE);
		ciphersuitesList = new ArrayList<Integer>();		
		compressionMethodList = new ArrayList<Integer>();
		this.protocolVersion = ProtocolVersion.TLS1_0;
	}
	 
	public ClientHello(byte[] messageData) {
		ciphersuitesList = new ArrayList<Integer>();        
		compressionMethodList = new ArrayList<Integer>();
		setBytes(messageData);
	}
	
	/***
	 * 0					2					4					6					 8 (in Bytes)			
	 * -----------------------------------------------------------------------------------
	 * | Protocol  Version	|			Client Random ( 32 bytes).......
	 * |  Major		Minor	|
	 * -----------------------------------------------------------------------------------
	 * |								Client Random...............
	 * |
	 * -----------------------------------------------------------------------------------
	 * |								Client Random...............
	 * |
	 * -----------------------------------------------------------------------------------
	 * |								Client Random...............
	 * |
	 * -----------------------------------------------------------------------------------
	 * | Client Random		|SessionID	|	Session ID (Length defines in SessionIDLength)	 
	 * | 					|Length		|				(suppose SessionID length = 5)
	 * -----------------------------------------------------------------------------------
	 * | CipherSuite List	| First CipherSuite	|Second CipherSuite	|Compression |Compression			
	 * | 	Length(if 4)	|			|		|			|		|Method Len	 | Method
	 * -----------------------------------------------------------------------------------
	 * 
	 * Reference = [ RFC-2246 -  The TLS Protocol Version 1.0]
	 * Protocol Version   = Section 6.2.1-Fragmentation
	 * Client Random	  = Section 7.4.1.2-Client hello
	 * Session ID 		  = Section 7.4.1.2-Client hello
	 * CipherSuite		  = Section 7.4.1.2-Client hello
	 * Compression Method = Section 7.4.1.2-Client hello
	 * SessionID Length,CipherSuiteList Length,Compression Method Length = Section 4.3-Vectors
	 */	
	 
	public void setBytes(byte[] messageData){		
		if(messageData == null)
			throw new IllegalArgumentException("Invalid handshake message, reason : attempted to set null bytes");
		if(messageData.length < DEFAULT_CLIENT_HELLO_LENGTH)
			throw new IllegalArgumentException("Invalid handshake message, reason : incomplete handshake message");

		ByteArrayInputStream in = new ByteArrayInputStream(messageData);	
		
		try {
			readFrom(in);
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during method call - setBytes, reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}				
	}
	
	public int readFrom(InputStream sourceStream) throws IOException{	
			int byteRead=0;
			int majorVersion = sourceStream.read();
			if(majorVersion== -1)
				return -1;
			this.protocolVersion = ProtocolVersion.getProtocolVersion(majorVersion & 0xFF, sourceStream.read() & 0xFF);
			byteRead = byteRead + 2;
			
			this.clientRandom = new byte[32];
			int readBytes = sourceStream.read(this.clientRandom,0,32);
			if(readBytes != 32)
				throw new IllegalArgumentException("Incomplete client hello handshake message,reason :client random not found");
			byteRead = byteRead + 32;
			this.sessionIdLength = sourceStream.read() & 0xFF;
			
			if(this.sessionIdLength > 32)
				throw new IllegalArgumentException("Illegal Session ID Length.<0..32> : " + this.sessionIdLength);
			
			if(this.sessionIdLength != 0){
				this.sessionId = new byte[this.sessionIdLength];
				readBytes =sourceStream.read(this.sessionId, 0, this.sessionIdLength);
				if(readBytes != this.sessionIdLength)
					throw new IllegalArgumentException("Incomplete client hello handshake message,reason : session id not found");
			}
			byteRead = byteRead + sessionIdLength + 1 ;
			this.ciphersuiteListLength = sourceStream.read() & 0xFF;
			this.ciphersuiteListLength = this.ciphersuiteListLength << 8;
			this.ciphersuiteListLength = this.ciphersuiteListLength | (sourceStream.read() &  0xFF);
			
			if(this.ciphersuiteListLength < 2 || this.ciphersuiteListLength > 65535){
				throw new IllegalArgumentException("Illegal CipherSuite List Length.<2..65535> : " + this.ciphersuiteListLength);
			}
			
			byte[] ciphersuites = new byte[this.ciphersuiteListLength];
			readBytes = sourceStream.read(ciphersuites);
			if(readBytes != this.ciphersuiteListLength)
				throw new IllegalArgumentException("Invalid ciphersuite list length : " + this.ciphersuiteListLength + ", expected " + readBytes);
			byteRead = byteRead + (ciphersuiteListLength) + 2 ;
			int ciphersuiteCode,iCounter=0;
			while(iCounter < this.ciphersuiteListLength)
			{
				ciphersuiteCode = ciphersuites[iCounter++] & 0xFF;
				ciphersuiteCode = ciphersuiteCode << 8;
				ciphersuiteCode = ciphersuiteCode | (ciphersuites[iCounter++] & 0xFF);
				addCiphersuite(ciphersuiteCode);						
			}
			
			this.compressionMethodListLength = sourceStream.read() & 0xFF;
			
			if(this.compressionMethodListLength < 1 || this.compressionMethodListLength > 255){
				throw new IllegalArgumentException("Illegal Compression Method List Length.<2..255> : " + this.ciphersuiteListLength);
			}
			
			for(int i = 0; i < compressionMethodListLength; i++){
				this.compressionMethodList.add(new Integer(sourceStream.read() & 0xFF));
			}	
			byteRead = byteRead + compressionMethodListLength + 1 ;			
			
			
			return(byteRead);
	}
	
	public ProtocolVersion getProtocolVersion() {
		return this.protocolVersion;
	}
		
	public byte[] getClientRandom() {
		return this.clientRandom;
	}
	
	public byte[] getSessionId(){
		return this.sessionId;
	}
	 
	public void addCiphersuite(int code) {
		if(code < 0 || code > 65535)
			throw new IllegalArgumentException("CipherSuite Code can not be Negative : " + code); 
			this.ciphersuitesList.add(code);
	}
	
	public List<Integer> getCiphersuiteList(){
		return this.ciphersuitesList;
	}
	 
	public List<Integer> getCompressionMethod() {
		return this.compressionMethodList;
	}	 

	public byte[] getBytes(){
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
	
	public void writeTo(OutputStream out) throws IOException{		
		out.write(this.protocolVersion.getMajor());
		out.write(this.protocolVersion.getMinor());
		out.write(this.clientRandom, 0, this.clientRandom.length);
		out.write(this.sessionIdLength);
		if (this.sessionIdLength > 0) {
			out.write(this.sessionId, 0, this.sessionIdLength);
		}
		out.write((byte)this.ciphersuiteListLength >>> 8);
		out.write((byte)this.ciphersuiteListLength);			
		for (int i=0; i < this.ciphersuiteListLength/2; i++) {
			int  ciphersuite = this.ciphersuitesList.get(i);				
			out.write(ciphersuite >>> 8);
			out.write(ciphersuite & 0xFF);				
		}			
		out.write(this.compressionMethodListLength);
		for (int i = 0; i < compressionMethodListLength; i++) {
			out.write(this.compressionMethodList.get(i) & 0xFF);				
		}
	}

	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n" +" Version = " + this.protocolVersion.getMajor() + "." + this.protocolVersion.getMinor());
		strBuilder.append("\n" +" Random = ");
		if (this.clientRandom != null) {
			strBuilder.append(Utility.bytesToHex(this.clientRandom));
		} else {
			strBuilder.append("?");
		}
		strBuilder.append("\n" +" Session Identifier Length = " + this.sessionIdLength);
		if (this.sessionIdLength > 0) {
			strBuilder.append("\n" +" Session Identifier = "+Utility.bytesToHex(this.sessionId));
		}
		
		strBuilder.append("\n" +" Cipher Suites Length = " + this.ciphersuiteListLength);
		strBuilder.append("\n" +" CipherSuites");		
		
		for (int i = 0; i < ciphersuitesList.size(); i++) {
			int cipherCode = ciphersuitesList.get(i);
			if (CipherSuites.isSupported(cipherCode, protocolVersion)) {
				strBuilder.append("\n" +" Cipher Suite = "	+ CipherSuites.fromCipherCode(cipherCode) + "("	+ cipherCode + ") is " + ((CipherSuites.isBlockCipher(cipherCode))?"BlockCipherSuite":"StreamCipherSuite")); 
			} else {
				strBuilder.append("\n" +" Cipher Suite = "	+ cipherCode + " is not supported ");
			}
		}
		strBuilder.append("\n" +" Compression Methods Length= " + this.compressionMethodListLength);
		strBuilder.append("\n" +" Compression method = " + this.compressionMethodList);
		return strBuilder.toString();
	}

	public int getCiphersuiteListLength() {
		return ciphersuiteListLength;
	}

	public void setCiphersuiteListLength(int ciphersuiteListLength) {
		this.ciphersuiteListLength = ciphersuiteListLength;
	}

	public int getCompressionMethodListLength() {
		return compressionMethodListLength;
	}

	public void setCompressionMethodListLength(int compressionMethodListLength) {
		this.compressionMethodListLength = compressionMethodListLength;
	}

	public int getSessionIdLength() {
		return sessionIdLength;
	}

	public void setSessionIdLength(int sessionIdLength) {
		this.sessionIdLength = sessionIdLength;
	}

	public Collection<Integer> getCiphersuitesList() {
		return ciphersuitesList;
	}

	public void setClientRandom(byte[] clientRandom) {
		this.clientRandom = clientRandom;
	}

	public void addCompressionMethod(int compressionMethod) {
		this.compressionMethodList.add(new Integer(compressionMethod));
	}

	public void setProtocolVersion(ProtocolVersion protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public void setSessionId(byte[] sessionId) {
		this.sessionId = sessionId;
	}
	
	public void refreshPacketHeader(){
		
	}

	public Object clone() throws CloneNotSupportedException{		
		ClientHello clientHello = (ClientHello)super.clone();
		clientHello.ciphersuitesList = new ArrayList<Integer>();
		clientHello.compressionMethodList = new ArrayList<Integer>();
		clientHello .protocolVersion = ProtocolVersion.TLS1_0;
		clientHello.clientRandom = null;
		clientHello.sessionId = null;
		return(clientHello);
	}

	public int getType() {
		return HandshakeMessageConstants.ClientHello.value;
	}
}
