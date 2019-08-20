package com.elitecore.aaa.core.data;

import java.util.HashMap;
import java.util.Map;
public enum ClientTypeConstant {
	NAS(1,"NAS",false),
	PROXY(2,"Proxy",true),
	PORTAL(3,"Portal",true),
	WIMAX_PORTAL(4,"WiMAX Portal",true),
	THREE_GPP2_HA(5,"3GPP2-HA",true),
	WIMAX_HA(6,"WiMAX-HA",true),
	WIMAX_DHCP(7,"WiMAX-DHCP",true),
	ASN(8,"ASN",true),
	GGSN(9,"GGSN",true),
	PDSN(10,"PDSN",true),
	CISCO_ISG(11,"Cisco-ISG",true),
	CISCO_SSG(12,"Cisco-SSG",true),
	CISCO_LAC(13,"Cisco-LAC",true),
	CISCO_LNS(14,"Cisco-LNS",true),
	A12_ANAAA(15,"A12-ANAAA",true),
	RADIUS1(16,"RADIUS1",false),
	RADIUS2(17,"RADIUS2",false),
	RADIUS3(18,"RADIUS3",false),
	SOFTSWITCH(19,"Softswitch",true),
	VOIP(20,"VoIP",true),
	VOICE(21,"Voice",true),
	ATM_VC(22,"ATM-VC",true),
	PPPoA(23,"PPPoA",true),
	PPPoE(24,"PPPoE",true),
	PPPoX(25,"PPPoX",true),
	DOCSIS(26,"DOCSIS",true),
	WAG(27,"WAG",true),
	CUSTOM1(64,"Custom1",false),
	CUSTOM2(65,"Custom2",false),
	CUSTOM3(66,"Custom3",false),
	CUSTOM4(67,"Custom4",false),
	CUSTOM5(68,"Custom5",false),
	CUSTOM6(69,"Custom6",false),
	CUSTOM7(70,"Custom7",false),
	CUSTOM8(71,"Custom8",false),
	CUSTOM9(72,"Custom9",false),
	CUSTOM10(73,"Custom10",false);
	
	public final int typeId;
	public final String clientType;
	public boolean licenseRequired;
	private static final Map<Integer,ClientTypeConstant> map;
	private static final Map<String,ClientTypeConstant> nameMap;

	public static final ClientTypeConstant[] VALUES = values();
	static {
		map = new HashMap<Integer,ClientTypeConstant>(36);
		for (ClientTypeConstant type : VALUES) {
			map.put(type.typeId, type);
		}
		nameMap = new HashMap<String,ClientTypeConstant>(36);
		for (ClientTypeConstant type : VALUES) {
			nameMap.put(type.clientType, type);
		}
	}	

	ClientTypeConstant(int id,String name,boolean licenseRequired){
		this.typeId= id;
		this.clientType = name;
		this.licenseRequired=licenseRequired;
	}
	public int getTypeId(){
		return this.typeId;
	}

	public static String getClientType(int value){
		if(map.get(value)!=null)
			return map.get(value).clientType;
		else
			return "";
	}
	
	public static int getTypeId(String name){
		if(nameMap.get(name)!=null)
			return nameMap.get(name).typeId;
		else
			return -1;
	}
	
	public static boolean isLicenseRequired(int value){
		if(map.get(value)!=null)
			return map.get(value).licenseRequired;
		else 
			return true;
	}
}
