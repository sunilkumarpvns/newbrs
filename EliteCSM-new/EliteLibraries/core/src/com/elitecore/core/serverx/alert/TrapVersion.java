package com.elitecore.core.serverx.alert;


public enum TrapVersion {

	V1("V1" , "0"),
	V2c("V2c" , "1"),
	;

	private String name;
	private String val;

	private TrapVersion(String name , String val){
		this.name = name;
		this.val = val;
	}

	public static TrapVersion fromValue(String version){
		if(V1.getVal().equals(version)){
			return V1;
		}else if(V2c.getVal().equals(version)){
			return V2c;
		}
		return null;
	}

	public String getName(){
		return name;
	}

	public String getVal(){
		return val;
	}
}
