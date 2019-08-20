package com.elitecore.diameterapi.diameter.common.util;

import java.util.ArrayList;
import java.util.List;

public enum InbandSecurityId {

	NO_INBAND_SECURITY(0),
	TLS(1),
	IPSec(2),;
	public static final InbandSecurityId[] VALUES = values();
	private static final List<InbandSecurityId> list;

	static {
		list = new ArrayList<InbandSecurityId>();
		for (InbandSecurityId type : VALUES) {
			list.add(type);
		}
	}

	public final int code;

	InbandSecurityId(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static boolean isValid(int value) {
		return list.contains(value);
	}

	public static InbandSecurityId fromCode(int value) {
		return list.get(value);
	}

	public static List<InbandSecurityId> keys() {
		return list;
	}
}
