package com.elitecore.netvertex.usagemetering;


public enum UMLevel {
	
	SESSION_LEVEL(0),
	PCC_RULE_LEVEL(1);
	
	public final int val;
	
	private UMLevel(int val){
		this.val = val;
	}
	
	public int getVal(){
		return val;
	}

	public static UMLevel getMonitoringLevelMap(int level) {
		if(level == SESSION_LEVEL.val){
			return SESSION_LEVEL;
		} else if(level == PCC_RULE_LEVEL.val){
			return PCC_RULE_LEVEL;
		} 
			
		return null;
	}
	
}
