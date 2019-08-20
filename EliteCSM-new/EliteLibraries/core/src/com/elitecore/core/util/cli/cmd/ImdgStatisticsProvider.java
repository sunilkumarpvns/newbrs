package com.elitecore.core.util.cli.cmd;

import java.util.Arrays;
import java.util.HashMap;

public class ImdgStatisticsProvider extends DetailProvider{

	private static final String KEY 		= "statistics";

	private HashMap<String ,DetailProvider> detailProviderMap;

	public ImdgStatisticsProvider() {
		detailProviderMap = new HashMap<String ,DetailProvider>();
	}
	
	@Override
	public void registerDetailProvider(DetailProvider detailprovider) throws RegistrationFailedException {
		
		if(detailProviderMap.containsKey(detailprovider.getKey())){
			throw new RegistrationFailedException("Failed to register detail provider. Reason : Show Command already contains detail provider with Key : " + detailprovider.getKey());
		}
		
		detailProviderMap.put(detailprovider.getKey() , detailprovider);	

	}

	@Override
	public String execute(String[] parameters) {
		if(parameters == null || parameters.length == 0) {
			return getHelpMsg();
		}
		
		DetailProvider detailProvider = detailProviderMap.get(parameters[0]);
		if(detailProvider != null){
			parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
			return detailProvider.execute(parameters);
		}
		return getHelpMsg();
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	  : show imdg statistics <options>");
		responseBuilder.append("\nDescription : " + getDescription());
		responseBuilder.append("\n\nPossible Options:\n");
		for(DetailProvider detailProvider : detailProviderMap.values()){
			responseBuilder.append(detailProvider.getKey());
		}
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	@Override
	public String getDescription() {
		return "Display details for given parameter";
	}

}
