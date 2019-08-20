package com.elitecore.aaa.diameter.util.cli;

import java.util.HashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticResetter;

public class DiameterClearRealmStatisticDetailProvider extends DetailProvider {

	private final static String REALM			= "realm";
	private static final String REALM_NAME 		= "<Realm Name>";
	private final static String HELP			= "-help";
	private final static String SUCCESS_MSG		= "Cleared Realm Statistics successfully.\n";
	private final static String FAILURE_MSG		= "Unable to Clear Realm Statistics.\n";
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private DiameterStatisticResetter statisticResetter;
	
	public DiameterClearRealmStatisticDetailProvider(
			DiameterStatisticResetter statisticResetter) {
		this.detailProviderMap = new HashMap<String ,DetailProvider>();
		this.statisticResetter = statisticResetter;
	}

	@Override
	public String execute(String[] parameters) {
		
		if(parameters == null || parameters.length == 0)
			if(statisticResetter.resetAllRealmStatistics())
				return SUCCESS_MSG;
		
		if(isHelpSymbol(parameters[0])){
			return getRealmUsage();
		}
		if(statisticResetter.resetRealmStatistics(parameters[0]))
			return SUCCESS_MSG;
		return FAILURE_MSG;
	}

	@Override
	public String getHelpMsg() {
		return StringUtility.fillChar(REALM, 20, ' ') + " : " + getDescription() + "\n";
	}
	
	public String getRealmUsage() {
		
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	 : clear diameter statistic realm ["+REALM_NAME+"]");
		responseBuilder.append("\nDescription : "+getDescription() +"\n\n");
		responseBuilder.append("----------------------------Possible Options:---------------------------------\n");
		responseBuilder.append( REALM_NAME + "  : Clears Diameter Realm statistics for provided Realm Name\n");
		return responseBuilder.toString();
	}
	
	@Override
	public String getHotkeyHelp() {
		return "'"+getKey()+"':{'"+HELP+"':{}}";
	}

	public String getDescription() {
		return "Clears Diameter Realm Statistics.";
	}

	@Override
	public String getKey() {
		return REALM;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

}
