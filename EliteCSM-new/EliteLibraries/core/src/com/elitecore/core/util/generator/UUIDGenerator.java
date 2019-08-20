package com.elitecore.core.util.generator;

import java.util.UUID;

/**
 * Generates 128 bits Unique identifier value and removes <b>'-'</b> character from that value
 */
public class UUIDGenerator {

	public static String generate() {
		String uuidStr = UUID.randomUUID().toString();
		String uuidWithoutHyphen = removeHyphenFromUUID(uuidStr); 
		return uuidWithoutHyphen;
	}

	private static String removeHyphenFromUUID(String uuid) {
		return uuid.replaceAll("[\\s\\-()]", "");
	}

}