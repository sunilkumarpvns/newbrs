package com.elitecore.corenetvertex.constants;

public enum GatewayTypeConstant {
	
	RADIUS("Radius"),
	DIAMETER("Diameter"),
	CISCOSCE("CiscoSCE");
	
	public String val;
	private GatewayTypeConstant(String val){
		this.val = val;
	}
	
	public String getVal(){
		return val;
	}
	
	

}
