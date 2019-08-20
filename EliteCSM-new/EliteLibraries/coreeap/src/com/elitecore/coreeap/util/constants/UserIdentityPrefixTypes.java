package com.elitecore.coreeap.util.constants;

import com.elitecore.commons.base.Strings;

public enum UserIdentityPrefixTypes {

	AKA_PERMANENT("0"),
	SIM_PERMANENT("1"),
	AKA_PRIME_PERMANENT("6");
	
	public final String identifier;
	
	private UserIdentityPrefixTypes(String identifier) {
		this.identifier = identifier;
	}
	
	public static boolean isPrefixed(String userIdentity){
		if(Strings.isNullOrBlank(userIdentity)){
			return false;
		}
		return userIdentity.startsWith(AKA_PERMANENT.identifier) || 
				userIdentity.startsWith(SIM_PERMANENT.identifier) ||
				userIdentity.startsWith(AKA_PRIME_PERMANENT.identifier);
	}
	
}
