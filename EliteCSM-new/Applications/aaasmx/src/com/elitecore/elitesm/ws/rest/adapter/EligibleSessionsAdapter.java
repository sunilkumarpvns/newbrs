package com.elitecore.elitesm.ws.rest.adapter;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class EligibleSessionsAdapter extends XmlAdapter<String, Integer>{

	public static final String MODULE = "ELIGIBLE-SESSION-ADAPTER";

	@Override
	public Integer unmarshal(String v) throws Exception {
		return EligibleSession.getCodeFrom(v);
	}

	@Override
	public String marshal(Integer v) throws Exception {
		return EligibleSession.getValueFrom(v);
	}
	
	enum EligibleSession {
		NONE(1, "NONE"),
		RECENT(2, "RECENT"),
		OLDEST(3, "OLDEST"),
		ALL(4, "ALL");
		
		public Integer code;
		public String value;
		private static Map<Integer, String> codeToValueMap = new HashMap<Integer, String>();
		private static Map<String, Integer> valueToCodeMap = new HashMap<String, Integer>();
		
		static {
			EligibleSession[] eligibleSessions = values();
			for (EligibleSession eligibleSession : eligibleSessions) {
				codeToValueMap.put(eligibleSession.code, eligibleSession.value);
				valueToCodeMap.put(eligibleSession.value, eligibleSession.code);
			}
		}
		
		private EligibleSession(Integer code, String value) {
			this.code = code;
			this.value = value;
		}
		
		public static Integer getCodeFrom(String value) {
			if (valueToCodeMap.containsKey(value)) {
				return valueToCodeMap.get(value);
			}
			return -1;
		}
		
		public static String getValueFrom(Integer code) {
			if (codeToValueMap.containsKey(code)) {
				return codeToValueMap.get(code);
			}
			return NONE.value;
		}
	}
}
