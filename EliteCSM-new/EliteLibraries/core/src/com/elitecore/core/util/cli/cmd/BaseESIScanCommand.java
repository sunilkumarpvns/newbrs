package com.elitecore.core.util.cli.cmd;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public abstract class BaseESIScanCommand extends EliteBaseCommand {

	protected static Map<String,Map<String, ESIStatus>> esiStatusMap;
	
	public static void registerESI(ESIStatus esiStatus){
		Map<String, ESIStatus> tmpMap = esiStatusMap.get(buildParameterString(esiStatus.getConfiguredESType()));
		if(tmpMap != null){
			tmpMap.put(esiStatus.getESName(), esiStatus);
		}		
	}
	
	public interface ESIStatus{
		public static final String ALIVE = "ALIVE";
		public static final String DEAD = "DEAD";
		public static final String FAIL = "FAIL";
		public String getESStatus();
		public String getESName();
		public String getConfiguredESType();
	}
	
	protected static String buildParameterString(String esName){
		return "-"+esName.toLowerCase();
	}
	
	static{
		esiStatusMap = new HashMap<String, Map<String, ESIStatus>>();
	}
	
}
