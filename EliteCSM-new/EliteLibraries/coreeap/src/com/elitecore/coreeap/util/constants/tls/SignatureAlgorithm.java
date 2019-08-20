package com.elitecore.coreeap.util.constants.tls;

import java.util.HashMap;
import java.util.Map;

public enum SignatureAlgorithm {
	ANONYMOUS(0),
	RSA(1),
	DSA(2),
	ECDSA(3)
	;
	
	private final int value;
	private static Map<Integer, SignatureAlgorithm> map;
	public static final SignatureAlgorithm[] VALUES = values();
	
	static{
		map = new HashMap<Integer, SignatureAlgorithm>(5);
		for(SignatureAlgorithm signatureAlgorithm : VALUES){
			map.put(signatureAlgorithm.value, signatureAlgorithm);
		}
	}
	
	private SignatureAlgorithm(int value) {
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public static boolean isValid(int key){
		return map.containsKey(key);
	}
	
	public static String getStringIdentifier(int value){
		SignatureAlgorithm algo = map.get(value);
		return algo != null ? algo.name() : null;
		
	}
}
