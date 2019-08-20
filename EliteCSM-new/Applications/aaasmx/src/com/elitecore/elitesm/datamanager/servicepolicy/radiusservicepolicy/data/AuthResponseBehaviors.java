package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import com.elitecore.core.commons.config.core.CaseInsensitiveEnumAdapter;

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlEnum
public enum AuthResponseBehaviors {
	
	@XmlEnumValue(value = "REJECT")
	REJECT(0),
	
	@XmlEnumValue(value = "DROP")
	DROP(1),
	
	@XmlEnumValue(value = "HOTLINE")
	HOTLINE(2);
	
	public final int value;
	private static final Map<Integer, AuthResponseBehaviors> map;
	private static final AuthResponseBehaviors[] VALUES = values();
	
	static {
		map = new HashMap<Integer, AuthResponseBehaviors>();
		for (AuthResponseBehaviors type: VALUES) {
			map.put(type.value, type);
		}
	}
	
	private AuthResponseBehaviors(int value) {
		this.value = value;
	}
	
	public static class AuthResponseBehaviorsXMLAdapter extends CaseInsensitiveEnumAdapter<AuthResponseBehaviors> {
		public AuthResponseBehaviorsXMLAdapter() {
			super(AuthResponseBehaviors.class, REJECT);
		}
	}
}
