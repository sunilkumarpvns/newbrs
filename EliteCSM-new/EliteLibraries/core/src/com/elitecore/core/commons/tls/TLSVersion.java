package com.elitecore.core.commons.tls;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.HashMap;
import java.util.Map;


@XmlEnum(value = String.class)
public enum TLSVersion {

	// do not change the order
	
	@XmlEnumValue(value = "TLSv1")
	TLS1_0("TLSv1"),
	@XmlEnumValue(value = "TLSv1.1")
	TLS1_1("TLSv1.1"),
	@XmlEnumValue(value = "TLSv1.2")
	TLS1_2("TLSv1.2");

	public final String version;

	private static final Map<String,TLSVersion> versionToTLSVersion;

	private TLSVersion(String name) {
		this.version = name;
	}

	static {
		versionToTLSVersion = new HashMap<>(14);
		for (TLSVersion type : values()) {
			versionToTLSVersion.put(type.version, type);
		}
	}
	
	public static TLSVersion fromVersion(String version){
		return versionToTLSVersion.get(version);
	}
	
}
