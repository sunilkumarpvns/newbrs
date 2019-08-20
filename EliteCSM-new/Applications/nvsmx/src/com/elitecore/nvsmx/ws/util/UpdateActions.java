package com.elitecore.nvsmx.ws.util;

import java.util.HashMap;
import java.util.Map;

public enum UpdateActions {
	
	NO_ACTION(0,"No-Action"),
	RE_AUTH_SESSION(1,"Re-Auth"),
	DISCONNECT_SESSION(2,"Disconnect-Session");
	
	private Integer val;
	private String label;
	private static Map<Integer,UpdateActions> map = new HashMap<Integer,UpdateActions>();
	
	static{
		for(UpdateActions updateAction : values()){
			map.put(updateAction.val, updateAction);
		}
	}
	
	UpdateActions(int val,String label){
		this.val = val;
		this.label = label;
	}
	
	public static UpdateActions fromValue(Integer val){
		return map.get(val);
	}
	
	public Integer val(){
		return val;
	}
	
	public String label(){
		return label;
	}
}
