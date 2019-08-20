package com.elitecore.coreeap.packet.types.tls.record;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.dictionary.tls.TLSRecordTypeDictionary;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ContentType;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.packet.types.tls.record.types.ITLSRecordType;
import com.elitecore.coreeap.util.constants.tls.TLSRecordConstants;

public class TLSPlaintext extends BaseTLSRecord {
	private final static String MODULE = "TLSPLAINTEXT";
	private ContentType contentType;
	private ProtocolVersion protocolVersion;
	private int length;

	private Collection<ITLSRecordType> contentMessages;

	public TLSPlaintext(){
		this.contentType = new ContentType();
		this.protocolVersion = ProtocolVersion.TLS1_0;
		this.contentMessages = new ArrayList<ITLSRecordType>();
	}
	public TLSPlaintext(byte[] tlsPlainTextBytes){		
		this.contentType = new ContentType();
		this.protocolVersion = ProtocolVersion.TLS1_0;
		this.contentMessages = new ArrayList<ITLSRecordType>();
		setBytes(tlsPlainTextBytes);
	}	
	public void setBytes(byte[] tlsPlaintextBytes){
		ByteArrayInputStream in = new ByteArrayInputStream(tlsPlaintextBytes);
		try {
			readFrom(in);
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during method call - setBytes ,reason :"+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	public int readFrom(InputStream sourceStream) throws IOException{
		int byteRead=0;
		this.contentType.setType(sourceStream.read());		
		this.protocolVersion = ProtocolVersion.getProtocolVersion(sourceStream.read(), sourceStream.read());
		this.length = sourceStream.read() & 0xFF;
		this.length = this.length << 8;
		this.length = this.length | (sourceStream.read() & 0xFF);
//		System.out.println("Content data Length" + this.length);
		ITLSRecordType tlsRecordType ;
		while(byteRead < this.length ){
			tlsRecordType = TLSRecordTypeDictionary.getInstance().createTLSRecord(this.contentType.getType(), protocolVersion);
			int bRead = tlsRecordType.readFrom(sourceStream);
			if (bRead <= 0){
				break;
			}
			byteRead = byteRead + bRead;
//			System.out.println("Multiple Handshake Message Read :: " + tlsRecordType);
			this.contentMessages.add(tlsRecordType);
		}			
		return(byteRead + 5);
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
			out.write(this.contentType.getType());
			out.write(this.protocolVersion.getMajor());
			out.write(this.protocolVersion.getMinor());
			out.write((byte)(this.length >>> 8) & 0xFF);
			out.write((byte)(this.length) & 0xFF);
			Iterator<ITLSRecordType> allHandshakeMessages = this.contentMessages.iterator();
			ITLSRecordType handshakeMessage;
			while(allHandshakeMessages.hasNext()){
				handshakeMessage = allHandshakeMessages.next();
				out.write(handshakeMessage.getBytes());
			}					
	}

	public Collection<ITLSRecordType> getContent() {
		return this.contentMessages;
	}
	public void setContent(ITLSRecordType content) {
		this.contentMessages.add(content);
	}
	public ContentType getContentType() {
		return contentType;
	}
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}
	public void setProtocolVersion(ProtocolVersion protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
	
	public void refreshHeader(){
		this.length =0 ;
		Iterator<ITLSRecordType> allHandshakeMessages = this.contentMessages.iterator();		
		ITLSRecordType handshakeMessage;
		while(allHandshakeMessages.hasNext()){
			handshakeMessage = allHandshakeMessages.next();
			this.length = this.length + handshakeMessage.getBytes().length;
		}		
	}

	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append("\n" +" TLS Record : " + TLSRecordConstants.getName(this.contentType.getType()));
		strBuilder.append("\n" +" Content Type = " + this.contentType.getType());
		strBuilder.append("\n" +" Protocol Version = " + this.protocolVersion.getMajor()+ "." +this.protocolVersion.getMinor());		
		strBuilder.append("\n" +" Length = " + this.length);

		Iterator<ITLSRecordType> allHandshakeMessages = this.contentMessages.iterator();
		ITLSRecordType handshakeMessage;
		while(allHandshakeMessages.hasNext()){
			handshakeMessage = allHandshakeMessages.next();
			strBuilder.append(handshakeMessage.toString());
		}
		return(strBuilder.toString());
	}
	
	public Object clone()throws CloneNotSupportedException{
		TLSPlaintext tlsPlaintext = (TLSPlaintext)super.clone();
		tlsPlaintext.contentType = new ContentType();
		tlsPlaintext.protocolVersion = ProtocolVersion.TLS1_0;
		tlsPlaintext.contentMessages = new ArrayList<ITLSRecordType>();		
		return(super.clone());
	}
}
