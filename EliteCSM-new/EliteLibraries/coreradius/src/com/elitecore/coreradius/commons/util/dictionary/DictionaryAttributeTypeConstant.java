package com.elitecore.coreradius.commons.util.dictionary;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author narendra.pathai
 *
 */
public enum DictionaryAttributeTypeConstant {
	/*
	 * The type strings are case-insensitive, String and string are same types for us
	 */
	STRING("string"),
	OCTETS("octets"),
	LONG("long"),
	INTEGER("integer"),
	SHORT("short"),
	BYTE("byte"),
	IPADDR("ipaddr"),
	IPV6PREFIX("ipv6prefix"),
	DATE("date"),
	PREPAIDTLV("prepaidTLV"),
	EUI64("EUI64"),
	EUI("EUI"),
	GROUPED("grouped"),
	EVLANID("evlanid"),
	USER_LOCATION_INFO("UserLocationInfo"),
	GTP_TUNNEL_DATA("gtptunnel"),
	GTPV1_TUNNEL_DATA("gtpv1tunnel"),
	CISCO_COMMAND_CODE("CiscoCommandCode"),
	TWAN_IDENTIFIER("TWANIdentifier"),
	
	/* Internal Type */
	UNKNOWN("unknown");
	public String value;
	
	private DictionaryAttributeTypeConstant(String value) {
		this.value = value;
	}

	private static final Map<String,DictionaryAttributeTypeConstant> map;
	static {
		map = new HashMap<String,DictionaryAttributeTypeConstant>();
		for (DictionaryAttributeTypeConstant type : values()) {
			/*
			 * The map stores the values by converting the type value in lower-case,
			 * so that case-insensitive check can be made while locating the type
			 */
			map.put(type.value.toLowerCase(), type);
		}
	}

	/**
	 * Returns the type corresponding to the type string passed, or {@link #UNKNOWN} type
	 * if no type is found corresponding to the type string. Returns {@link #UNKNOWN} type
	 * if {@code typeString} is {@code null}
	 * 
	 * <p>NOTE: The type location is done in <i>case-insensitive</i> manner. String, STRING
	 * and string are all same types and will return {@link #STRING} type.
	 * 
	 * @param typeString string for which the type is to be located
	 * @return type corresponding to type string if found, else UNKNOWN type. Returns UNKNOWN 
	 * type if <code>typeString</code> is <code>null</code>
	 */
	public static DictionaryAttributeTypeConstant from(String typeString){
		if (typeString == null) {
			return UNKNOWN;
		}

		/*
		 * Dictionary attribute type is case-insensitive and map stores the values in lower-case,
		 * so we are converting the typeString to lower-case for checking presence of known type. 
		 */
		DictionaryAttributeTypeConstant type = map.get(typeString.trim().toLowerCase());
		return type != null ? type : UNKNOWN;
	}

	@Override
	public String toString() {
		return name();
	}
}
