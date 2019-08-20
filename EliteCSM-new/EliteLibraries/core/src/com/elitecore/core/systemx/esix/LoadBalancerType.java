package com.elitecore.core.systemx.esix;

import java.util.HashMap;
import java.util.Map;

public enum LoadBalancerType {

	ROUND_ROBIN("ROUND_ROBIN"),
	FAIL_OVER("FAIL_OVER"),
	SWITCH_OVER("SWITCH_OVER");
	
	public final String type;
	
	public static final LoadBalancerType[] types = values();
	private static final Map<String , LoadBalancerType> map;
	
	static {
		map = new HashMap<String,LoadBalancerType>();
		for ( LoadBalancerType type : types){
			map.put(type.type, type);
		}
	}
	
	private LoadBalancerType(String type){
		this.type = type;
	}
	
	public String getTypeID (){
		return type;
	}

	public static LoadBalancerType fromTypeID (String type){
		return map.get(type);
	}
}
