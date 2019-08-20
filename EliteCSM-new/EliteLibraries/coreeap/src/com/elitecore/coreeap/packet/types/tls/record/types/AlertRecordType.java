package com.elitecore.coreeap.packet.types.tls.record.types;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.constants.tls.TLSRecordConstants;
import com.elitecore.coreeap.util.constants.tls.alert.TLSAlertDescConstants;
import com.elitecore.coreeap.util.constants.tls.alert.TLSAlertLevelConstants;

public class AlertRecordType extends BaseTLSRecordType{

	private final static String MODULE = "TLS_ALERT_RECORD";
	private int alertLevel;
	private int alertDescription;
	
	

	public void setBytes(byte[] messageData) {
		if(messageData == null || messageData.length != 2)
			throw new IllegalArgumentException("Incomplete alert record");
		ByteArrayInputStream in = new ByteArrayInputStream(messageData);
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
		int bRead = sourceStream.read();
		if(bRead == -1)
			return(-1);
		setAlertLevel(bRead & 0xFF);
		setAlertDescription(sourceStream.read() & 0xFF);			
		return(2);
	}

	public byte[] getBytes() {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			writeTo(buffer);
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during method call - setBytes, reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}		
		return buffer.toByteArray();
	}
	
	public void writeTo(OutputStream out) throws IOException {
		out.write((byte)getAlertLevel() & 0xFF);
		out.write((byte)getAlertDescription() & 0xFF);
	}
	
	public Object clone()throws CloneNotSupportedException{
		AlertRecordType alertRecordType = (AlertRecordType)super.clone();
		alertRecordType.alertDescription = 0;
		alertRecordType.alertLevel = 0;
		return(alertRecordType);
	}

	public int getAlertDescription() {
		return alertDescription;
	}

	public void setAlertDescription(int alertDescription) {
		if(TLSAlertDescConstants.isValid(alertDescription))
			this.alertDescription = alertDescription;
		else
			throw new IllegalArgumentException("Invalid alert description :" + alertDescription);
	}

	public int getAlertLevel() {
		return alertLevel;
	}

	public void setAlertLevel(int alertLevel) {
		if(TLSAlertLevelConstants.isValid(alertLevel))
			this.alertLevel = alertLevel;
		else
			throw new IllegalArgumentException("Invalid alert level : " + alertLevel);
	}

	public int getType() {
		return TLSRecordConstants.Alert.value;
	}
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n" +"Level = " + TLSAlertLevelConstants.getName(this.alertLevel) + "(" + this.alertLevel + ")");
		strBuilder.append("\n" +"Description = " + TLSAlertDescConstants.getName(this.alertDescription) + "(" + this.alertDescription + ")");
		return(strBuilder.toString());
	}
}
