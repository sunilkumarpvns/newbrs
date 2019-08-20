package com.elitecore.coreeap.util.constants.tls;

import java.util.HashMap;
import java.util.Map;

public enum HashAlgorithm {
	NONE(0, "NONE"),
	MD5(1, "MD5"),
	SHA1(2, "SHA-1"),
	SHA224(3, "SHA-224"),
	SHA256(4, "SHA-256"),
	SHA384(5, "SHA-384"),
	SHA512(6, "SHA-512")
	;
	
	private final int value;
	private final String identifier;
	private static final Map<Integer, HashAlgorithm> valueToHashAlgorithm;
	public static final HashAlgorithm[] VALUES = values();
	
	static{
		valueToHashAlgorithm = new HashMap<Integer, HashAlgorithm>(8);
		for(HashAlgorithm hash : VALUES){
			valueToHashAlgorithm.put(hash.value, hash);
		}
	}
	
	HashAlgorithm(int value, String name) {
		this.value = value;
		this.identifier = name;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public static boolean isValid(String key){
		return valueToHashAlgorithm.containsKey(key);
	}
	
	public static String getStringIdentifier(int value){
		HashAlgorithm algo = valueToHashAlgorithm.get(value); 
		return algo != null ? algo.identifier : null;
	}
}
