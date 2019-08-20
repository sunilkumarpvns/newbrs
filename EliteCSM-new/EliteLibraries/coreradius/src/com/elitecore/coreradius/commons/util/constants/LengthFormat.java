package com.elitecore.coreradius.commons.util.constants;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum LengthFormat {

	//  Radius Attribute [ type (1 byte), length (1 byte), value (n bytes)] 

	@XmlEnumValue("tlv")
	TLV("tlv" , true),			// length value is sum of length of {type , length , value} ( 1 + 1 + n ) bytes
	@XmlEnumValue("value")
	VALUE("value" , false),		// length value is only length of value part ( n ) bytes
	;

	private static Map<String, LengthFormat> objectMap;
	static {
		objectMap = new HashMap<String, LengthFormat>(2,1);
		for(LengthFormat lengthFormat : values()){
			objectMap.put(lengthFormat.getName(), lengthFormat);
		}
	}
	
	private String name;
	private boolean value;

	private LengthFormat(String name , boolean value) {
		this.name = name;
		this.value = value;
	}
	
	public static LengthFormat fromName(String name){
		return objectMap.get(name.toLowerCase());
	}

	public String getName() {
		return name;
	}
	
	public boolean getValue(){
		return value;
	}
}