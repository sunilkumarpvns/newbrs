package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.tls.HandshakeMessageConstants;

public class ServerHello implements ITLSHandshakeMessage{ 
	
	private static final String MODULE = "SERVERHELLO MESSAGE";
	private static final int DEFAULT_SESSIONID_LENGTH = 32;
	private static final int DEFAULT_SERVER_RANDOM_LENGTH = 32;
	private ProtocolVersion protocolVersion;
	private byte[] serverRandom;	 
	
	private int sessionIdLength;
	private byte[] sessionId;
	
	
	private int ciphersuiteCode;	
	
	private int compressionMethod;

    /***
     * 0                    2                   4                   6                    8 (in Bytes)           
     * -----------------------------------------------------------------------------------
     * | Protocol  Version  |           Server Random ( 32 bytes).......
     * |  Major     Minor   |
     * -----------------------------------------------------------------------------------
     * |                                Server Random...............
     * |
     * -----------------------------------------------------------------------------------
     * |                                Server Random...............
     * |
     * -----------------------------------------------------------------------------------
     * |                                Server Random...............
     * |
     * -----------------------------------------------------------------------------------
     * | Server Random      |SessionID  |   Session ID (Length defines in SessionIDLength)   
     * |                    |Length     |               (suppose SessionID length = 6)
     * -----------------------------------------------------------------------------------
     * |        |  First CipherSuite |Compression|          
     * |        |                    | Method    |
     * -------------------------------------------
     * 
     * Reference = [ RFC-2246 -  The TLS Protocol Version 1.0]
     * Protocol Version   = Section 6.2.1-Fragmentation
     * Client Random      = Section 7.4.1.3-Server hello
     * SessionID Length   = Section 4.3-Vectors
     * Session ID         = Section 7.4.1.3-Server hello
     * CipherSuite        = Section 7.4.1.3-Server hello
     * Compression Method = Section 7.4.1.3-Server hello  
     */
    
	
	public ServerHello() {
		//super(ITLSHandshakeMessageConstants.SERVER_HELLO_CODE);
		this.protocolVersion = ProtocolVersion.TLS1_0;
		this.serverRandom = new byte[DEFAULT_SERVER_RANDOM_LENGTH];
		this.sessionId = null;
	}

	public void setProtocolVersion(ProtocolVersion protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
    
    public int getProtocolVersionMajor() {
        return this.protocolVersion.getMajor();
    }
    
    public int getProtocolVersionMinor() {
        return this.protocolVersion.getMinor();
    }
	 
	public void setServerRandom(byte[] serverRandom) {
		if(serverRandom == null ){
			throw new IllegalArgumentException("Server Random is null.");
		}
		else if(serverRandom.length == 32){
            this.serverRandom = new byte[serverRandom.length];
            System.arraycopy(serverRandom, 0, this.serverRandom, 0, serverRandom.length);
        }
        else{
            throw new IllegalArgumentException("Invalid Server Random.");
        }
	} 
    
    public byte[] getServerRandom(){
        return(this.serverRandom);
    }
    
    public void setSessionIdLength(int sessionIdLength){
    	if(sessionIdLength >= 0 && sessionIdLength <=32){
        this.sessionIdLength = sessionIdLength;
    	}
    	else{
    		throw new IllegalArgumentException("Invalid Session ID Length.");
    	}
    }
    
    public int getSessionIdLength(){
        return(this.sessionIdLength);
    }
    
	public void setSessionId(byte[] sessionId) {
		if(sessionId == null ){
			throw new IllegalArgumentException("Session Id is null.");
		}
		else if(serverRandom.length == 32){
			this.sessionId = sessionId;	
		}
		else {
			throw new IllegalArgumentException("Invalid Session Id");
		}
		
	}		
	
	public byte[] getSessionId(){
		return(this.sessionId);
	}
	
	public void setCiphersuite(int ciphersuite) {
		if(ciphersuite >=0 && ciphersuite <= 65535)
			this.ciphersuiteCode = ciphersuite;
		else
			throw new IllegalArgumentException("Invalid Ciphersuite.");
	}	
	
	public int getCiphersuite(){
		return(this.ciphersuiteCode);
	}
	
	public void setCompressionMethod(int compressionMethod) {
		if(compressionMethod >=0 && compressionMethod <=255){
			this.compressionMethod = compressionMethod;
		}
		else{
			throw new IllegalArgumentException("Invalid Compression Method");
		}
	}
	
	public int getCompressionMethod() {
		return this.compressionMethod;
	}	
	
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n" +" Version = " + this.protocolVersion.getMajor() + "." + this.protocolVersion.getMinor());
		strBuilder.append("\n" +" Random = ");
		if(this.serverRandom != null)
			strBuilder.append(Utility.bytesToHex(this.serverRandom));
		else
			strBuilder.append("?");
		strBuilder.append("\n" +" Session Identifier Length = " + this.sessionIdLength);
		
		if(this.sessionId != null)
			strBuilder.append("\n" +" Session Identifier = "+Utility.bytesToHex(this.sessionId));			
		else
			strBuilder.append("\n" +" Session Identifier = ?");
		if (CipherSuites.isSupported(ciphersuiteCode, protocolVersion)) {
			strBuilder.append("\n" +" Cipher Suite = "+ CipherSuites.fromCipherCode(ciphersuiteCode) + "("+ ciphersuiteCode + ") is " + ((CipherSuites.isBlockCipher(ciphersuiteCode))?"BlockCipherSuite":"StreamCipherSuite"));
		}				
		strBuilder.append("\n" +" Compression method = " + this.compressionMethod);			
		return strBuilder.toString();

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
		return buffer.toByteArray();
	}
	public void setBytes(byte[] handshakeMessageBytes) {
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
		// TODO Auto-generated catch block
		return(0);
	}
	public void writeTo(OutputStream out) throws IOException {
		out.write((byte)this.protocolVersion.getMajor());
		out.write((byte)this.protocolVersion.getMinor());		
		
		out.write(this.serverRandom,0,DEFAULT_SERVER_RANDOM_LENGTH);	
		
		out.write((byte)this.sessionIdLength);			
		if(this.sessionIdLength >0)
			out.write(this.sessionId,0,DEFAULT_SESSIONID_LENGTH);
		
		out.write((byte)this.ciphersuiteCode >>> 8);
		out.write((byte)this.ciphersuiteCode);
				out.write((byte)this.compressionMethod);					
	}
	public Object clone()throws CloneNotSupportedException{		
		ServerHello serverHello = null;
		
		serverHello = (ServerHello)super.clone();
		serverHello.protocolVersion = ProtocolVersion.TLS1_0;
		serverHello.serverRandom = new byte[DEFAULT_SERVER_RANDOM_LENGTH];
		serverHello.sessionId = null;
		return(serverHello);
	}
	public int getType() {
		return HandshakeMessageConstants.ServerHello.value;
	}

} 
