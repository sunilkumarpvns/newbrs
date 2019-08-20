package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.dictionary.tls.TLSHandshakeMessageDictionary;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.packet.types.tls.record.types.BaseTLSRecordType;
import com.elitecore.coreeap.util.constants.tls.HandshakeMessageConstants;
import com.elitecore.coreeap.util.constants.tls.TLSRecordConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

/**
 * 	NOTE:	
 * 		It is required to call {@link #setProtocolVersion(ProtocolVersion)} BEFORE calling  {@link  #setBytes(byte[])} and {@link  #readFrom(InputStream)}
 */
public class HandshakeMessageRecordType extends BaseTLSRecordType{
	private final static String MODULE = "TLS_HANDSHAKE_RECORD";
	private final static int HANDSHAKE_MESSAGE_MAX_LENGTH =16777215;
	private int handshakeMessageType;	 
	private int handshakeMessagelength;
	private ITLSHandshakeMessage handshakeMessage;
	private int extensionLength;
	private byte[] exntensionBytes;
	
	public HandshakeMessageRecordType() {		
	}
	
	public HandshakeMessageRecordType(int messageType) {
		this.handshakeMessageType = messageType;
	}
	
	public HandshakeMessageRecordType(byte[] handshakeMessageData, ProtocolVersion protocolVersion){
		setProtocolVersion(protocolVersion);
		setBytes(handshakeMessageData);
	}
	
	/**
	 * 	NOTE:	
	 * 		It is required to call {@link #setProtocolVersion(ProtocolVersion)} BEFORE calling this method
	 */
	public void setBytes(byte[] handshakeMessageData){
		if(handshakeMessageData==null)
			throw new IllegalArgumentException("Invalid handshake message, reason : attempted to set null bytes");
		ByteArrayInputStream in = new ByteArrayInputStream(handshakeMessageData);

		try {
			readFrom(in);
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during call of set bytes method,reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	/**
	 * 	NOTE:	
	 * 		It is required to call {@link #setProtocolVersion(ProtocolVersion)} BEFORE calling this method
	 */
	public int readFrom(InputStream sourceStream) throws IOException{
		int byteRead=4;
		this.handshakeMessageType = sourceStream.read();
		if (this.handshakeMessageType == -1)
			return -1;
		
		this.handshakeMessagelength = sourceStream.read() & 0xFF;
		this.handshakeMessagelength = this.handshakeMessagelength << 8;
		this.handshakeMessagelength = this.handshakeMessagelength | ( sourceStream.read() & 0xFF);
		this.handshakeMessagelength = this.handshakeMessagelength << 8;
		this.handshakeMessagelength = this.handshakeMessagelength | ( sourceStream.read() & 0xFF);
		
		this.handshakeMessage = TLSHandshakeMessageDictionary.getInstance().createHandshakeMessage(this.handshakeMessageType, getProtocolVersion());
		
		if(this.handshakeMessage != null){
			int bRead = this.handshakeMessage.readFrom(sourceStream);
			if(this.handshakeMessagelength-bRead > 0){
				this.extensionLength = sourceStream.read() & 0xFF;
				this.extensionLength = this.extensionLength << 8;
				this.extensionLength= this.extensionLength | ( sourceStream.read() & 0xFF);
				if(this.extensionLength > 0){
					this.exntensionBytes = new byte[this.extensionLength];
					sourceStream.read(this.exntensionBytes);
				}
				bRead = bRead + 1 + this.extensionLength; 
			}
			byteRead = byteRead +  bRead;
		}
		return(byteRead);
	}
	
	public byte[] getBytes(){
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try {
			writeTo(buffer);
			buffer.close();
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during call of getBytes method,reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		return buffer.toByteArray();
	}

	public void writeTo(OutputStream out) throws IOException{		
			out.write(this.handshakeMessageType);
			out.write((byte)(this.handshakeMessagelength >>> 16) & 0xFF);
			out.write((byte)(this.handshakeMessagelength >>> 8 )& 0xFF);
			out.write((byte)(this.handshakeMessagelength)& 0xFF);
			if(this.handshakeMessage != null){								
				out.write(this.handshakeMessage.getBytes());		
				int length = this.handshakeMessage.getBytes().length;
				if(this.handshakeMessagelength - length > 0){
					out.write((byte)(this.extensionLength >>> 8 )& 0xFF);
					out.write((byte)(this.extensionLength)& 0xFF);					
					if(this.extensionLength > 0){
						out.write(this.exntensionBytes);
	}
				}
			}
	}
		
	public int getHandshakeMessageType() {
		return handshakeMessageType;
	}
	public void setHandshakeMessageType(int handshakeMessageType) {
		if(HandshakeMessageConstants.isValid(handshakeMessageType))
			this.handshakeMessageType = handshakeMessageType;
		else
			throw new IllegalArgumentException("Invalid handshake message : " + handshakeMessageType);
	}
	public int getHandshakeMessagelength() {
		return handshakeMessagelength;
	}
	public void setHandshakeMessagelength(int length) {
		if(length < 0 || length > HANDSHAKE_MESSAGE_MAX_LENGTH)
			throw new IllegalArgumentException("Invalid handshake message length : " + length);
		this.handshakeMessagelength = length;
	}

	public ITLSHandshakeMessage getHandshakeMessage() {
		return handshakeMessage;
	}

	public void setHandshakeMessage(ITLSHandshakeMessage handshakeMessage) {
		this.handshakeMessage = handshakeMessage;
	}
		
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n" +" Handshake Protocol : " + HandshakeMessageConstants.getName(this.handshakeMessageType));
		strBuilder.append("\n" +" Handshake Type = " + this.handshakeMessageType);
		strBuilder.append("\n" +" Length = " + this.handshakeMessagelength);			
		if(this.handshakeMessage != null){
			strBuilder.append(this.handshakeMessage.toString());
		}
		if(this.exntensionBytes != null){
			strBuilder.append("\n" +" Extension Length = " + this.extensionLength);
			strBuilder.append("\n" +" Extension Bytes = " + TLSUtility.bytesToHex(this.exntensionBytes));
		}
		return(strBuilder.toString());
	}
	
	public void refreshHeader(){
		this.handshakeMessageType = this.handshakeMessage.getType();
		this.handshakeMessagelength = this.handshakeMessage.getBytes().length;
	}
	
	public Object clone()throws CloneNotSupportedException{
		HandshakeMessageRecordType handshakeMessageRecordType = (HandshakeMessageRecordType)super.clone();
		handshakeMessageRecordType.handshakeMessage = null;
		handshakeMessageRecordType.handshakeMessagelength = 0;
		handshakeMessageRecordType.handshakeMessageType = 0;
		return(handshakeMessageRecordType);
	}

	public int getType() {
		return TLSRecordConstants.Handshake.value;
	}

}
