package com.elitecore.coreeap.commons.util.constants.policy;


import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum EapPolicyValueConstants implements IEnum{
	//Eap Decision Constants
	SUCCESS(1, "Success"),
	FAILURE(-1, "Failure") ,
	CONTINUE(0, "Continue"),
	//EAP Type Constants
	IDENTITY_TYPE(EapTypeConstants.IDENTITY.typeId, EapTypeConstants.IDENTITY.name),
	NOTIFICATION_TYPE(EapTypeConstants.NOTIFICATION.typeId, EapTypeConstants.NOTIFICATION.name),
	NAK_TYPE(EapTypeConstants.NAK.typeId, EapTypeConstants.NAK.name),
	EXPANDED_TYPE(EapTypeConstants.EXPANDED.typeId, EapTypeConstants.EXPANDED.name),
	EXPERIMENTAL_TYPE(EapTypeConstants.EXPERIMENTAL.typeId, EapTypeConstants.EXPERIMENTAL.name),
	NO_ALTERNATIVE_TYPE(EapTypeConstants.NO_ALTERNATIVE.typeId, EapTypeConstants.NO_ALTERNATIVE.name),
	GTC(EapTypeConstants.GTC.typeId, EapTypeConstants.GTC.name),
	EAP_MD5(EapTypeConstants.MD5_CHALLENGE.typeId, EapTypeConstants.MD5_CHALLENGE.name),	
	EAP_TLS(EapTypeConstants.TLS.typeId, EapTypeConstants.TLS.name),
	EAP_SIM(EapTypeConstants.SIM.typeId,EapTypeConstants.SIM.name),
	EAP_TTLS(EapTypeConstants.TTLS.typeId, EapTypeConstants.TTLS.name),
	EAP_AKA(EapTypeConstants.AKA.typeId,EapTypeConstants.AKA.name),
	EAP_AKA_PRIME(EapTypeConstants.AKA_PRIME.typeId,EapTypeConstants.AKA_PRIME.name),
	EAP_PEAP(EapTypeConstants.PEAP.typeId, EapTypeConstants.PEAP.name),
	EAP_MSCHAPv2(EapTypeConstants.MSCHAPv2.typeId, EapTypeConstants.MSCHAPv2.name),
	NONE(0, "None");
	
	public final int value;
	public final String name;
	private static final Map<Integer,EapPolicyValueConstants> map;
	private static final Map<String,EapPolicyValueConstants> nameMap;
	public static final EapPolicyValueConstants[] VALUES = values();
	
	static {
		map = new HashMap<Integer,EapPolicyValueConstants>();
		for (EapPolicyValueConstants type : VALUES) {
			map.put(type.value, type);
		}
		nameMap = new HashMap<String,EapPolicyValueConstants>();
		for (EapPolicyValueConstants type : VALUES) {
			nameMap.put(type.name, type);
		}
	}	
	EapPolicyValueConstants(int value, String name) {
		this.value = value;
		this.name = name;
	}
	public int getValue(){
		return this.value;
	}
	public static String getName(int value){
		return map.get(value).name;
	}
	public static int getValue(String name){
		return nameMap.get(name).value;
	}
}
