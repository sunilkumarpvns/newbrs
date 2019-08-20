package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.constants.tls.SignatureAlgorithm;

public class Tls1_2CertificateVerify extends CertificateVerify {
	
	private int hashAlgorithm;
	private int signatureAlgorithm;
	private static final int SIGN_AND_HASH_ALGO_LENGTH = 2;
	
	
	
	/***
	 * 0					1					2					3					 4 (in Bytes)			
	 * -----------------------------------------------------------------------------------
	 * |		Hash		|	  Signature		|			Signature bytes										
	 * |		Algo		|		Algo		|				Length						
	 * -----------------------------------------------------------------------------------
	 * |									Signature		
	 * |				  					Bytes ...			
	 * -----------------------------------------------------------------------------------
	 * 
	 * Reference = [ RFC-5246 -  The TLS Protocol Version 1.0] ( Section 7.4.8 - Certificate Verify)
	 * 
	 */
	
	
	
	
	public Tls1_2CertificateVerify(){
	}
	
	public Tls1_2CertificateVerify(byte[] messageData){
		ByteArrayInputStream message = new ByteArrayInputStream(messageData);
		try {
			readFrom(message);
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during set bytes : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	@Override
	public int readFrom(InputStream sourceStream) throws IOException {
		this.hashAlgorithm = sourceStream.read();
		this.signatureAlgorithm = sourceStream.read();
		return (SIGN_AND_HASH_ALGO_LENGTH + super.readFrom(sourceStream));
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		out.write(hashAlgorithm);
		out.write(signatureAlgorithm);
		out.write(getSignatureLength() >>> 8);
		out.write(getSignatureLength());
		out.write(getSignature());
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		Tls1_2CertificateVerify tls1_2CertificateVerify = (Tls1_2CertificateVerify) super.clone();
		tls1_2CertificateVerify.hashAlgorithm = 0;
		tls1_2CertificateVerify.signatureAlgorithm = 0;
		return tls1_2CertificateVerify;
	}

	public int getHashAlgorithm(){
		return hashAlgorithm;
	}
	
	public int getSignatureAlgorithm(){
		return signatureAlgorithm;
	}
	
	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n Hash Algorithm: " + HashAlgorithm.getStringIdentifier(this.hashAlgorithm));
		strBuilder.append("\n Signature Algorithm: " + SignatureAlgorithm.getStringIdentifier(this.signatureAlgorithm));
		strBuilder.append(super.toString());
		return(strBuilder.toString());
	}
	
}
