package com.elitecore.elitesm.ws;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.elitecore.elitesm.util.logger.Logger;

public class WebServiceUtils {
	private static String MODULE="WEB-SERVICE-UTILS";
	public static Map<String,String> getAttrIDColumnMap(String paramString){
		Map<String,String> paramValueMap = new HashMap<String, String>();
		
		if(paramString!=null){
			StringTokenizer tokenizer = new java.util.StringTokenizer(paramString,",;&",false);
			while(tokenizer.hasMoreTokens()){
				String token = tokenizer.nextToken();
				String paramValues[] = token.split("=");
				if(paramValues!=null && paramValues.length==2){
					String key = paramValues[0];
					String value = paramValues[1];
					int dollarIndex = value.indexOf('$');
					if(dollarIndex>-1){
						value = value.substring(dollarIndex+1);
					}
					if(value.trim().length()>0 && key.trim().length()>0){
						paramValueMap.put(key.trim().toLowerCase(),value.trim().toLowerCase());
					}
				}
			}
		}
		Logger.logDebug(MODULE, " AttrID - Column Map :"+paramValueMap);
		return paramValueMap;
	}

}
