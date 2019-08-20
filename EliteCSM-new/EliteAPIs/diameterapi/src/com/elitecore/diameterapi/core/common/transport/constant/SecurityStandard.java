package com.elitecore.diameterapi.core.common.transport.constant;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum SecurityStandard {

	@XmlEnumValue(value = "RFC 6733")
	RFC_6733("RFC 6733"),
	
	@XmlEnumValue(value = "RFC 3588 Dynamic")
	RFC_3588_DYNAMIC("RFC 3588 Dynamic"),
	
	@XmlEnumValue(value = "RFC 3588 TLS")
	RFC_3588_TLS("RFC 3588 TLS"),
	
	@XmlEnumValue(value = "NONE")
	NONE("NONE");
	
	public final String val;
	
	private static final Map<String,SecurityStandard> map;
	
	
	static {
		map = new HashMap<String,SecurityStandard>();
		for (SecurityStandard securityStandard : values()) {
			map.put(securityStandard.val, securityStandard);
		}
	}
	
	private SecurityStandard(String val){
		this.val = val;
	}
	
	public static SecurityStandard fromSecurityStandardVal(String val) {
		return map.get(val);
	}
	
	
}
