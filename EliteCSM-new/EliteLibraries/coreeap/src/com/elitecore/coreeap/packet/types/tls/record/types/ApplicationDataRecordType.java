package com.elitecore.coreeap.packet.types.tls.record.types;

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
import com.elitecore.coreeap.util.constants.tls.TLSRecordConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class ApplicationDataRecordType extends BaseTLSRecordType {
	
	private final static String MODULE = "TLS_APPLICATION_RECORD";
	private byte[] valueBuffer;

	Collection<AVP> avps = new ArrayList<AVP>();
	public byte[] getBytes() {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			writeTo(buffer);
		}catch(Exception e){
			e.printStackTrace();
		}		
		return buffer.toByteArray();
	}	

	public int readFrom(InputStream sourceStream) throws IOException {
		// TODO Auto-generated method stub
		int read =0,buffer = 500;
		byte[] bufferBytes = new byte[buffer];
		while((read = sourceStream.read(bufferBytes)) != -1){
			byte[] tempBytes = new byte[valueBuffer.length + read];
			System.arraycopy(valueBuffer, 0, tempBytes, 0, valueBuffer.length);
			System.arraycopy(bufferBytes, 0, tempBytes, valueBuffer.length, read);
			valueBuffer = tempBytes;
		}
		return valueBuffer.length;
	}

	public void setBytes(byte[] messageData) {
		if (messageData != null) {
			this.valueBuffer = messageData;
		}else {
			valueBuffer = new byte[0];
		}
	}

	public void writeTo(OutputStream out) throws IOException {
		if(valueBuffer == null){
			return;
		}
		out.write(valueBuffer);
	}
	
	public void setAVPs(byte[] applicationData) {
		ByteArrayInputStream in = new ByteArrayInputStream(applicationData);

		try {
			readAVPs(in);
		} catch (IOException e) {		
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during building Application Data Record Type,reason :"+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	public int readAVPs(InputStream sourceStream) throws IOException {
		// TODO Auto-generated method stub
		AVP tempAVP = new AVP();				
		int byteRead =0,bRead=0;		
		
//		System.out.println("[TLSApplication Data]");	
				
		while((bRead = tempAVP.readFrom(sourceStream)) != -1){
			//System.out.println(tempAVP);
			if(bRead == -1)
				break;
			this.avps.add(tempAVP);
			byteRead = byteRead + bRead;
			tempAVP = new AVP();
		}
		return(byteRead);
	}

	public byte[] getAVPBytes() {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			writeAVPs(buffer);
			buffer.close();
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during method call - getBytes, reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}		
		return buffer.toByteArray();
	}

	public void writeAVPs(OutputStream out) throws IOException {
		AVP avpRead = null;
		Iterator<AVP> avpIterator = this.avps.iterator();
		while(avpIterator.hasNext()){
			avpRead = avpIterator.next();
			out.write(avpRead.getBytes());
		}		
	}

	public int getType() {
		return TLSRecordConstants.ApplicationData.value;
	}

	public Collection<AVP> getAVPs() {
		reset();
		setAVPs(getBytes());		
		return avps;
	}

	public void setAvps(Collection<AVP> avps) {
		this.avps = avps;
		this.valueBuffer = getAVPBytes();
	}
	public void setAVP(AVP avp){
		this.avps.add(avp);
		this.valueBuffer = getAVPBytes(); 
	}
	
	public void reset(){
		this.avps.clear();
	}
	public Object clone()throws CloneNotSupportedException{
		ApplicationDataRecordType clonnedObject = (ApplicationDataRecordType)super.clone();
		clonnedObject.avps = new ArrayList<AVP>();
		clonnedObject.valueBuffer = new byte[0];
		return clonnedObject;
	}
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		if(this.avps.size() > 0){
			strBuilder.append("\n" +"Application record contains " + this.avps.size() + " AVPs");		
			Iterator<AVP> avpIterator = this.avps.iterator();
			AVP avpRead = null;
			while(avpIterator.hasNext()){
				avpRead = avpIterator.next();
				strBuilder.append("\n\nAVP:	" +avpRead.toString());
			}				
		}else{
			strBuilder.append("\n" +"Application record contains ");	
			strBuilder.append("\n\nBytes :	" + TLSUtility.bytesToHex(getBytes()));									
		}
		return(strBuilder.toString());
	}
}
