package com.elitecore.coreeap.util.constants.certificate;

import java.util.HashMap;
import java.util.Map;


public enum CertificateTypeConstants {
	RSA(1),
	DSS(2),
	RSA_DH(3),
	DSS_DH(4);
	private final int id;
	private static final Map<Integer,CertificateTypeConstants> map;
	public static final CertificateTypeConstants[] VALUES = values();
	static {
		map = new HashMap<Integer,CertificateTypeConstants>(6);
		for (CertificateTypeConstants type : VALUES) {
			map.put(type.id, type);
		}
	}	

	CertificateTypeConstants(int certificateTypeId) {	
		this.id = certificateTypeId;
	}
	
	public static boolean isValid(int value){
		return map.containsKey(value);	
	}
	public int getID() {
		return this.id;
	}
}
