package com.elitecore.coreeap.util.constants.aka;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum AkaQuintetAlgoConstants implements IEnum {
	AKA_PRE_PROVISION(0,"Pre Provision"),
	AKA_MILENAGE(1,"AKA Milenage"),
	AKA_MILENAGE_OPC(2,"AKA MilenageOpc"),	
	AKA_XOR(3,"AKA Xor");
//	AKA_SHA1(3,"AKA Sha1");
	
	private int code;
	private String name;
	private static final Map<Integer, AkaQuintetAlgoConstants> akaQuintetAlgoConstantsMap;
	public static final AkaQuintetAlgoConstants[] VALUES = values();
	static{
		akaQuintetAlgoConstantsMap = new HashMap<Integer, AkaQuintetAlgoConstants>();
		for(AkaQuintetAlgoConstants algoConstant : values()){
			akaQuintetAlgoConstantsMap.put(algoConstant.code, algoConstant);
		}
	}
	private AkaQuintetAlgoConstants(int code,String name) {
		this.code = code;
		this.name = name;
	}
	public int getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public AkaQuintetAlgoConstants getAkaQuintetAlgoConstants(int code){
		return akaQuintetAlgoConstantsMap.get(code);
	}

	public static boolean isValid(int code){
		return akaQuintetAlgoConstantsMap.containsKey(code);	
	}

}
