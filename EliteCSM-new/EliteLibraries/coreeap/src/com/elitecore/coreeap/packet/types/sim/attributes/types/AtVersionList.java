package com.elitecore.coreeap.packet.types.sim.attributes.types;

import java.util.ArrayList;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtVersionList extends BaseSIMAttribute {

	public AtVersionList() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_VERSION_LIST.Id);		
	}	
	
	public int getActualVersionListLength(){
		byte[] buffer = this.getReservedBytes();
		int length = buffer[0] & 0xFF;
		length = length << 8;
		length = length | (int)(buffer[1] & 0xFF);
		return length;
	}
	
	public ArrayList<Integer> getSupportedVersionList(){
		int actualVersionListLength = getActualVersionListLength();
		byte[] buffer = this.getValueBytes();
		int readLength = 0;
		ArrayList<Integer> versionList = new ArrayList<Integer>();
		while(readLength < actualVersionListLength){
			int version = buffer[readLength];
			version = version << 8;
			version = version | (int)(buffer[readLength+1]& 0xFF);
			versionList.add(version);
			readLength +=  2;
		}
		return versionList;
	}
	
	public void setSupportedVersionList(byte[] supportedVersionList){
		int length = supportedVersionList.length;
		setReservedBytes(new byte[]{(byte)(length>>8),(byte)length});
		setValueBytes(supportedVersionList);
	}

	@Override
	public String getStringValue() {		
		StringBuilder returnString = new StringBuilder();
		returnString.append("[ Actual Version List Length = " + getActualVersionListLength());
		returnString.append(", Supported Version List = " + getSupportedVersionList() + " ] ");
		return returnString.toString();
	}
}
