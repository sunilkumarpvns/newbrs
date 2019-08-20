package com.elitecore.coreeap.util.constants.tls;

import java.util.HashMap;
import java.util.Map;

public enum HandshakeMessageConstants {
	Hello(0,"Hello"),
	ClientHello(1,"Client Hello"),
	ServerHello(2,"Server Hello"),
	Certificate(11,"Certificate"),
	ServerKeyExchange(12,"Server Key Exchange"),
	CertificateRequest(13,"Certificate Request"),
	ServerHelloDone(14,"Server Hello Done"),
	CertificateVerify(15,"Certificate Verify"),
	ClientKeyExchange(16,"Client Key Exchange"),
	Finished(20,"Finished");
	public final int value;
	public final String name;
	private static final Map<Integer,HandshakeMessageConstants> map;
	public static final HandshakeMessageConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,HandshakeMessageConstants>(14);
		for (HandshakeMessageConstants type : VALUES) {
			map.put(type.value, type);
		}
	}	
	
	HandshakeMessageConstants(int value,String name){
		this.value = value;
		this.name = name;
	}	
	public int getValue(){
		return this.value;
	}
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	
	public static HandshakeMessageConstants get(int key){
		return map.get(key);
	}
	public static String getName(int value){
		return map.get(value).name;
	}
}
