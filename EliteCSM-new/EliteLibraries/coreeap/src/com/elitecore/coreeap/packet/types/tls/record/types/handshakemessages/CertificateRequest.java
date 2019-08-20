package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.tls.HandshakeMessageConstants;

public class CertificateRequest implements ITLSHandshakeMessage{
	protected static final String MODULE = "CERTIFICATE_REQUEST_MESSAGE";
	protected static final int HEADER_BYTES_LENGTH = 5;
	protected static final int DISTINGUISHED_NAME_LENGTH_BYTES = 2;
	private int[] certificateType;
	private int numberOfTypesOfCertificate;
	
	private int lengthOfDistiguishedNames; 
	private byte[][] distinguishedName=null;
	private int counterForDN;
	private int numberOfDN;
	
	
	/***
	 * 0					1					2					3					 4 (in Bytes)			
	 * -----------------------------------------------------------------------------------
	 * | Certificate Type	|	Certificate		|	Certificate		|	Certificate		
	 * | 	  length	 	|	   Type			|	   Type			|	   Type			
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
	 * 
	 * Reference = [ RFC-2246 -  The TLS Protocol Version 1.0] ( Section 7.4.4 - Certificate Request)
	 * 
	 */
	
	
	 
	public CertificateRequest() {
		this.certificateType = new int[4];
		this.counterForDN = 0;
		this.numberOfDN = 0;
	}	 	
	 
	public void setDistinguishedName(byte[] distinguishedName) {
		this.distinguishedName[this.counterForDN]  = new byte[distinguishedName.length];
		System.arraycopy(distinguishedName,0,this.distinguishedName[this.counterForDN],0,distinguishedName.length);
		this.lengthOfDistiguishedNames = this.lengthOfDistiguishedNames + distinguishedName.length + DISTINGUISHED_NAME_LENGTH_BYTES;
		this.counterForDN++;
	}		
	public void setRequestedCertificateType(int type)
	{
		if(this.numberOfTypesOfCertificate > 3){
			this.numberOfTypesOfCertificate = 0;
		}
		certificateType[this.numberOfTypesOfCertificate] = type;
		this.numberOfTypesOfCertificate++;
	}	

	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n" +" Certificate types count = " + this.numberOfTypesOfCertificate);
		for(int iCounter=0;iCounter < this.numberOfTypesOfCertificate;iCounter++){
			strBuilder.append("\n" +" Certificate Type = " +  this.certificateType[iCounter]);
		}
		strBuilder.append("\n" +" Distinguished Names Length = " + this.lengthOfDistiguishedNames);
		strBuilder.append("\n" +" Number of Distinguished Names = " + this.numberOfDN);
		
		for(int iCounter=0;iCounter < this.numberOfDN ; iCounter ++){
			strBuilder.append("\n" +" Distinguished Name Length = " + this.distinguishedName[iCounter].length);
			strBuilder.append("\n" +" Distinguished Name = " + Utility.bytesToHex(this.distinguishedName[iCounter]));
		}
		return(strBuilder.toString());
	}

	public byte[] getBytes() {
		// TODO Auto-generated method stub
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			writeTo(buffer);
		} catch (IOException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during get bytes : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return(buffer.toByteArray());
	}


	public void setBytes(byte[] handshakeMessageBytes) {
		// TODO Auto-generated method stub
		ByteArrayInputStream in = new ByteArrayInputStream(handshakeMessageBytes);
		
		try {
			readFrom(in);
		} catch (IOException e) {		
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during set bytes : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}

	public int readFrom(InputStream sourceStream) throws IOException {
		return (	
				HEADER_BYTES_LENGTH +
				readCertificateTypeDetails(sourceStream) +
				readDistinguishedNamesDetails(sourceStream)
		);
	}
	
	protected int readCertificateTypeDetails(InputStream sourceStream) throws IOException {
		this.numberOfTypesOfCertificate = sourceStream.read();
		if(this.numberOfTypesOfCertificate > 0){
			for(int iCounter = 0; iCounter < this.numberOfTypesOfCertificate; iCounter++){
				this.certificateType[iCounter] = sourceStream.read();
			}
		}
		return this.numberOfTypesOfCertificate;
	}
	
	protected int readDistinguishedNamesDetails(InputStream sourceStream) throws IOException {
		this.lengthOfDistiguishedNames = sourceStream.read() & 0xFF;
		this.lengthOfDistiguishedNames = this.lengthOfDistiguishedNames << 8;
		this.lengthOfDistiguishedNames = this.lengthOfDistiguishedNames | (sourceStream.read() &  0xFF);
		
		Collection<byte[]> distinguishedNames = new ArrayList<byte[]>();
		int bytesRead= 0;
		int bRead = 0;
		while(bytesRead < lengthOfDistiguishedNames){
//			System.out.println("loop start read from certificate" + certificateMessages.size());
			int distinguishedNameLength =0 ;			
			bRead = sourceStream.read();
			if(bRead == -1)
				break;
			distinguishedNameLength = distinguishedNameLength << 8;
			distinguishedNameLength = distinguishedNameLength | (sourceStream.read() &  0xFF);
			byte[] bufferBytes = new byte[distinguishedNameLength];
			bRead = sourceStream.read(bufferBytes);
			if(bRead == -1){
				break;
			}else if(bRead < distinguishedNameLength){			
				 // TODO - throw exception				
			}
			bytesRead += bRead  + 3;			 				
			distinguishedNames.add(bufferBytes);			
		}
		setNumberOfDN(distinguishedNames.size());		
		for (byte[] distinguishedName : distinguishedNames) {
			setDistinguishedName(distinguishedName);
		}
		return bytesRead;
	}

	public void writeTo(OutputStream out) throws IOException {
		writeCertificateTypeDetails(out);
		writeDistiguishedNamesDetails(out);
	}
	
	protected void writeCertificateTypeDetails(OutputStream out) throws IOException {
		out.write((byte)this.numberOfTypesOfCertificate);

		if(this.numberOfTypesOfCertificate > 0){
			for(int iCounter = 0; iCounter < this.numberOfTypesOfCertificate;iCounter++){
				out.write((byte)this.certificateType[iCounter]);				
			}
		}
	}
	
	protected void writeDistiguishedNamesDetails(OutputStream out) throws IOException {
		if(this.lengthOfDistiguishedNames > 0){
			out.write((byte)((this.lengthOfDistiguishedNames >>> 8) & 0xFF));
			out.write((byte)((this.lengthOfDistiguishedNames) & 0xFF));
			for(int iCounter=0;iCounter < this.numberOfDN;iCounter++)
			{				
				int len = this.distinguishedName[iCounter].length;
				out.write((byte)(len>>> 8) & 0xFF);
				out.write((byte)(len) & 0xFF);
				out.write(this.distinguishedName[iCounter],0,len);												
			}	
		}
	}
	
	public Object clone() throws CloneNotSupportedException{		
		CertificateRequest certificateRequest = null;
		
		certificateRequest= (CertificateRequest)super.clone();
		certificateRequest.certificateType = new int[4];
		certificateRequest.distinguishedName = null;
		return(certificateRequest);
	}
	public int getType() {
		// TODO Auto-generated method stub
		return HandshakeMessageConstants.CertificateRequest.value;		
	}

	public int getNumberOfDN() {
		return numberOfDN;
	}
	
	public int getNumberOfTypesOfCertificate() {
		return numberOfTypesOfCertificate;
	}
	
	public int getLengthOfDistiguishedNames() {
		return lengthOfDistiguishedNames;
	}

	public void setNumberOfDN(int numberOfDN) {
		this.numberOfDN = numberOfDN;
		this.distinguishedName = new byte[this.numberOfDN][0];
		this.counterForDN = 0;
	}

}
 
