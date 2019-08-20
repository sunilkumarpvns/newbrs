package com.elitecore.aaa.radius.util.converters;

import java.util.HashMap;

public class PrepaidConverterFactory {

	static private PrepaidConverterFactory prepaidConverterFactory;
	private HashMap<String, IPrepaidConverter> prepaidConverterInstanceMap;
	
	private PrepaidConverterFactory(){
		loadPrepaidConverterInstances();
	}
	
	private void loadPrepaidConverterInstances(){
		prepaidConverterInstanceMap = new HashMap<String, IPrepaidConverter>();
		
		IPrepaidConverter prepaidConverter = new Wimax12PrepaidConverter();
		IPrepaidConverter prepaidConverterFor3gpp2 = new ThreeGPP2PrepaidConverter();
		IPrepaidConverter ztePrepaidConverter = new ZTEPrepaidConverter();
		prepaidConverterInstanceMap.put(ElitePrepaidConstants.WimaxPrepaidStandard, prepaidConverter);
		prepaidConverterInstanceMap.put(ElitePrepaidConstants.PrepaidStandardFor3GPP2, prepaidConverterFor3gpp2);
		prepaidConverterInstanceMap.put(ElitePrepaidConstants.ZTEPrepaidStandard, ztePrepaidConverter);
	}	

	static {
		prepaidConverterFactory = new PrepaidConverterFactory();
	}
	
	public static PrepaidConverterFactory getInstance() {
		return prepaidConverterFactory;
	}
	
	public IPrepaidConverter createPrepaidConverter(String prepaidStandard) {
		return prepaidConverterInstanceMap.get(prepaidStandard);
	}
	
}
