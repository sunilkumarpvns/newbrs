package com.elitecore.coreeap.util.constants.sim;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public enum GsmA3A8AlgoConstants implements IEnum {
	GSM_PRE_PROVISION(0,"Pre Provision"),
	GSM_XOR(1,"GSM XOR"),
	GSM_AES(2,"GSM AES"),
	GSM_MILENAGE1(3,"GSM MILENAGE 1"),
	GSM_MILENAGE2(4,"GSM MILENAGE 2"),
	GSM_MILENAGE_OPC1(5,"GSM MILENAGE OPC 1"),
	GSM_MILENAGE_OPC2(6,"GSM MILENAGE OPC 2");
	private int code;
	private String name;
	private static final Map<Integer, GsmA3A8AlgoConstants> gsmA3A8AlgoConstantsMap;
	public static final GsmA3A8AlgoConstants[] VALUES = values();
	static{
		gsmA3A8AlgoConstantsMap = new HashMap<Integer, GsmA3A8AlgoConstants>();
		for(GsmA3A8AlgoConstants algoConstant : values()){
			gsmA3A8AlgoConstantsMap.put(algoConstant.code, algoConstant);
		}
	}
	private GsmA3A8AlgoConstants(int code,String name) {
		this.code = code;
		this.name = name;
	}
	public int getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public GsmA3A8AlgoConstants getGsmA3A8AlgoConstants(int code){
		return gsmA3A8AlgoConstantsMap.get(code);
	}

	public static boolean isValid(int code){
		return gsmA3A8AlgoConstantsMap.containsKey(code);	
	}
	
}
