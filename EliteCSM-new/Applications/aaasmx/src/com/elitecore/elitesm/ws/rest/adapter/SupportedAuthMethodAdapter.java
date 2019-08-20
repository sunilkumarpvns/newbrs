package com.elitecore.elitesm.ws.rest.adapter;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class SupportedAuthMethodAdapter extends XmlAdapter<String, Long> {

	@Override
	public Long unmarshal(String type) throws Exception {
		return AuthMethod.getCodeFromType(type);
	}

	@Override
	public String marshal(Long code) throws Exception {
		return AuthMethod.getTypeFromCode(code);
	}

	enum AuthMethod {
		PAP(1L, "PAP"),
		CHAP(2L, "CHAP"),
		EAP(3L, "EAP"),
		DIGEST(4L, "DIGEST"),
		PROXY(5L, "PROXY");
		
		public Long code;
		private String type;
		public static final AuthMethod[] VALUES = values();
		
		private static Map<String, AuthMethod> valueToType = new HashMap<String, SupportedAuthMethodAdapter.AuthMethod>();
		private static Map<Long, AuthMethod> codeToType = new HashMap<Long, SupportedAuthMethodAdapter.AuthMethod>();
		
		static {
			valueToType = new HashMap<String, AuthMethod>();
			for (AuthMethod type : VALUES) {
				valueToType.put(type.type, type);
				codeToType.put(type.code, type);
			}
		}
		
		AuthMethod(Long code, String type) {
			this.code = code;
			this.type = type;
		}
		
		public static Long getCodeFromType(String type) {
			if (valueToType.containsKey(type)) {
				return valueToType.get(type).code;
			}
			return null;
		}
		
		public static String getTypeFromCode(Long code) {
			if (codeToType.containsKey(code)) {
				return codeToType.get(code).type;
			}
			return null;
		}
	}
}