package com.elitecore.netvertex.cli;

import java.util.HashMap;
import java.util.Set;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticResetter;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;

public class DiameterClearApplicationStatisticDetailProvider extends DetailProvider {

	private static final String ALL = "all";
	private static final String PEER = "peer";
	private static final String HELP = "-help";
	/**
	 * DO NOT USE THIS KEY. AS ITS JUST A WRAP UP KEY FOR ALL APPLICATIONS.
	 */
	private static final String key  = "app";
	private static final String HOST_IDENTITY = "<Host Identity>";
	
	private final static String SUCCESS_MSG		= "Cleared Statistics successfully.\n";
	private final static String FAILURE_MSG		= "Unable to Clear Statistics.\n";
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private DiameterStatisticResetter  diameterStatisticResetter;
	private DiameterStatisticsProvider diameterStatisticsProvider;
	
	public DiameterClearApplicationStatisticDetailProvider(
			DiameterStatisticResetter  diameterStatisticResetter,
			DiameterStatisticsProvider diameterStatisticsProvider) {
		this.diameterStatisticsProvider = diameterStatisticsProvider;
		this.detailProviderMap = new HashMap<String, DetailProvider>();
		this.diameterStatisticResetter = diameterStatisticResetter;
	}
	@Override
	public String execute(String[] parameters) {
		if(parameters != null){
			if(parameters.length == 1){
				if(diameterStatisticResetter.resetApplicationStatistics(parameters[0]))
					return SUCCESS_MSG;
			}
			if(parameters.length >= 2){
				if(ALL.equalsIgnoreCase(parameters[1])){
					if(diameterStatisticResetter.resetApplicationStatistics(parameters[0]))
						return SUCCESS_MSG;
				}else if(PEER.equalsIgnoreCase(parameters[1])){
					if(parameters.length == 2){
						if(diameterStatisticResetter.resetApplicationAllPeerStatistics(parameters[0]))
							return SUCCESS_MSG;
						return FAILURE_MSG;
					}
					if(isHelpSymbol(parameters[2])){
						return getPeerUsage(parameters[0]);
					}
					if(diameterStatisticResetter.resetApplicationPeerStatistics(parameters[0], parameters[2]))
						return SUCCESS_MSG;
					return FAILURE_MSG;
				}else{
					return getApplicationHelp(parameters[0]);
				}
			}
		}
		return "Invalid/Missing Parameters\n" + getHelpMsg();
	}
	
	private String getPeerUsage(String application) {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	 : clear diameter statistic " + application + " " + PEER + " " + HOST_IDENTITY);
		responseBuilder.append("\nDescription: Clears Statistic Details of All Peers of "+application+
				".(If provided with Host Identity, clears details of that peer.)");
		return responseBuilder.toString();
	}

	@Override
	public String getHelpMsg() {
		
		StringBuilder helpMessage = new StringBuilder();
		Set<String> applicationSet = diameterStatisticsProvider.getApplicationsSet();
		for(String appKey : applicationSet){
			helpMessage.append(getApplicationDescription(appKey));
		}
		return helpMessage.toString();
	}

	private String getApplicationDescription(String appKey) {
		return StringUtility.fillChar(appKey, 30, ' ')+" : Clear Diameter Statistics of " + appKey + " Application.\n";
	}
	private String getApplicationHelp(String appKey) {
		
		StringBuilder help = new StringBuilder();
		help.append("\nUsage: clear diameter statistic "+ appKey + " [<options>]\n");
		help.append("\n-------------------------------------------------Possible Options:---------------------------------------------------\n");
		help.append("\t"+ ALL + "                  : Clears Diameter Statistics\n");
		help.append("\t"+ PEER + "                 : Clears Diameter Statistics of All Peers\n");
		help.append("\t" + PEER + " " + HOST_IDENTITY + " : Clears Diameter Statistics of Specific Peer\n");
		
		return help.toString();
	}
	
	/**
	 * DO NOT USE THIS KEY. AS ITS JUST A WRAP UP KEY FOR ALL DIAMETER APPLICATIONS.
	 */
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	@Override
	public String getHotkeyHelp() {

		StringBuilder hotKeyHelp = new StringBuilder();
		boolean firstKey = true;
		Set<String> applicationSet = diameterStatisticsProvider.getApplicationsSet();
		for(String appKey : applicationSet){
			if(firstKey == false){
				hotKeyHelp.append(",");
			}
			hotKeyHelp.append(getHotKeyHelpForApplication(appKey));
			firstKey = false;
		}
		return hotKeyHelp.toString();
	}
	private Object getHotKeyHelpForApplication(String appKey) {
		return "'"+appKey+"':{'"+HELP+"':{},'"+PEER+"':{'"+HELP+"':{}},'"+ALL+"':{}}";
	}
}
