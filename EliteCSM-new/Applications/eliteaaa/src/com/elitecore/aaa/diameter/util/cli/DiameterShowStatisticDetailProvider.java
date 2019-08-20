package com.elitecore.aaa.diameter.util.cli;

import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.core.util.cli.cmd.DetailProvider;

public class DiameterShowStatisticDetailProvider extends DetailProvider {

	private static final String STATISTIC 		= "statistic";
	private final static String HELP			= "-help";

	private HashMap<String ,DetailProvider> detailProviderMap;

	public DiameterShowStatisticDetailProvider(){
		detailProviderMap = new HashMap<String ,DetailProvider>();
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
		responseBuilder.append("\nUsage 	  : show diameter statistic <options>");
		responseBuilder.append("\nDescription : " + getDescription());
		responseBuilder.append("\n\n-----------------------------Possible Options:------------------------------\n");
		for(DetailProvider detailProvider : detailProviderMap.values()){
			responseBuilder.append(detailProvider.getHelpMsg());
		}
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return STATISTIC;
	}

	public String getDescription(){
		return "Displays Diameter Statistics of given parameter";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	@Override
	public String getHotkeyHelp() {
		
		String childDetailProvidersHotKeyHelp = "";
		childDetailProvidersHotKeyHelp = getChildDetailProvidersHotKeyHelp();
		String hotKeyHelp = "'"+STATISTIC+"':{ " + childDetailProvidersHotKeyHelp + " '"+HELP+"':{}}";
		return hotKeyHelp;
	}

	private String getChildDetailProvidersHotKeyHelp() {

		StringBuffer appHotKey = new StringBuffer();
		for(DetailProvider detailProvider: detailProviderMap.values()){

			String hotKey = detailProvider.getHotkeyHelp();
			if(hotKey != null && hotKey.trim().length() > 0){
				appHotKey.append(hotKey);
				appHotKey.append(",");
			}
		}
		return appHotKey.toString();
	}
	
}
