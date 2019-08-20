package com.elitecore.aaa.diameter.util.cli;

import java.util.Arrays;
import java.util.HashMap;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.cmd.DetailProvider;

public class DiameterShowConfigDetailProvider extends DetailProvider {
	
	private static final String CONFIG 		= "config";
	private static final String HELP = "-help";
	private static final String PEER = "peer";
	private static final String HOST_IDENTIY	= "<Host Identity>";
	private static final String PEER_GROUP		= "peergroup";
	private static final String PEER_GROUP_NAME	= "<Peer Group Name>";

	private HashMap<String ,DetailProvider> detailProviderMap;
	private static DiameterShowConfigDetailProvider diameterShowConfigDetailProvider;
	
	static {
		diameterShowConfigDetailProvider = new DiameterShowConfigDetailProvider();
	}
	
	private DiameterShowConfigDetailProvider(){
		detailProviderMap = new HashMap<String, DetailProvider>();
	}
	
	public static DiameterShowConfigDetailProvider getInstance() {
		return diameterShowConfigDetailProvider;
	}

	@Override
	public String execute(String[] parameters) {
		
		if (parameters == null || parameters.length == 0) {
			return getHelpMsg();
		}
		
		if (EliteBaseCommand.isHelpParameter(parameters[0])) {
			return getHelpMsg();
		}
		
		if (detailProviderMap.containsKey(parameters[0])) {
			return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));
		}
		
		return " Invalid Option: " + parameters[0] + getHelpMsg();
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage : show diameter "+CONFIG+" <options>");
		responseBuilder.append("\n\n---------------------------Possible Options:----------------------------\n");
		responseBuilder.append(PEER + "                  : Displays Configuration Details for All Peers\n");
		responseBuilder.append(PEER + " " + HOST_IDENTIY + "  : Displays Configuration Details for Specific Peer\n");
		responseBuilder.append(PEER_GROUP + "             : Displays Configuration Details for All Peer Groups\n");
		responseBuilder.append(PEER_GROUP + " " + PEER_GROUP_NAME + "  : Displays Configuration Details for Specific Peer Group\n");

		return responseBuilder.toString();
	}
	
	@Override
	public String getKey() {
		return CONFIG;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	@Override
	public String getHotkeyHelp() {
		return "'"+CONFIG+"':{'"+PEER+"':{'"+HELP+"':{}},'"+HELP+"':{},'"+PEER_GROUP+"':{'"+HELP+"':{}}}";
	}
	
}
