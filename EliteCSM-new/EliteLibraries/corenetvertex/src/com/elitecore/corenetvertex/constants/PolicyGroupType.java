package com.elitecore.corenetvertex.constants;

/**
 * @author Manjil Purohit
 *
 */
public enum PolicyGroupType {

	INDIVIDUAL("INDIVIDUAL"),
	FAMILY("FAMILY"),        
	ENTERPRISE("ENTERPRISE"),
	
	;
	
	public final String type; 
	
	private PolicyGroupType(String type) {
		this.type = type;
	}
	
	public static PolicyGroupType fromString(String type) {
		
		if (INDIVIDUAL.type.equalsIgnoreCase(type)) {
			return PolicyGroupType.INDIVIDUAL;
		} else if (FAMILY.type.equalsIgnoreCase(type)) {
			return PolicyGroupType.FAMILY;
		} else if (ENTERPRISE.type.equalsIgnoreCase(type)) {
			return PolicyGroupType.ENTERPRISE;
		}
		return null;
	}
	
}
