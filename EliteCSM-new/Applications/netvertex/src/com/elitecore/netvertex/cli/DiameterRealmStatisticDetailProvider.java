package com.elitecore.netvertex.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;
import com.elitecore.diameterapi.mibs.statistics.RealmIdentifier;

public class DiameterRealmStatisticDetailProvider extends DetailProvider {

	private final static String REALM			= "realm";
	private static final String REALM_NAME 		= "<Realm Name>";
	private final static String HELP			= "-help";
	private static final int TABLE_WIDTH 		= 76;
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private DiameterStatisticsProvider diameterStatisticProvider;
	public DiameterRealmStatisticDetailProvider(DiameterStatisticsProvider diameterStatisticProvider) {
		this.diameterStatisticProvider = diameterStatisticProvider;
		this.detailProviderMap = new HashMap<String ,DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {
		
		if(parameters == null || parameters.length == 0)
			return getRealmStatisticsSummary();
	
		if(CSV.equalsIgnoreCase(parameters[0])){
			return getRealmStatisticsCSVSummary();
		}
		if(isHelpSymbol(parameters[0])){
			return getRealmUsage();
		}
		return getRealmStatisticsSummary(parameters[0]);
	}

	@Override
	public String getHelpMsg() {
		return StringUtility.fillChar(REALM, 20, ' ') + " : " + getDescription() + "\n";
	}
	
	public String getRealmUsage() {
		
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	  : show diameter statistic realm ["+REALM_NAME+"]");
		responseBuilder.append("\nDescription : "+getDescription() +"\n\n");
		responseBuilder.append("----------------------------Possible Options:---------------------------------\n");
		responseBuilder.append( REALM_NAME + "  : Displays Diameter Realm Statistics for provided Realm Name\n");
		responseBuilder.append( CSV + "          : Displays Diameter Realm Statistics in CSV Format\n");
		return responseBuilder.toString();
	}
	
	@Override
	public String getHotkeyHelp() {
		return "'"+getKey()+"':{'"+CSV+"':{}, '"+HELP+"':{}}";
	}

	public String getDescription() {
		return "Displays Diameter Realm Statistics.";
	}

	@Override
	public String getKey() {
		return REALM;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	private String getRealmStatisticsSummary() {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{"Diameter Realm Statistics"}, 
				new int[]{TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		Map<RealmIdentifier, GroupedStatistics> realmStatisticsMap = diameterStatisticProvider.getRealmStatsMap();
		if(realmStatisticsMap.size() == 0){
			formatter.add("No Statistics Available.", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		for(Entry<RealmIdentifier, GroupedStatistics> statsEntry : realmStatisticsMap.entrySet()){
			RealmIdentifier realmIdentifier = statsEntry.getKey();
			formatter.addNewLine();
			formatter.add("REALM: "+ realmIdentifier.getDbpRealmMessageRouteRealm(), TableFormatter.CENTER);
			formatter.add("Application: " + 
					realmIdentifier.getDbpRealmMessageRouteApp() + 
					"(" + realmIdentifier.getDbpRealmMessageRouteType() + ")" 
					, TableFormatter.LEFT);
			formatter.add("Route-Action: " + 
					realmIdentifier.getDbpRealmMessageRouteAction()
					, TableFormatter.LEFT);
			formatter.add(statsEntry.getValue().toString());
			formatter.addNewLine();
		}
		formatter.add(getLegend());
		return formatter.getFormattedValues();
	}
	
	private String getRealmStatisticsCSVSummary() {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{"Diameter Realm Statistics"}, 
				new int[]{TABLE_WIDTH}, TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		
		Map<RealmIdentifier, GroupedStatistics> realmStatisticsMap = diameterStatisticProvider.getRealmStatsMap();
		if(realmStatisticsMap.size() == 0){
			formatter.addRecord(new String[]{"No Statistics Available."});
			return formatter.getFormattedValues();
		}
		for(Entry<RealmIdentifier, GroupedStatistics> statsEntry : realmStatisticsMap.entrySet()){
			RealmIdentifier realmIdentifier = statsEntry.getKey();
			formatter.addNewLine();
			formatter.addRecord(new String[]{"REALM: "+ realmIdentifier.getDbpRealmMessageRouteRealm()});
			formatter.addRecord(new String[]{"Application: " + 
					realmIdentifier.getDbpRealmMessageRouteApp() + 
					"(" + realmIdentifier.getDbpRealmMessageRouteType() + ")"});
			formatter.addRecord(new String[]{"Route-Action: " + 
					realmIdentifier.getDbpRealmMessageRouteAction()});
			formatter.add(statsEntry.getValue().toCSV());
		}
		return formatter.getFormattedValues();
	}

	private String getRealmStatisticsSummary(String realmName) {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{"Diameter Realm Statistics"}, 
				new int[]{TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		boolean realmExists = false;
		for(Entry<RealmIdentifier, GroupedStatistics> statsEntry : diameterStatisticProvider.getRealmStatsMap().entrySet()){
			if(statsEntry.getKey().getDbpRealmMessageRouteRealm().equals(realmName)){
				RealmIdentifier realmIdentifier = statsEntry.getKey();
				formatter.addNewLine();
				formatter.add("REALM: "+ realmIdentifier.getDbpRealmMessageRouteRealm(), TableFormatter.CENTER);
				formatter.add("Application: " + 
						realmIdentifier.getDbpRealmMessageRouteApp() + 
						"(" + realmIdentifier.getDbpRealmMessageRouteType() + ")" 
						, TableFormatter.LEFT);
				formatter.add("Route-Action: " + 
						realmIdentifier.getDbpRealmMessageRouteAction()
						, TableFormatter.LEFT);
				formatter.add(statsEntry.getValue().toString());
				formatter.addNewLine();
				realmExists = true;
			}
		}
		if(realmExists){
			formatter.add(getLegend());
		}else{
			formatter.add("No Statistics Available.", TableFormatter.LEFT);
		}
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
