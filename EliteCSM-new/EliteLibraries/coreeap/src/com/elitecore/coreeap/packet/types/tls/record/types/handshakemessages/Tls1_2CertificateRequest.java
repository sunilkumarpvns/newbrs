package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Tls1_2CertificateRequest extends CertificateRequest {
	
	private int signatureHashAlgoLength;
	private static final int SIGNATURE_ALGORITHM_IDENTIFIER_LENGTH = 1;
	private static final int HASH_ALGORITHM_IDENTIFIER_LENGTH = 1;
	private ArrayList<SignatureAndHashAlgorithm> signatureAndHashAlgorithms = new ArrayList<SignatureAndHashAlgorithm>(4);
	
	
	/***
	 * 0					1					2					3					 4 (in Bytes)			
	 * -----------------------------------------------------------------------------------
	 * | Certificate Type	|	Certificate		|	Certificate		|	Certificate		
	 * | 	  length	 	|	   Type			|	   Type			|	   Type			
	 * -----------------------------------------------------------------------------------
	 * |			Hash And Signature			|		Hash		|	Signature		
	 * |			Algorithm Length			|		Algo1		|	  Algo1			
	 * -----------------------------------------------------------------------------------
	 * |		Hash		|	Signature		|		Hash		|	Signature		
	 * |		Algo2		|	  Algo2			|		Algo3		|	  Algo3			
	 * -----------------------------------------------------------------------------------
	 * |		Hash		|	Signature		|
	 * |		Algo3		|	  Algo3			|				....
	 * ----------------------------------------------------------------------------------- 
	 * |		All Distinguished Names			|			Distinguished Name1											
	 * |				 Length					|				 Length										
	 * -----------------------------------------------------------------------------------
	 * |								Distinguished Name
	 * |									Value ....
	 * -----------------------------------------------------------------------------------
	 * |			Distinguished Name2			|			Distinguished Name
	 * |				 Length					|				Value ....
	 * -----------------------------------------------------------------------------------
	 * |			Distinguished Name3			|			Distinguished Name
	 * |				 Length					|				Value ....
	 * -----------------------------------------------------------------------------------
	 * |			
	 * |
	 * -----------------------------------------------------------------------------------
	 * 
	 * Reference = [ RFC-5246 -  The TLS Protocol Version 1.0] ( Section 7.4.4 - Certificate Request)
	 * 
	 */
	
	
	public Tls1_2CertificateRequest() {
		super();
		signatureHashAlgoLength = 0;
	}
	
	public void setSignatureAndHashAlgorithm(int hash, int signature){
		signatureAndHashAlgorithms.add(new SignatureAndHashAlgorithm(hash, signature));
		signatureHashAlgoLength += SIGNATURE_ALGORITHM_IDENTIFIER_LENGTH + HASH_ALGORITHM_IDENTIFIER_LENGTH;
	}
	
	@Override
	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(super.toString());
		
		strBuilder.append("\n" + " Number of Signature & Hash Algorithm = " + (this.signatureHashAlgoLength/2));
		for(SignatureAndHashAlgorithm signatureAndHashAlgorithm : signatureAndHashAlgorithms){
			strBuilder.append("\n" + "Hash Algorithm = " + signatureAndHashAlgorithm.getHash() +
					" Signature Algorithm = " + signatureAndHashAlgorithm.getSignature());
		}
		
		return(strBuilder.toString());
	}

	@Override
	public int readFrom(InputStream sourceStream) throws IOException {
		
		return (	
				HEADER_BYTES_LENGTH +
				readCertificateTypeDetails(sourceStream) +
				readSignatureAndHashAlgoDetails(sourceStream) +
				readDistinguishedNamesDetails(sourceStream)
		);
	}
	
	private int readSignatureAndHashAlgoDetails(InputStream sourceStream) throws IOException {
		this.signatureHashAlgoLength = sourceStream.read() & 0xFF;
		this.signatureHashAlgoLength = this.signatureHashAlgoLength << 8;
		this.signatureHashAlgoLength = this.signatureHashAlgoLength | (sourceStream.read() & 0xFF);
		int byteRead = 0;
		while(byteRead < signatureHashAlgoLength){
			SignatureAndHashAlgorithm signatureAndHashAlgorithm = new SignatureAndHashAlgorithm((sourceStream.read() & 0xFF), (sourceStream.read() & 0XFF));
			signatureAndHashAlgorithms.add(signatureAndHashAlgorithm);
			byteRead += SIGNATURE_ALGORITHM_IDENTIFIER_LENGTH + HASH_ALGORITHM_IDENTIFIER_LENGTH;
		}
		return byteRead;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{		
		Tls1_2CertificateRequest tls1_2CertificateRequest = (Tls1_2CertificateRequest)super.clone();
		tls1_2CertificateRequest.signatureAndHashAlgorithms = new ArrayList<SignatureAndHashAlgorithm>(4);
		return(tls1_2CertificateRequest);
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		writeCertificateTypeDetails(out);
		writeSignatureAndHashAlgo(out);
		writeDistiguishedNamesDetails(out);
	}
	
	private void writeSignatureAndHashAlgo(OutputStream out) throws IOException {
		out.write(signatureHashAlgoLength >>> 8);
		out.write(signatureHashAlgoLength);
		for(SignatureAndHashAlgorithm signatureAndHashAlgorithm : signatureAndHashAlgorithms){
			out.write((byte) signatureAndHashAlgorithm.getHash() & 0xFF);
			out.write((byte) signatureAndHashAlgorithm.getSignature() & 0xFF);
		}
	}
	
	public int getSignatureHashAlgoLength() {
		return signatureHashAlgoLength;
	}
	
}

class SignatureAndHashAlgorithm {
	private int signature;
	private int hash;
	
	public SignatureAndHashAlgorithm(int hash, int signature) {
		this.signature = signature;
		this.hash = hash;
	}
	
	public int getSignature() {
		return signature;
	}

	public int getHash() {
		return hash;
	}
}
