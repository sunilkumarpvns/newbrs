package com.elitecore.diameterapi.core.stack.constant;

import javax.xml.bind.annotation.XmlEnumValue;


public enum OverloadAction {
	
	@XmlEnumValue(value = "DROP")
	DROP("DROP"),
	@XmlEnumValue(value = "REJECT")
	REJECT("REJECT");
	
	
	public final String val;
	private OverloadAction(String val){
		this.val = val;
	}
	
	public static OverloadAction fromVal(String val){
		if(REJECT.val.equalsIgnoreCase(val)){
			return REJECT;
		}else if(DROP.val.equalsIgnoreCase(val)){
			return DROP;
		}else{
			return null;
		}
	}
}
