package com.elitecore.aaa.diameter.util.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.statistics.ApplicationStatsIdentifier;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;

public class DiameterApplicationDetailProvider extends DetailProvider{

	private static final String ALL = "all";
	private static final String PEER = "peer";
	private static final String HELP = "-help";
	private static final String key  = "app";
	private static final String HOST_IDENTITY = "<Host Identity>";
	private static final int TABLE_WIDTH = 76;
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private DiameterStatisticsProvider statisticProvider;
	private DiameterConfigProvider configProvider;
	public DiameterApplicationDetailProvider(DiameterStatisticsProvider statisticProvider, DiameterConfigProvider configProvider) {
		this.configProvider = configProvider;
		this.detailProviderMap = new HashMap<String, DetailProvider>();
		this.statisticProvider = statisticProvider;
	}
	@Override
	public String execute(String[] parameters) {
		if(parameters != null){
			if(parameters.length == 1){
				return getApplicationSummary(parameters[0]);
			}
			if(parameters.length >= 2){
				if(CSV.equalsIgnoreCase(parameters[1])){
					return getApplicationCSVSummary(parameters[0]);
				}
				if(ALL.equalsIgnoreCase(parameters[1])){
					return getApplicationSummary(parameters[0]);
				}else if(PEER.equalsIgnoreCase(parameters[1])){
					if(parameters.length == 2)
						return getApplicationPeerSummary(parameters[0]);
					
					if(isHelpSymbol(parameters[2])){
						return getPeerUsage(parameters[0]);
					}
					if(CSV.equalsIgnoreCase(parameters[2])){
						return getApplicationPeerCSVSummary(parameters[0]);
					}
					return getApplicationPeerSummary(parameters[0], parameters[2]);
				}else{
					return getApplicationHelp(parameters[0]);
				}
			}
		}
		return "Invalid/Missing Parameters\n" + getHelpMsg();
	}
	
	private String getPeerUsage(String application) {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	  : show diameter statistic app " + application + " " + PEER + " ["+HOST_IDENTITY+"]");
		responseBuilder.append("\nDescription : Displays Diameter Statistics of All Peers.");
		responseBuilder.append("\n--------------------------Possible Options:--------------------------------\n");
		responseBuilder.append( HOST_IDENTITY + "  : Displays Diameter Peer Statistics for Specific Peer\n");
		responseBuilder.append( CSV + "             : Displays Diameter Peer Statistics of all Peers in CSV Format\n");
		return responseBuilder.toString();
	
	}

	@Override
	public String getHelpMsg() {
		
		StringBuilder helpMessage = new StringBuilder();
		Set<String> applicationSet = statisticProvider.getApplicationsSet();
		for(String appKey : applicationSet){
			helpMessage.append(getApplicationDescription(appKey));
		}
		return helpMessage.toString();
	}

	private String getApplicationDescription(String appKey) {
		return StringUtility.fillChar(getKey() + " " + appKey, 20, ' ')+" : Displays Diameter Statistics of " + appKey + " Application.\n";
	}
	private String getApplicationHelp(String appKey) {
		
		StringBuilder help = new StringBuilder();
		help.append("\nUsage: show diameter statistic app "+ appKey + " [<options>]\n");
		help.append("\n-------------------------------Possible Options:---------------------------------\n");
		help.append(ALL + "                  : Displays Diameter Statistics of "+appKey+" Application.\n");
		help.append(CSV + "                 : CSV Format of Diameter Statistics of "+appKey+" Application.\n");
		help.append(PEER + "                 : Displays Diameter Statistics of All Peers.\n");
		help.append(PEER + " " + CSV + "            : Displays Diameter Statistics of All Peers.\n");
		help.append(PEER + " " + HOST_IDENTITY + " : Displays Diameter Statistics of Specific Peer.\n");
		
		return help.toString();
	}
	
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
		hotKeyHelp.append("'"+key+"':{");
		boolean firstKey = true;
		Set<String> applicationSet = statisticProvider.getApplicationsSet();
		for(String appKey : applicationSet){
			if(firstKey == false){
				hotKeyHelp.append(",");
			}
			hotKeyHelp.append(getHotKeyHelpForApplication(appKey));
			firstKey = false;
		}
		hotKeyHelp.append("}");
		return hotKeyHelp.toString();
	}
	private String getHotKeyHelpForApplication(String appKey) {
		return "'"+appKey+"':{'"+CSV+"':{}, '"+HELP+"':{},'"+PEER+"':{ '"+CSV+"':{},'"+HELP+"':{}},'"+ALL+"':{}}";
	}
	
	public String getApplicationSummary(String applicationStr) {

		TableFormatter formatter = new TableFormatter(
				new String[]{}, 
				new int[]{TABLE_WIDTH}, TableFormatter.NO_BORDERS);
		
		boolean applicationFound = false;
		for(Map.Entry<ApplicationStatsIdentifier, GroupedStatistics> appEntry: statisticProvider.getApplicationMap().entrySet()){
			if(appEntry.getKey().getApplication().equalsIgnoreCase(applicationStr) || 
					String.valueOf(appEntry.getKey().getApplicationId()).equals(applicationStr)){

				formatter.addNewLine();
				formatter.add(appEntry.getKey().toString(), TableFormatter.CENTER);
				formatter.addNewLine();
				formatter.add(appEntry.getValue().toString());
				formatter.addNewLine();
				applicationFound = true;
			}
		}
		if(applicationFound)
			formatter.add(getLegend());
		else
			formatter.add("No Statistics Available", TableFormatter.LEFT);
		return formatter.getFormattedValues();
	}
	
	public String getApplicationCSVSummary(String applicationStr) {

		TableFormatter formatter = new TableFormatter(
				new String[]{}, 
				new int[]{TABLE_WIDTH}, TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		
		boolean applicationFound = false;
		for(Map.Entry<ApplicationStatsIdentifier, GroupedStatistics> appEntry: statisticProvider.getApplicationMap().entrySet()){
			if(appEntry.getKey().getApplication().equalsIgnoreCase(applicationStr) || 
					String.valueOf(appEntry.getKey().getApplicationId()).equals(applicationStr)){

				formatter.addRecord(new String[]{appEntry.getKey().toString()});
				formatter.add(appEntry.getValue().toCSV());
				applicationFound = true;
			}
		}
		if(!applicationFound)
			formatter.addRecord(new String[]{"No Statistics Available"});
		return formatter.getFormattedValues();
	}
	
	private String getLegend(){
		StringBuilder responseBuilder = new StringBuilder();
		
		responseBuilder.append(StringUtility.fillChar("", TABLE_WIDTH+2, '-'));
		responseBuilder.append("\n  Rx  : Received     ||   Tx  : Transmitted   ||   Rt  : Retransmitted"); 
		responseBuilder.append("\n  To  : Timeout      ||   Dr  : Dropped       ||   Un  : Unknown");
		responseBuilder.append("\n  Du  : Duplicate    ||   Mf  : Malformed     ||   Pn  : Pending" );
		responseBuilder.append("\n" + StringUtility.fillChar("", TABLE_WIDTH+2, '-') + "\n");
		return responseBuilder.toString();
	}
	
	private String getApplicationPeerSummary(String applicationStr, String hostIdentity) {
		TableFormatter formatter = new TableFormatter(
				new String[]{}, 
				new int[]{TABLE_WIDTH}, TableFormatter.NO_BORDERS);
		boolean appFound = false;
		Map<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> applicationPeerStatisticsMap = statisticProvider.getApplicationPeerMap();
		for(ApplicationStatsIdentifier appStatsIdentifier : applicationPeerStatisticsMap.keySet()){
			if(appStatsIdentifier.getApplication().equalsIgnoreCase(applicationStr) || 
					String.valueOf(appStatsIdentifier.getApplicationId()).equals(applicationStr)){

				formatter.addNewLine();
				formatter.add(getApplicationPeerSummary(appStatsIdentifier, hostIdentity));
				appFound = true;
			}
		}
		if(!appFound){
			formatter.add("No Statistics Available for Peer: " + hostIdentity, TableFormatter.LEFT);
		}else{
			formatter.add(getLegend());
		}
		return formatter.getFormattedValues();
	}

	private String getApplicationPeerSummary(ApplicationStatsIdentifier applicationStatsId, String hostIdentity) {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{applicationStatsId.toString()}, 
				new int[]{TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		Map<String, GroupedStatistics> appPeerStatsMap = statisticProvider.getApplicationPeerMap().get(applicationStatsId);

		if(appPeerStatsMap == null){
			formatter.add("No Statistics Available." , TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		GroupedStatistics statistics = appPeerStatsMap.get(hostIdentity);
		if(statistics == null){
			formatter.add("No Statistics Available.", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		formatter.addNewLine();
		formatter.add("PEER: " + hostIdentity + 
				" (" + configProvider.getPeerState(hostIdentity) + ")", 
				TableFormatter.CENTER);
		formatter.addNewLine();
		formatter.add(statistics.toString());
		return formatter.getFormattedValues();
	}

	private String getApplicationPeerSummary(String applicationStr) {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{""}, 
				new int[]{TABLE_WIDTH}, TableFormatter.NO_BORDERS);
		Map<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> applicationPeerStatisticsMap = statisticProvider.getApplicationPeerMap();
		if(applicationPeerStatisticsMap.size() == 0){
			formatter.add("No Peer Statistics Available", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		for(ApplicationStatsIdentifier appStatsIdentifier : applicationPeerStatisticsMap.keySet()){
			if(appStatsIdentifier.getApplication().equalsIgnoreCase(applicationStr)  || 
					String.valueOf(appStatsIdentifier.getApplicationId()).equals(applicationStr)){
				formatter.add(getApplicationPeerSummary(appStatsIdentifier));
			}
		}
		formatter.add(getLegend());
		return formatter.getFormattedValues();
		
	}
	
	private String getApplicationPeerCSVSummary(String applicationStr) {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{""}, 
				new int[]{TABLE_WIDTH}, TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		
		Map<ApplicationStatsIdentifier, Map<String, GroupedStatistics>> applicationPeerStatisticsMap = statisticProvider.getApplicationPeerMap();
		if(applicationPeerStatisticsMap.size() == 0){
			formatter.addRecord(new String[]{"No Peer Statistics Available"});
			return formatter.getFormattedValues();
		}
		for(ApplicationStatsIdentifier appStatsIdentifier : applicationPeerStatisticsMap.keySet()){
			if(appStatsIdentifier.getApplication().equalsIgnoreCase(applicationStr)  || 
					String.valueOf(appStatsIdentifier.getApplicationId()).equals(applicationStr)){
				formatter.add(getApplicationPeerCSVSummary(appStatsIdentifier));
			}
		}
		return formatter.getFormattedValues();
		
	}
	
	private String getApplicationPeerSummary(ApplicationStatsIdentifier applicationStatsId) {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{applicationStatsId.toString()}, 
				new int[]{TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		Map<String, GroupedStatistics> appPeerStatsMap = statisticProvider.getApplicationPeerMap().get(applicationStatsId);

		if(appPeerStatsMap == null || appPeerStatsMap.size() == 0){
			formatter.add("No Peer Statistics Available." , TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		for(Map.Entry<String, GroupedStatistics> statsEntry : appPeerStatsMap.entrySet()){
			formatter.addNewLine();
			formatter.add("PEER: " + statsEntry.getKey() + 
					" (" + configProvider.getPeerState(statsEntry.getKey()) + ")", 
					TableFormatter.CENTER);
			formatter.addNewLine();
			formatter.add(statsEntry.getValue().toString());
			formatter.addNewLine();
		}
		return formatter.getFormattedValues();
		
	}
	
	private String getApplicationPeerCSVSummary(ApplicationStatsIdentifier applicationStatsId) {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{applicationStatsId.toString()}, 
				new int[]{TABLE_WIDTH}, TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		Map<String, GroupedStatistics> appPeerStatsMap = statisticProvider.getApplicationPeerMap().get(applicationStatsId);

		if(appPeerStatsMap == null || appPeerStatsMap.size() == 0){
			formatter.addRecord(new String[]{"No Peer Statistics Available."});
			return formatter.getFormattedValues();
		}
		for(Map.Entry<String, GroupedStatistics> statsEntry : appPeerStatsMap.entrySet()){
			formatter.addNewLine();
			formatter.addRecord(new String[]{"PEER: " + statsEntry.getKey() + 
					" (" + configProvider.getPeerState(statsEntry.getKey()) + ")"});
			formatter.add(statsEntry.getValue().toCSV());
		}
		return formatter.getFormattedValues();
		
	}

	
	
}
