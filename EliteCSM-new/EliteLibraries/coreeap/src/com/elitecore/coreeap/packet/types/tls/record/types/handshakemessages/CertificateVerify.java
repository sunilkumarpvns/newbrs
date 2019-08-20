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

public class CertificateVerify implements ITLSHandshakeMessage{
	/***
	 * 0					1					2					3					 4 (in Bytes)			
	 * -----------------------------------------------------------------------------------
	 * |		Signature bytes					|				Signature		
	 * |			Length						|				  Bytes			
	 * -----------------------------------------------------------------------------------
	 * |											
	 * |							.....										
	 * -----------------------------------------------------------------------------------
	 * 
	 * Reference = [ RFC-2246 -  The TLS Protocol Version 1.0] ( Section 7.4.8 - Certificate Verify)
	 * 
	 */
	
	
	protected static final String MODULE = "CERTIFICATE_VERIFY";
	private static final int SIGNATURE_BYTE_LENGTH = 2;
	private int signatureLength;
	private byte[] signature;
	
	
	
	public CertificateVerify() {
		this.signature = new byte[0]; 
	}
	 
	public CertificateVerify(byte[] messageData) {
		setBytes(messageData);
	}
	
	public int getSignatureLength() {
		return signatureLength;
	}

	protected void setSignatureLength(int signatureLength) {
		this.signatureLength = signatureLength;
	}

	protected void setSignature(byte[] signature) {
		if(signature == null)
			throw new IllegalArgumentException("Invalid value NULL for signature");
		
		this.signature = signature;
	}

	public byte[] getSignature() {
		return this.signature;
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
		this.signatureLength = (sourceStream.read() << 8) + sourceStream.read();
		this.signature = new byte[this.signatureLength];
		sourceStream.read(this.signature);
		return (SIGNATURE_BYTE_LENGTH + this.signatureLength);
	}

	public void writeTo(OutputStream out) throws IOException {
		out.write(signatureLength >>> 8);
		out.write(signatureLength);
		out.write(this.signature,0,this.signature.length);		
	}
	
	public Object clone()throws CloneNotSupportedException{		
		
		CertificateVerify certificateVerify = (CertificateVerify)super.clone();
		certificateVerify.signatureLength = 0;
		certificateVerify.signature = new byte[0];
		return(certificateVerify);
	}

	public int getType() {
		return HandshakeMessageConstants.CertificateVerify.value;
	}
	
	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n Signature Length: " + this.signatureLength);
		strBuilder.append("\n Signature: " + Utility.bytesToHex(signature));
		return(strBuilder.toString());
	}
	
}
 
