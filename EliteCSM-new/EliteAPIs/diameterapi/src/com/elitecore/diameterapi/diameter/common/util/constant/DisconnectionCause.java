package com.elitecore.diameterapi.diameter.common.util.constant;

import java.util.HashMap;
import java.util.Map;

public enum DisconnectionCause {
	REBOOTING(0),
	BUSY(1),
	DO_NOT_WANT_TO_TALK_TO_YOU(2),
	;

	private static final Map<Integer,DisconnectionCause> map;
	public static final DisconnectionCause[] VALUES = values();

	static {
		map = new HashMap<Integer,DisconnectionCause>();
		for (DisconnectionCause type : VALUES) {
			map.put(type.code, type);
		}
	}

	public final int code;

	DisconnectionCause(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static boolean isValid(int value) {
		return map.containsKey(value);
	}

	public static DisconnectionCause fromCode(int value) {
		return map.get(value);
	}

}
