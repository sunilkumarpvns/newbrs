package com.elitecore.coreradius.commons.util.constants;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum PaddingType {

	@XmlEnumValue("dhcp")
	DHCP("dhcp" , new byte[]{(byte) 0xFF}),	// DHCP Discover packet has padding 0xFF after all DHCP Options, attribute value contains options bytes
	@XmlEnumValue("none")
	NONE("none" , new byte[0]),					// Default
	;
	
	private static Map<String, PaddingType> objectMap;
	static {
		objectMap = new HashMap<String, PaddingType>(2,1);
		for(PaddingType paddingType : values()){
			objectMap.put(paddingType.getType(), paddingType);
		}
	}
	
	private String type;
	private byte[] paddingBytes;

	private PaddingType(String type, byte[] paddingBytes) {
		this.type = type;
		this.paddingBytes = paddingBytes;
	}
	
	public static PaddingType fromName(String type){
		return objectMap.get(type.toLowerCase());
	}

	public String getType() {
		return type;
	}

	public int getLength() {
		return paddingBytes.length;
	}

	public byte[] getPaddingBytes() {
		return paddingBytes;
	}
}