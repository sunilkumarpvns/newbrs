package com.elitecore.diameterapi.core.commands;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.mibs.config.DiameterConfigProvider;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;

public class DiameterPeerStatisticDetailProvider extends DetailProvider {

	private final static String PEER			= "peer";
	private static final String HOST_IDENTITY 	= "<Host Identity>";
	private final static String HELP			= "-help";
	private static final int TABLE_WIDTH 		=  76;
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private DiameterStatisticsProvider statisticProvider;
	private DiameterConfigProvider configProvider;
	public DiameterPeerStatisticDetailProvider(DiameterStatisticsProvider statisticProvider,
			DiameterConfigProvider configProvider) {
		this.configProvider = configProvider;
		this.detailProviderMap = new HashMap<String ,DetailProvider>();
		this.statisticProvider = statisticProvider;
	}

	@Override
	public String execute(String[] parameters) {
		
		if(parameters == null || parameters.length == 0)
			return getPeerStatisticSummary();
		
		if(isHelpSymbol(parameters[0])){
			return getPeerUsage();
		}
		if(CSV.equalsIgnoreCase(parameters[0]))
			return getPeerStatisticCSVSummary();
		
		return getPeerStatisticSummary(parameters[0]);
	}

	@Override
	public String getHelpMsg() {
		return StringUtility.fillChar(PEER, 20, ' ') + " : " + getDescription() + "\n";
	}
	
	public String getPeerUsage() {
		
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	  : show diameter statistic " + PEER + " ["+HOST_IDENTITY+"]");
		responseBuilder.append("\nDescription : "+getDescription() +"\n\n");
		responseBuilder.append("--------------------------Possible Options:--------------------------------\n");
		responseBuilder.append( HOST_IDENTITY + "  : Displays Diameter Peer Statistics for Specific Peer\n");
		responseBuilder.append( CSV + "             : Displays Diameter Peer Statistics of all Peers in CSV Format\n");
		return responseBuilder.toString();
	}
	
	@Override
	public String getHotkeyHelp() {
		return "'"+getKey()+"':{'"+CSV+"':{}, '"+HELP+"':{}}";
	}

	public String getDescription() {
		return "Displays Diameter Peer Statistics.";
	}

	@Override
	public String getKey() {
		return PEER;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	private String getPeerStatisticSummary(String hostIdentity) {
		
		DiameterPeerConfig peerConfig = configProvider.getPeerConfig(hostIdentity);
				
		if (peerConfig == null) {
			return "Peer: " + hostIdentity + " is not registered.";
		}
		
		TableFormatter formatter = new TableFormatter(
				new String[]{"PEER: " + hostIdentity +" ("+  
						configProvider.getPeerState(hostIdentity) +")"}, 
				new int[]{TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		
		GroupedStatistics statistics = statisticProvider.getPeerStatsMap().get(hostIdentity);
		if(statistics == null){
			formatter.add("No Statistics Available.", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		formatter.add(statistics.toString());
		formatter.add(getLegend());
		return formatter.getFormattedValues();
	}
	
	private String getPeerStatisticCSVSummary() {
		TableFormatter formatter = new TableFormatter(
				new String[]{"Diameter Peer Statistics"}, 
				new int[]{TABLE_WIDTH}, TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		
		Map<String, GroupedStatistics> peerStatisticsMap = statisticProvider.getPeerStatsMap();
		if(peerStatisticsMap.size() == 0){
			formatter.addRecord(new String[]{"No Statistics Available."});
			return formatter.getFormattedValues();
		}
		for(Map.Entry<String, GroupedStatistics> statsEntry : peerStatisticsMap.entrySet()){
			formatter.addNewLine();
			formatter.addRecord(new String[]{"PEER: " + statsEntry.getKey() +" ("+  configProvider.getPeerState( statsEntry.getKey()) +")"});
			formatter.add(statsEntry.getValue().toCSV());
		}
		return formatter.getFormattedValues();
	}

	private String getPeerStatisticSummary() {
		TableFormatter formatter = new TableFormatter(
				new String[]{"Diameter Peer Statistics"}, 
				new int[]{TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		
		Map<String, GroupedStatistics> peerStatisticsMap = statisticProvider.getPeerStatsMap();
		if(peerStatisticsMap.size() == 0){
			formatter.add("No Statistics Available.", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		for(Map.Entry<String, GroupedStatistics> statsEntry : peerStatisticsMap.entrySet()){
			formatter.addNewLine();
			formatter.add("PEER: " + statsEntry.getKey() +" ("+  configProvider.getPeerState( statsEntry.getKey()) +")", TableFormatter.CENTER);
			formatter.add(statsEntry.getValue().toString());
			formatter.addNewLine();
		}
		formatter.add(getLegend());
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

}