/**
 * 
 */
package com.elitecore.test.dependecy.diameter.packet.avps.threegpp;

import java.util.HashMap;
import java.util.Map;

/**
 *  3GPP Location Fields
 *  
 *  0 CGI
 *  1 SAI
 *  2 RAI
 * 	3-127 Spare for future use
 *	128 TAI
 *	129 ECGI
 *	130 TAI and ECGI
 *	131-255 Spare for future use
 *
 * @author jatin
 */
public enum TGPPLocationField {
	CGI(0),
	SAI(1),
	RAI(2),
	TAI(128),
	ECGI(129),
	TAI_AND_ECGI(130),
	UNKNOWN(-1);;
	
	
	public final int type;
	private static Map<Integer, TGPPLocationField> fieldsMap;
	private static TGPPLocationField[] values = values();
	
	static{
		fieldsMap = new HashMap<Integer, TGPPLocationField>();
		for (TGPPLocationField field : values) {
			fieldsMap.put(field.type, field);
		}
	}
	
	TGPPLocationField(int type){
		this.type = type;
	}
	public static TGPPLocationField getField(int type){
		TGPPLocationField tgppLocationField =	fieldsMap.get(type);
		if(tgppLocationField != null){
			return tgppLocationField;
		}
		return TGPPLocationField.UNKNOWN;
		
	}
	public static String fieldName(int type){
		TGPPLocationField tgppLocationField =	fieldsMap.get(type);
		if(tgppLocationField != null){
			return tgppLocationField.name();
		}
		return "UNKNOWN ( "+String.valueOf(type) + " )";
	}
}
