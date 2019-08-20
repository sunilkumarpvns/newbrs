package com.elitecore.corenetvertex.constants;

public enum GatewayComponent {
	ACCESS_GATEWAY("Access Gateway"),
	APPLICATION_FUNCTION("Application Function"),
	V_PCRF("V-PCRF"),
	OCS("OCS"),
	DRA("DRA");
	
	public String value;
	private GatewayComponent(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}
}
