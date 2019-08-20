package com.elitecore.coreeap.util.constants.sim.attribute;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum SIMAttributeTypeConstants implements IEnum {
	AT_RAND(1,"AT_RAND",false,false),
	AT_AUTN(2,"AT_AUTN",false,false),
	AT_RES(3,"AT_RES",false,false),
	AT_AUTS(4,"AT_AUTS",false,false),
	AT_PADDING(6,"AT_PADDING",true,false),
	AT_NONCE_MT(7,"AT_NONCE_MT",false,false),
	AT_PERMANENT_ID_REQ(10,"AT_PERMANENT_ID_REQ",false,false),
	AT_MAC(11,"AT_MAC",false,false),
	AT_NOTIFICATION(12,"AT_NOTIFICATION",false,false),
	AT_ANY_ID_REQ(13,"AT_ANY_ID_REQ",false,false),
	AT_IDENTITY(14,"AT_IDENTITY",false,false),
	AT_VERSION_LIST(15,"AT_VERSION_LIST",false,false),
	AT_SELECTED_VERSION(16,"AT_SELECTED_VERSION",false,false),
	AT_FULLAUTH_ID_REQ(17,"AT_FULLAUTH_ID_REQ",false,false),
	AT_COUNTER(19,"AT_COUNTER",true,false),
	AT_COUNTER_TOO_SMALL(20,"AT_COUNTER_TOO_SMALL",true,false),
	AT_NONCE_S(21,"AT_NONCE_S",true,false),
	AT_CLIENT_ERROR_CODE(22,"AT_CLIENT_ERROR_CODE",false,false),
	AT_IV(129,"AT_IV",false,true),
	AT_ENCR_DATA(130,"AT_ENCR_DATA",false,true),
	AT_NEXT_PSEUDONYM(132,"AT_NEXT_PSEUDONYM",true,true),
	AT_NEXT_REAUTH_ID(133,"AT_NEXT_REAUTH_ID",true,true),
	AT_CHECKCODE(134,"AT_CHECKCODE",false,false),
	AT_RESULT_IND(135,"AT_RESULT_IND",false,true),
	
	/*
	 * Attributes for AKA'
	 */
	AT_KDF_INPUT(23,"AT_KDF_INPUT",false,false),
	AT_KDF(24,"AT_KDF",false,false),
	AT_BIDDING(136, "AT_BIDDING", false,true);
	
	public final int Id;
	public final String name;
	public boolean encr ;
	public boolean skip ;

	private static final Map<Integer,SIMAttributeTypeConstants> map;
	private static final Map<String,SIMAttributeTypeConstants> nameMap;
	public static final SIMAttributeTypeConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,SIMAttributeTypeConstants>();
		for (SIMAttributeTypeConstants type : VALUES) {
			map.put(type.Id, type);
		}
		nameMap = new HashMap<String,SIMAttributeTypeConstants>();
		for (SIMAttributeTypeConstants type : VALUES) {
			nameMap.put(type.name, type);
		}
	}
	SIMAttributeTypeConstants(int id,String name,boolean encr,boolean skip){
		this.Id= id;
		this.name = name;
		this.encr = encr;
		this.skip = skip;
	}
	public int getId(){
		return this.Id;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	public static String getName(int value){
		return map.get(value).name;
	}
	public static int getId(String name){
		return nameMap.get(name).Id;
	}	
}
