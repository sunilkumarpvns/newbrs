package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.packet.types.tls.TLSException;
import com.elitecore.coreeap.util.constants.tls.HandshakeMessageConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;


public class Certificate implements ITLSHandshakeMessage{
	private static final String MODULE = "CERTIFICATE_MESSAGE";
	private List<byte[]> certificateList;
	private int certificateBytesLength;
	
	public Certificate() {
		//super(ITLSHandshakeMessageConstants.CERTIFICATE_CODE);
		this.certificateList = new ArrayList<byte[]>();
	}
	
	public Certificate(byte[] messageData) throws TLSException {
		this.certificateBytesLength =0;
		this.certificateList = new ArrayList<byte[]>();
		//parseMessage(messageData);
		setBytes(messageData);
	}
	 
//	private void parseMessage(byte[] messageData) throws TLSException {
//		int readLength = 0;
//		this.length = TLSUtility.readint(messageData,readLength,3);		
//		readLength = readLength + 3;
//		//while(readLength+1  < this.length)
//		{
//			byte[] tempcertificate = new byte[TLSUtility.readint(messageData,readLength,3)];
//			readLength = readLength + 3;
//			System.arraycopy(messageData,readLength,tempcertificate,0,tempcertificate.length);
//			readLength = readLength + tempcertificate.length;
//			//if(TLSUtility.verifyCertificate(messageData))
//			{				
//				setNumberOfCertificate(1);
//				setCertificate(tempcertificate);
//			}
//			/*else
//			 {
//			 throw new TLSException("Certificate is not valid");
//			 }	*/
//
//		}
//		if(TLSUtility.verifyCertificate(this.certificate[0]));		
//		
//	}
	 
	public List<byte[]> getCertificateList() {
		return this.certificateList;
	}
	 
	public void setCertificate(List<byte[]> certificateChainList) {
		for (byte[] certificate : certificateChainList) {
			certificateList.add(certificate);
			this.certificateBytesLength = this.certificateBytesLength + certificate.length;
		}
	}
	
	public void resetLength(){
		int certificateTotalLength=0;
		for(int iCounter=0;iCounter < this.certificateList.size();iCounter++)
		{
			certificateTotalLength += 3;
			certificateTotalLength += this.certificateList.get(iCounter).length;			
		}
		this.certificateBytesLength =certificateTotalLength;
	}
	/*public byte[] toBytes()
	{
		byte[] returnBytes = null;		
		int readLength=0;
		if(this.length > 0)
			returnBytes = new byte[this.length + 3];					
		else
			returnBytes = new byte[3];
		
		returnBytes[2] = (byte) this.length;
		returnBytes[1] = (byte) (this.length>>>8);
		returnBytes[0] = (byte) (this.length>>>16);
		readLength = readLength + 3;
		if(this.length >0)
		{
			for(int iCounter=0;iCounter < this.numberOfCertificate;iCounter++)
			{
				int len = this.certificate[iCounter].length;
				returnBytes[readLength + 2] = (byte) len;
				returnBytes[readLength + 1] = (byte) (len>>>8);
				returnBytes[readLength] = (byte) (len>>>16);			
				readLength= readLength + 3;
				System.arraycopy(this.certificate[iCounter],0,returnBytes,readLength,this.certificate[iCounter].length);
				readLength = readLength + this.certificate[iCounter].length;				
			}					
		}
		return(returnBytes);
	}*/
	
	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();		
		strBuilder.append("\n" +" Certificates " + "(" +  this.certificateBytesLength +" bytes )");
		strBuilder.append("\n" +" Number of Certificate = " + this.certificateList.size());
		CertificateFactory cf;

		for(int iCounter=0;iCounter < certificateList.size() ; iCounter ++){
			strBuilder.append("\n" +" Certificate Length = " + this.certificateList.get(iCounter).length);
			strBuilder.append("\n" +" Certificate :");
			try {				
				cf = CertificateFactory.getInstance("X.509");
				InputStream inStream = new ByteArrayInputStream(this.certificateList.get(iCounter));
				java.security.cert.Certificate cert = cf.generateCertificate(inStream);
				X509Certificate x509cert = (X509Certificate) cert;
				strBuilder.append(x509cert.toString());
			} catch (CertificateException e) {
				strBuilder.append(TLSUtility.bytesToHex(this.certificateList.get(iCounter))); 
			}
		}
		strBuilder.append(" ]");
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
		// TODO Auto-generated method stub
		int byteRead=3;		
		List<byte[]> certificateMessages = new ArrayList<byte[]>();
		int len;
		len = sourceStream.read() & 0xFF;
		len = len << 8;
		len = len | (sourceStream.read() & 0xFF);
		len = len << 8;
		len = len | sourceStream.read() & 0xFF;
		int byteReadLocal=0;
		int bRead =0;
		while(byteReadLocal < len){
//			System.out.println("loop start read from certificate" + certificateMessages.size());
			int certificateLength =0 ;			
			bRead = sourceStream.read();
			if(bRead == -1)
				break;
			certificateLength = bRead & 0xFF;			
			certificateLength = certificateLength << 8;
			certificateLength = certificateLength | (sourceStream.read() & 0xFF);
			certificateLength = certificateLength << 8;
			certificateLength = certificateLength | sourceStream.read() & 0xFF;
			byte[] bufferBytes = new byte[certificateLength];
			bRead = sourceStream.read(bufferBytes);
			if(bRead == -1){
				break;
			}else if(bRead < certificateLength){			
				 // TODO - throw exception				
			}
			byteReadLocal += bRead  + 3;			 				
			certificateMessages.add(bufferBytes);			
//			System.out.println("loop end read from certificate" + certificateMessages.size());
		}					
		
		byteRead = byteRead + byteReadLocal;
		this.certificateList = certificateMessages;
		resetLength();
		return(byteRead);
	}

	public void writeTo(OutputStream out) throws IOException {
		// TODO Auto-generated method stub	
		
		out.write((byte)(this.certificateBytesLength>>> 16) & 0xFF);
		out.write((byte)(this.certificateBytesLength>>> 8) & 0xFF);
		out.write((byte)(this.certificateBytesLength) & 0xFF);
		
		if(this.certificateBytesLength >0)
		{
			for(int iCounter=0;iCounter < this.certificateList.size();iCounter++)
			{				
				int len = this.certificateList.get(iCounter).length;
				out.write((byte)(len>>> 16) & 0xFF);
				out.write((byte)(len>>> 8) & 0xFF);
				out.write((byte)(len) & 0xFF);
				out.write(this.certificateList.get(iCounter),0,this.certificateList.get(iCounter).length);												
			}					
		}		
	}
	
	public Object clone() throws CloneNotSupportedException{
		Certificate certificate=null ;		
		certificate = (Certificate)super.clone();
		certificate.certificateList = new ArrayList<byte[]>();
		return(certificate);
	}
	
	public int getType() {
		return HandshakeMessageConstants.Certificate.value;
	}
}
 
