package com.elitecore.netvertex.cli;

import java.util.HashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticResetter;

public class DiameterClearStatisticDetailProvider extends DetailProvider {

	private static final String STATISTIC 		= "statistic";
	private static final  String ALL				= "all";
	private static final  String HELP			= "-help";
	private static final String APP 			= "app"; 
	
	private static final String SUCCESS_MSG		= "Cleared Statistics successfully.\n";
	private static final String FAILURE_MSG		= "Unable to Clear Statistics.\n";

	private HashMap<String ,DetailProvider> detailProviderMap;
	private DiameterStatisticResetter  diameterStatisticResetter;

	public DiameterClearStatisticDetailProvider(
			DiameterStatisticResetter resetter){
		detailProviderMap = new HashMap<String ,DetailProvider>();
		diameterStatisticResetter = resetter;
	}

	@Override
	public String execute(String[] parameters) {

		if(parameters == null || parameters.length == 0)
			return getHelpMsg();

		if(ALL.equalsIgnoreCase(parameters[0])){
			if(parameters.length == 1){
				if(diameterStatisticResetter.reset())
					return SUCCESS_MSG;
				return FAILURE_MSG;
			}
			return getAllUsage();
		}else if(isHelpSymbol(parameters[0])){
			return getHelpMsg();
		}else{
			DetailProvider detailProvider = detailProviderMap.get(parameters[0]);
			String [] appParameters = null;
			
			if(detailProvider != null){
				appParameters = new String[parameters.length-1];
				for (int i = 0; i < appParameters.length; i++) {
					appParameters[i] = parameters[i+1];
				}
			}else{
				detailProvider = detailProviderMap.get(APP);
				appParameters = parameters;

			}
			return detailProvider.execute(appParameters);
		}
	}

	private String getAllUsage() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	 : clear diameter "+STATISTIC+ " " + ALL);
		responseBuilder.append("\nDescription: Clear All Diameter Statistics");
		return responseBuilder.toString();
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	  : clear diameter statistic <options>");
		responseBuilder.append("\nDescription : " + getDescription());
		responseBuilder.append("\n\n---------------------Possible Options:----------------------\n");
		responseBuilder.append(StringUtility.fillChar(ALL, 30, ' ') + " : Clear All Diameter Statistics.\n");
		for(DetailProvider detailProvider : detailProviderMap.values()){
			responseBuilder.append(detailProvider.getHelpMsg());
		}
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return STATISTIC;
	}

	@Override
	public String getDescription(){
		return "Clears Diameter Statistics of given parameter";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	@Override
	public String getHotkeyHelp() {
		
		String childDetailProvidersHotKeyHelp = "";
		childDetailProvidersHotKeyHelp = getChildDetailProvidersHotKeyHelp();
		return "'"+STATISTIC+"':{'"+ALL+"':{'"+HELP+"':{}} " + childDetailProvidersHotKeyHelp + ", '"+HELP+"':{}}";
	}

	private String getChildDetailProvidersHotKeyHelp() {

		StringBuilder appHotKey = new StringBuilder();
		for(DetailProvider detailProvider: detailProviderMap.values()){

			String hotKey = detailProvider.getHotkeyHelp();
			if(hotKey != null && hotKey.trim().length() > 0){
				appHotKey.append(",");
				appHotKey.append(hotKey);
			}
		}
		return appHotKey.toString();
	}
}
