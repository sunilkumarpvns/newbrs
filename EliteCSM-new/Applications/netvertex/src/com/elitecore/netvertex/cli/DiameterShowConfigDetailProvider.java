package com.elitecore.netvertex.cli;

import java.util.HashMap;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;

public class DiameterShowConfigDetailProvider extends DetailProvider {
	
	private static final String CONFIG 		= "config";
	private final static String HELP		= "-help";
	private final static String PEER		= "peer";
	private final static String HOST_IDENTIY	= "<Host Identity>";

	private HashMap<String ,DetailProvider> detailProviderMap;
	private DiameterConfigProvider  configProvider;

	public DiameterShowConfigDetailProvider(DiameterConfigProvider provider) {
		detailProviderMap = new HashMap<String ,DetailProvider>();
		configProvider = provider;
	}

	@Override
	public String execute(String[] parameters) {
		
		if(parameters == null || parameters.length == 0){
			return getHelpMsg();
		}
		if(PEER.equalsIgnoreCase(parameters[0])){
			if(parameters.length == 1){
				return configProvider.getAllPeerConfigSummary();
			}
			if(HELP.equalsIgnoreCase(parameters[1])){
				return getPeerUsage();
			}
			return configProvider.getPeerConfigSummary(parameters[1]);
		}
		return getHelpMsg();
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage : show diameter "+CONFIG+" <options>");
		responseBuilder.append("\n\n---------------------------Possible Options:----------------------------\n");
		responseBuilder.append(PEER + "                  : Displays Configuration Details for All Peers\n");
		responseBuilder.append(PEER + " " + HOST_IDENTIY + "  : Displays Configuration Details for Specefic Peer\n");
		return responseBuilder.toString();
	}
	
	private String getPeerUsage() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	 : show diameter "+CONFIG+ " " + PEER + " ["+HOST_IDENTIY+"]");
		responseBuilder.append("\nDescription: Displays Configuration Details of All Peers.\n(If provided with Host Identity, displays details of that Peer.)");
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
		return "'"+CONFIG+"':{'"+PEER+"':{'"+HELP+"':{}},'"+HELP+"':{}}";
	}
	
}
