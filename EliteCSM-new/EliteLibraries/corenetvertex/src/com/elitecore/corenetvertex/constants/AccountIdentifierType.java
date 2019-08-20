package com.elitecore.corenetvertex.constants;
/**
 * @author Ajay pandey
 *
 */
public enum AccountIdentifierType {
	

	INGRESS("Ingress"),
	EGRESS("Egress"),
	ACCOUNT_ID("Account ID"),
	TADIGCODE("Tadigcode"),
	CONTENT_PROVIDER_ID("Content provider ID"),;
	
	private  String value;
		
		private AccountIdentifierType(String value){
			this.value = value;
		}
	
		public String getValue(){
			return value;	
		}
		
		public static AccountIdentifierType fromVal(String value) {
			for (AccountIdentifierType status : values()) {
				if (status.value.equalsIgnoreCase(value)) {
					return status;
				}
			}
			return null;
		}
	
	
	
}
	
