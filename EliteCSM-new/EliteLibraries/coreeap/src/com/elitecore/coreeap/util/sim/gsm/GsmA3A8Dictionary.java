package com.elitecore.coreeap.util.sim.gsm;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.constants.sim.GsmA3A8AlgoConstants;

public class GsmA3A8Dictionary {

	private static final String MODULE = "GSMA3A8 Dictionary";
	
	private static GsmA3A8Dictionary gsmA3A8Dictionary;
	
	private Map<Integer, IGsmA3A8> gsmA3A8map;
	
	public GsmA3A8Dictionary() {
		gsmA3A8map = new HashMap<Integer, IGsmA3A8>();
		loadInstances();
	}
	
	static {
		gsmA3A8Dictionary = new GsmA3A8Dictionary();
	}
	public static GsmA3A8Dictionary getInstance(){
		return gsmA3A8Dictionary;
	}
	
	private void loadInstances(){
		GsmAes gsmAes = new GsmAes();
		gsmA3A8map.put(GsmA3A8AlgoConstants.GSM_AES.getCode(), gsmAes);
		
		GsmMilenage1 gsmMilenage1 = new GsmMilenage1();
		gsmA3A8map.put(GsmA3A8AlgoConstants.GSM_MILENAGE1.getCode(), gsmMilenage1);
		
		GsmMilenage2 gsmMilenage2 = new GsmMilenage2();
		gsmA3A8map.put(GsmA3A8AlgoConstants.GSM_MILENAGE2.getCode(), gsmMilenage2);
		
		GsmMilenageOpc1 gsmMilenageOpc1 = new GsmMilenageOpc1();
		gsmA3A8map.put(GsmA3A8AlgoConstants.GSM_MILENAGE_OPC1.getCode(), gsmMilenageOpc1);
		
		GsmMilenageOpc2 gsmMilenageOpc2 = new GsmMilenageOpc2();
		gsmA3A8map.put(GsmA3A8AlgoConstants.GSM_MILENAGE_OPC2.getCode(), gsmMilenageOpc2);
		
		GsmXor gsmXor = new GsmXor();
		gsmA3A8map.put(GsmA3A8AlgoConstants.GSM_XOR.getCode(), gsmXor);		
		
	}
	
	public IGsmA3A8 getGsmA3A8(int code){
		IGsmA3A8 gsmA3A8 = gsmA3A8map.get(code);
		if(gsmA3A8 != null){
			try {
				return (IGsmA3A8) gsmA3A8.clone();
			} catch (CloneNotSupportedException e) {
				LogManager.getLogger().trace(MODULE, "Clone method is not supported in GSMA3A8 Algo : " + code);
			}
		}else{
			LogManager.getLogger().trace(MODULE, "GSMA3A8 Algo : " + code +" is not supported");
		}
		return gsmA3A8;
	}
	
}
