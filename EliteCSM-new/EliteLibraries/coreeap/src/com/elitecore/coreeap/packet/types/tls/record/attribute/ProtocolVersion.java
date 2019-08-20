package com.elitecore.coreeap.packet.types.tls.record.attribute;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnumValue;

public enum ProtocolVersion {
	@XmlEnumValue(value = "TLSv1")
	TLS1_0(3,1,"TLSv1"),
	@XmlEnumValue(value = "TLSv1.1")
	TLS1_1(3,2,"TLSv1.1"),
	@XmlEnumValue(value = "TLSv1.2")
	TLS1_2(3,3,"TLSv1.2");
	
	private int major;
	private int minor;
	private String version;
	
	public static final int MAJOR_PROTOCOL_VERSION = 3;
	private static Map<Integer, ProtocolVersion> minorVersionToProtocolVersion;
	private static Map<String, ProtocolVersion> versionStringToProtocolVersion;

	static{
		minorVersionToProtocolVersion = new HashMap<Integer, ProtocolVersion>(5);
		versionStringToProtocolVersion = new HashMap<String, ProtocolVersion>(5);
		for(ProtocolVersion protocolVersion : values()){
			minorVersionToProtocolVersion.put(protocolVersion.getMinor(), protocolVersion);
			versionStringToProtocolVersion.put(protocolVersion.getVersionIdentifier(), protocolVersion);
		}
	}
	
	ProtocolVersion(int major, int minor){
		this.major = major;
		this.minor = minor;
	}
	
	private ProtocolVersion(int major, int minor, String version) {
		this.major = major;
		this.minor = minor;
		this.version = version;
	}
	
	public int getMajor() {
		return this.major;
	}
	public int getMinor() {
		return this.minor;
	}
	
	public String getVersionIdentifier() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public static ProtocolVersion getProtocolVersion(int majorVersion, int minorVersion){
		return minorVersionToProtocolVersion.get(minorVersion);
	}
	
	public boolean isSmaller(ProtocolVersion protocolVersion){
		return this.minor < protocolVersion.minor;
	}
	
	public boolean isGreater(ProtocolVersion protocolVersion){
		return this.minor > protocolVersion.minor;
	}
	
	public static ProtocolVersion fromVersion(String versionIdentifier){
		return versionStringToProtocolVersion.get(versionIdentifier);
	}
	
	public static boolean isValid(String versionIdentifier){
		return versionStringToProtocolVersion.containsKey(versionIdentifier);
	}
	
	@Override
	public String toString() {
		return getVersionIdentifier();
	}
}
