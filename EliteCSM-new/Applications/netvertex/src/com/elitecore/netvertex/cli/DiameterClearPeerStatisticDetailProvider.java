package com.elitecore.netvertex.cli;

import java.util.HashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticResetter;

public class DiameterClearPeerStatisticDetailProvider extends DetailProvider {

	private final static String PEER			= "peer";
	private static final String HOST_IDENTITY 	= "<Host Identity>";
	private final static String HELP			= "-help";
	private final static String SUCCESS_MSG		= "Cleared Peer Statistics successfully.\n";
	private final static String FAILURE_MSG		= "Unable to Clear Peer Statistics.\n";
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private DiameterStatisticResetter statisticResetter;
	
	public DiameterClearPeerStatisticDetailProvider(
			DiameterStatisticResetter statisticResetter) {
		this.detailProviderMap = new HashMap<String ,DetailProvider>();
		this.statisticResetter = statisticResetter;
	}
	
	@Override
	public String execute(String[] parameters) {
		if(parameters == null || parameters.length == 0) {
			if(statisticResetter.resetAllPeerStatistics()) {
				return SUCCESS_MSG;
			} 
			
			return FAILURE_MSG;
		}
		
		if(isHelpSymbol(parameters[0])){
			return getPeerUsage();
		}
		if(statisticResetter.resetPeerStatistics(parameters[0]))
			return SUCCESS_MSG;
		return FAILURE_MSG;
	}

	public String getPeerUsage() {
		
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	 : clear diameter statistic " + PEER + " ["+HOST_IDENTITY+"]");
		responseBuilder.append("\nDescription : "+getDescription() +"\n\n");
		responseBuilder.append("----------------------------------Possible Options:---------------------------------------\n");
		responseBuilder.append( HOST_IDENTITY + "  : Clears Diameter Peer statistics for Specific Peer\n");
		return responseBuilder.toString();
	}

	@Override
	public String getHelpMsg() {
		return StringUtility.fillChar(PEER, 30, ' ') + " : " + getDescription() + "\n";
	}

	@Override
	public String getHotkeyHelp() {
		return "'"+getKey()+"':{'"+HELP+"':{}}";
	}

	public String getDescription() {
		return "Clears Diameter Peer Statistics.";
	}

	@Override
	public String getKey() {
		return PEER;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

}
