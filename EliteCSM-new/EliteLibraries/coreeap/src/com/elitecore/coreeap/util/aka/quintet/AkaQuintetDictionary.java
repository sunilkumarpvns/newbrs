package com.elitecore.coreeap.util.aka.quintet;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.constants.aka.AkaQuintetAlgoConstants;

public class AkaQuintetDictionary {

	private static final String MODULE = "AkaQuintet Dictionary";
	
	private static AkaQuintetDictionary akaQuintetDictionary;
	
	private Map<Integer, IAkaQuintet> akaQuintetmap;
	
	public AkaQuintetDictionary() {
		akaQuintetmap = new HashMap<Integer, IAkaQuintet>();
		loadInstances();
	}
	
	static {
		akaQuintetDictionary = new AkaQuintetDictionary();
	}
	
	public static AkaQuintetDictionary getInstance(){
		return akaQuintetDictionary;
	}
	
	private void loadInstances(){
		
//		AkaSha1 akaSha1 = new AkaSha1();
//		akaQuintetmap.put(AkaQuintetAlgoConstants.AKA_SHA1.getCode(), akaSha1);
		
		AkaMilenageOpc akaMilenageOpc = new AkaMilenageOpc();
		akaQuintetmap.put(AkaQuintetAlgoConstants.AKA_MILENAGE_OPC.getCode(), akaMilenageOpc);
		
		AkaMilenage akaMilenage = new AkaMilenage();
		akaQuintetmap.put(AkaQuintetAlgoConstants.AKA_MILENAGE.getCode(), akaMilenage);
		
		AkaXor akaXor = new AkaXor();
		akaQuintetmap.put(AkaQuintetAlgoConstants.AKA_XOR.getCode(), akaXor);

	}
	
	public IAkaQuintet getAkaQuintet(int code){
		IAkaQuintet akaQuintet = akaQuintetmap.get(code);
		if(akaQuintet != null){
			try {
				return (IAkaQuintet) akaQuintet.clone();
			} catch (CloneNotSupportedException e) {
				LogManager.getLogger().trace(MODULE, "Clone method is not supported in IAkaQuintet Algo : " + code);
			}
		}else{
			LogManager.getLogger().trace(MODULE, "IAkaQuintet Algo : " + code +" is not supported");
		}
		return akaQuintet;
	}
	
}
