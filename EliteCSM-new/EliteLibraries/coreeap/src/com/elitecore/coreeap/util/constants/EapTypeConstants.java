package com.elitecore.coreeap.util.constants;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum EapTypeConstants implements IEnum{
	IDENTITY(1,"Identity"),
	NOTIFICATION(2,"Notification"),
	NAK(3,"NAK"),
	EXPANDED(254,"Expanded-Type"),
	EXPERIMENTAL(255,"Experimental-Type"),
	NO_ALTERNATIVE(0,"No-Alternative"),
	MD5_CHALLENGE(4,"MD5-Challenge"),
	OTP_CHALLENGE(5,"OTP-Challenge"),
	GTC(6,"GTC"),
	TLS(13,"TLS"),
	SIM(18,"SIM"),
	TTLS(21,"TTLS"),
	AKA(23,"AKA"),
	PEAP(25,"PEAP"),
	MSCHAPv2(26,"MS-CHAPv2"),
	AKA_PRIME(50,"AKA-PRIME");
	public final int typeId;
	public final String name;
	private static final Map<Integer,EapTypeConstants> map;
	private static final Map<String,EapTypeConstants> nameMap;
	public static final EapTypeConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,EapTypeConstants>(14);
		for (EapTypeConstants type : VALUES) {
			map.put(type.typeId, type);
		}
		nameMap = new HashMap<String,EapTypeConstants>(14);
		for (EapTypeConstants type : VALUES) {
			nameMap.put(type.name, type);
		}
	}	

	EapTypeConstants(int id,String name){
		this.typeId= id;
		this.name = name;
	}
	public int getTypeId(){
		return this.typeId;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	public static String getName(int value){
		return map.get(value).name;
	}
	public static int getTypeId(String name){
		return nameMap.get(name).typeId;
	}
	public static EapTypeConstants getEapTypeConstants(String name){
		return nameMap.get(name);
	}
	
	public static EapTypeConstants getEapTypeConstants(int value){
		return map.get(value);
	}
}
