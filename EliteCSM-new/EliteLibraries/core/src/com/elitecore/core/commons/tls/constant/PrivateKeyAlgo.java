package com.elitecore.core.commons.tls.constant;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnumValue;

public enum PrivateKeyAlgo {
	
	@XmlEnumValue(value = "RSA")
	RSA("RSA"),
	
	@XmlEnumValue(value = "DiffieHellman")
	DHA("DiffieHellman"),
	
	@XmlEnumValue(value = "DSA")
	DSA("DSA");
	
	public final String name;
	private static Map<String, PrivateKeyAlgo> privateKeyAlgos = new HashMap<String, PrivateKeyAlgo>();
	
	private PrivateKeyAlgo(String name){
		this.name = name;
	}
	
	static{
		for(PrivateKeyAlgo privateKeyAlgo : values()){
			privateKeyAlgos.put(privateKeyAlgo.name, privateKeyAlgo);
		}
	}
	
	
	public static PrivateKeyAlgo fromAlgoName(String algoName){
		return privateKeyAlgos.get(algoName);
	}
	
	
}
