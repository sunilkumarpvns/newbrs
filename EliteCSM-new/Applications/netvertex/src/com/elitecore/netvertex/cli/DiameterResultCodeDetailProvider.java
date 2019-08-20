package com.elitecore.netvertex.cli;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.mibs.statistics.ApplicationStatsIdentifier;
import com.elitecore.diameterapi.mibs.statistics.CCResultCodeTuple;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;
import com.elitecore.diameterapi.mibs.statistics.RealmIdentifier;
import com.elitecore.diameterapi.mibs.statistics.ResultCodeTuple;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DiameterResultCodeDetailProvider extends DetailProvider {

	public static final String RC 					= "rc";
	private static final String PEER 				= "peer";
	private static final String REALM 				= "realm";
	private static final String ALL 				= "all";
	private static final String CMD 				= "cmd";
	private static final String APP 				= "app";
	private static final String HELP 				= "-help";
	private static final String HOST_IDENTITY 		= "<Host Identity>";
	private static final String REALM_NAME 			= "<Realm Name>";
	private static final String APP_NAME 			= "<App-Name>";
	private static final String COMMAND_CODE		= "<Command-Code>";
	private static final int RC_TABLE_WIDTH 		= 55;
	private HashMap<String, DetailProvider> detailProviderMap;
	private DiameterStatisticsProvider diameterStatisticProvider;


	public DiameterResultCodeDetailProvider(DiameterStatisticsProvider diameterStatisticProvider){
		this.diameterStatisticProvider = diameterStatisticProvider;
		detailProviderMap = new HashMap<String ,DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {

		if(parameters == null || parameters.length == 0)
			return getStackResultCodeSummary();
		
		if(parameters.length >= 1){
			if(parameters.length == 1 && ALL.equalsIgnoreCase(parameters[0])){
				return getStackResultCodeSummary();
			}
			if(PEER.equalsIgnoreCase(parameters[0])){
				if(parameters.length == 1)
					return getPeerResultCodeSummay();
				return getPeerResultCodeSummay(parameters[1]);
			}
			if(REALM.equalsIgnoreCase(parameters[0])){
				if(parameters.length == 1)
					return getRealmResultCodeSummay();
				return getRealmResultCodeSummay(parameters[1]);
			}
			if(CMD.equalsIgnoreCase(parameters[0])){
				if(parameters.length > 1)
					return getCommandCodeWiseSummery(parameters[1]);
				else 
					getResultCodeUsage();
			}
			if (APP.equalsIgnoreCase(parameters[0])) {
				if(parameters.length > 1)
					return getApplicationResultCodeSummary(parameters[1]);
		}
		}
		return getResultCodeUsage();
	}

	@Override
	public String getHelpMsg() {

		return StringUtility.fillChar(RC, 20, ' ')+ " : " + getDescription() + "\n";
	}
	
	public String getResultCodeUsage() {

		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage       : show diameter statistic rc [<options>]");
		responseBuilder.append("\nDescription : "+getDescription() +"\n\n");
		responseBuilder.append("-----------------------------Possible Options:----------------------------------\n");
		responseBuilder.append(StringUtility.fillChar(ALL, 20, ' ')+" : Displays Result Code Statistics of Stack.\n");
		responseBuilder.append(StringUtility.fillChar(PEER, 20, ' ')+" : Displays Result Code Statistics of All Peer.\n");
		responseBuilder.append(StringUtility.fillChar(PEER + " " + HOST_IDENTITY, 20, ' ')+" : Displays Result Code Statistics of Specific Peer.\n");
		responseBuilder.append(StringUtility.fillChar(REALM, 20, ' ')+" : Displays Result Code Statistics of All Realm.\n");
		responseBuilder.append(StringUtility.fillChar(REALM + " " + REALM_NAME, 20, ' ')+" : Displays Result Code Statistics of Specific Realm.\n");
		responseBuilder.append(StringUtility.fillChar(APP + " " + APP_NAME, 20, ' ')+" : Displays Result Code Statistics of Specific Application.\n");
		responseBuilder.append(StringUtility.fillChar(CMD + " " + COMMAND_CODE, 20, ' ')+" : Displays Result Code Statistics of Specific Command-Code.\n");
		return responseBuilder.toString();
	}
	
	@Override
	public String getKey() {
		return RC;
	}


	@Override
	public String getDescription(){
		return "Displays Diameter Result Code Statistics.";
	}

	@Override
	public String getHotkeyHelp() {
		
		StringBuilder commandHotkeyBuilder = new StringBuilder();
		commandHotkeyBuilder.append("'" + CMD +"':{");
		CommandCode[] commandCodes = CommandCode.VALUES;
		for (int i=0 ; i<commandCodes.length ; i++) {
			commandHotkeyBuilder.append("'" + commandCodes[i].displayName+"':{}, ");
		}
		String cmdHotKey = commandHotkeyBuilder.substring(0, commandHotkeyBuilder.length() -2) + "}, ";
		
		StringBuilder appHotkeyBuilder = new StringBuilder();
		appHotkeyBuilder.append("'" + APP+"':{");
		Set<String> applicationSet = diameterStatisticProvider.getApplicationsSet();
		String appHotKey = null;
		if(CollectionUtils.isNotEmpty(applicationSet)){
			for(String appKey : applicationSet){
				appHotkeyBuilder.append(getHotKeyPattern(appKey) + ", ");
			}		
			appHotKey = appHotkeyBuilder.substring(0, appHotkeyBuilder.length() -2) + "}, ";
		} else {
			appHotKey = appHotkeyBuilder.toString() + "},";
		}
		
		
		StringBuilder hotKeyHelp = new StringBuilder();
		hotKeyHelp.append(cmdHotKey);
		hotKeyHelp.append(appHotKey);
		hotKeyHelp.append(getHotKeyPattern(HELP));
		hotKeyHelp.append(",");
		hotKeyHelp.append(getHotKeyPattern(ALL));
		hotKeyHelp.append(",");
		hotKeyHelp.append(getHotKeyPattern(PEER));
		hotKeyHelp.append(",");
		hotKeyHelp.append(getHotKeyPattern(REALM));
		return "'"+getKey()+"':{"+hotKeyHelp.toString()+"}";
	}
	
	private String getHotKeyPattern(String key){
		return "'" + key + "':{}";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	private String getStackResultCodeSummary() {
		return diameterStatisticProvider.getStackStatistics().getResultCodeStatisticSummary() + "\n" + getResultCodeLegend();
	}
	
	private String getCommandCodeWiseSummery(String strCommandCode) {
		
		if (strCommandCode == null) {
			return getResultCodeUsage();
		}

		int iCommandCode = -1;
		try {
			iCommandCode = Integer.parseInt(strCommandCode);
		} catch (NumberFormatException e) {
			CommandCode commandCode = CommandCode.fromDisplayName(strCommandCode);
			if (commandCode != null) {
				iCommandCode = commandCode.code;
			}
		}

		TableFormatter formatter = new TableFormatter(new String[]{"Command-Wise ResultCode Statistics"}, new int[]{RC_TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		if (iCommandCode == -1) {
			formatter.add("Invalid Command-Code.", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		} 
		
		Map<Integer, ResultCodeTuple> resultCodeStats = diameterStatisticProvider.getStackStatistics().getCmdWiseResultCodeStatistic(iCommandCode);
		if (resultCodeStats == null || resultCodeStats.isEmpty()) {
			formatter.add("No Result Code Statistics Available.", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		
		
		return getFormatedResultCodeStats(formatter, resultCodeStats, iCommandCode);
	}
	
	public String getFormatedResultCodeStats(TableFormatter formatter, Map<Integer, ResultCodeTuple> resultCodeStats, int commandCode) {
		TableFormatter formatter1 = new TableFormatter(
				new String[]{"ResultCode", "", "Rx", "Tx"},	
				new int[]{20, 6, 12, 12}, 
				new int[]{TableFormatter.LEFT, TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.RIGHT}, 
				TableFormatter.NO_BORDERS);
		formatter1.addNewLine();
		
		if (commandCode == CommandCode.CREDIT_CONTROL.code) {
			addCCResultCodeStatisticSummary(formatter1, resultCodeStats);
		} else {
			addResultCodeStatisticSummary(formatter1, resultCodeStats);
		}
		formatter.add(formatter1.getFormattedValues());
		return formatter1.getFormattedValues();
	}

	private void addCCResultCodeStatisticSummary(TableFormatter formatter, Map<Integer, ResultCodeTuple> resultCodeStats) {
		
		TreeSet<Integer> resultCodeSet= new TreeSet<Integer>(resultCodeStats.keySet());
		int isEmpty = resultCodeSet.size();
		if(isEmpty == 0){
			formatter.add("No Result Code Details Available.", TableFormatter.LEFT);
		}
		
		formatter.add("Command: CC", TableFormatter.LEFT);
		int category = 0;
		while(isEmpty != 0){

			if(resultCodeSet.subSet(category+1, category + 1000).isEmpty() == false){
				ResultCodeCategory resultCodeCategory = ResultCodeCategory.getResultCodeCategory(category);
				CCResultCodeTuple tuple = (CCResultCodeTuple) resultCodeStats.get(category);
				formatter.addRecord(new String[]{
						resultCodeCategory.category, 
						resultCodeCategory.categoryType,
						getDisplayValue(tuple.getResultCodeIn(), 12), 
						getDisplayValue(tuple.getResultCodeOut(), 12)  });
				formatter.add("----------------------------------------------------------\n");
				isEmpty--;
				for(int resultCode : resultCodeSet.subSet(category+1, category + 1000)){

					tuple = (CCResultCodeTuple) resultCodeStats.get(resultCode);
					formatter.addRecord(new String[]{
							String.valueOf(resultCode), 
							"",
							getDisplayValue(tuple.getResultCodeIn(), 12), 
							getDisplayValue(tuple.getResultCodeOut(), 12)  });
					isEmpty--;
				}
				if(isEmpty != 0)
					formatter.addNewLine();
			}
			category += 1000;
		}
		
		formatter.addNewLine();
		formatter.add("Command: CC-Initial", TableFormatter.LEFT);
		category = 0;
		isEmpty = resultCodeSet.size();
		while(isEmpty != 0){

			if(resultCodeSet.subSet(category+1, category + 1000).isEmpty() == false){
				ResultCodeCategory resultCodeCategory = ResultCodeCategory.getResultCodeCategory(category);
				CCResultCodeTuple tuple = (CCResultCodeTuple) resultCodeStats.get(category);
				formatter.addRecord(new String[]{
						resultCodeCategory.category, 
						resultCodeCategory.categoryType,
						getDisplayValue(tuple.getInitialResultCodeIn(), 12), 
						getDisplayValue(tuple.getInitialResultCodeOut(), 12)  });
				formatter.add("----------------------------------------------------------\n");
				isEmpty--;
				for(int resultCode : resultCodeSet.subSet(category+1, category + 1000)){

					tuple = (CCResultCodeTuple) resultCodeStats.get(resultCode);
					formatter.addRecord(new String[]{
							String.valueOf(resultCode), 
							"",
							getDisplayValue(tuple.getInitialResultCodeIn(), 12), 
							getDisplayValue(tuple.getInitialResultCodeOut(), 12)  });
					isEmpty--;
				}
				if(isEmpty != 0)
					formatter.addNewLine();
			}
			category += 1000;
		}
		
		formatter.addNewLine();
		formatter.add("Command: CC-Update", TableFormatter.LEFT);
		category = 0;
		isEmpty = resultCodeSet.size();
		while(isEmpty != 0){

			if(resultCodeSet.subSet(category+1, category + 1000).isEmpty() == false){
				ResultCodeCategory resultCodeCategory = ResultCodeCategory.getResultCodeCategory(category);
				CCResultCodeTuple tuple = (CCResultCodeTuple) resultCodeStats.get(category);
				formatter.addRecord(new String[]{
						resultCodeCategory.category, 
						resultCodeCategory.categoryType,
						getDisplayValue(tuple.getUpdateResultCodeIn(), 12), 
						getDisplayValue(tuple.getUpdateResultCodeOut(), 12)  });
				formatter.add("----------------------------------------------------------\n");
				isEmpty--;
				for(int resultCode : resultCodeSet.subSet(category+1, category + 1000)){

					tuple = (CCResultCodeTuple) resultCodeStats.get(resultCode);
					formatter.addRecord(new String[]{
							String.valueOf(resultCode), 
							"",
							getDisplayValue(tuple.getUpdateResultCodeIn(), 12), 
							getDisplayValue(tuple.getUpdateResultCodeOut(), 12)  });
					isEmpty--;
				}
				if(isEmpty != 0)
					formatter.addNewLine();
			}
			category += 1000;
		}
		
		formatter.addNewLine();
		formatter.add("Command: CC-Terminate", TableFormatter.LEFT);
		category = 0;
		isEmpty = resultCodeSet.size();
		while(isEmpty != 0){

			if(resultCodeSet.subSet(category+1, category + 1000).isEmpty() == false){
				ResultCodeCategory resultCodeCategory = ResultCodeCategory.getResultCodeCategory(category);
				CCResultCodeTuple tuple = (CCResultCodeTuple) resultCodeStats.get(category);
				formatter.addRecord(new String[]{
						resultCodeCategory.category, 
						resultCodeCategory.categoryType,
						getDisplayValue(tuple.getTerminateResultCodeIn(), 12), 
						getDisplayValue(tuple.getTerminateResultCodeOut(), 12)  });
				formatter.add("----------------------------------------------------------\n");
				isEmpty--;
				for(int resultCode : resultCodeSet.subSet(category+1, category + 1000)){

					tuple = (CCResultCodeTuple) resultCodeStats.get(resultCode);
					formatter.addRecord(new String[]{
							String.valueOf(resultCode), 
							"",
							getDisplayValue(tuple.getTerminateResultCodeIn(), 12), 
							getDisplayValue(tuple.getTerminateResultCodeOut(), 12)  });
					isEmpty--;
				}
				if(isEmpty != 0)
					formatter.addNewLine();
			}
			category += 1000;
		}
		
		formatter.addNewLine();
		formatter.add("Command: CC-Other", TableFormatter.LEFT);
		category = 0;
		isEmpty = resultCodeSet.size();
		while(isEmpty != 0){

			if(resultCodeSet.subSet(category+1, category + 1000).isEmpty() == false){
				ResultCodeCategory resultCodeCategory = ResultCodeCategory.getResultCodeCategory(category);
				CCResultCodeTuple tuple = (CCResultCodeTuple) resultCodeStats.get(category);
				formatter.addRecord(new String[]{
						resultCodeCategory.category, 
						resultCodeCategory.categoryType,
						getDisplayValue(tuple.getOtherResultCodeIn(), 12), 
						getDisplayValue(tuple.getOtherResultCodeOut(), 12)  });
				formatter.add("----------------------------------------------------------\n");
				isEmpty--;
				for(int resultCode : resultCodeSet.subSet(category+1, category + 1000)){

					tuple = (CCResultCodeTuple) resultCodeStats.get(resultCode);
					formatter.addRecord(new String[]{
							String.valueOf(resultCode), 
							"",
							getDisplayValue(tuple.getOtherResultCodeIn(), 12), 
							getDisplayValue(tuple.getOtherResultCodeOut(), 12)  });
					isEmpty--;
				}
				if(isEmpty != 0)
					formatter.addNewLine();
			}
			category += 1000;
		}
	}
	
	private void addResultCodeStatisticSummary(TableFormatter formatter, Map<Integer, ResultCodeTuple> resultCodeStats) {
		
		TreeSet<Integer> resultCodeSet= new TreeSet<Integer>(resultCodeStats.keySet());
		int isEmpty = resultCodeSet.size();
		if(isEmpty == 0){
			formatter.add("No Result Code Details Available.", TableFormatter.LEFT);
		}
		int category = 0;
		while(isEmpty != 0){

			if(resultCodeSet.subSet(category+1, category + 1000).isEmpty() == false){
				ResultCodeCategory resultCodeCategory = ResultCodeCategory.getResultCodeCategory(category);
				ResultCodeTuple tuple = resultCodeStats.get(category);
				formatter.addRecord(new String[]{
						resultCodeCategory.category, 
						resultCodeCategory.categoryType,
						getDisplayValue(tuple.getResultCodeIn(), 12), 
						getDisplayValue(tuple.getResultCodeOut(), 12)  });
				formatter.add("----------------------------------------------------------\n");
				isEmpty--;
				for(int resultCode : resultCodeSet.subSet(category+1, category + 1000)){

					tuple = resultCodeStats.get(resultCode);
					formatter.addRecord(new String[]{
							String.valueOf(resultCode), 
							"",
							getDisplayValue(tuple.getResultCodeIn(), 12), 
							getDisplayValue(tuple.getResultCodeOut(), 12)  });
					isEmpty--;
				}
				if(isEmpty != 0)
					formatter.addNewLine();
			}
			category += 1000;
		}
	}
	
	private String getDisplayValue(long counter, int wrapDigts) {
		if(counter > (Math.pow(10, wrapDigts)-1))
			return (counter/1000) + "k";
		return String.valueOf(counter);
	}
	
	public String getPeerResultCodeSummay() {

		TableFormatter formatter = new TableFormatter(
				new String[]{"Peer ResultCode Statistics"}, 
				new int[]{RC_TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		
		Map<String, GroupedStatistics> peerStatisticsMap = diameterStatisticProvider.getPeerStatsMap();
		if(peerStatisticsMap.size() == 0){
			formatter.add("No ResultCode Statistics Available", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		for(Map.Entry<String, GroupedStatistics> peerEntry : peerStatisticsMap.entrySet()){
			formatter.addNewLine();
			formatter.add("PEER: "+ peerEntry.getKey(), TableFormatter.CENTER);
			formatter.add(peerEntry.getValue().getResultCodeStatisticSummary());
			formatter.addNewLine();
		}
		formatter.add(getResultCodeLegend());
		return formatter.getFormattedValues();
	}

	public String getPeerResultCodeSummay(String hostIdentity) {
		
		GroupedStatistics peerStatistics =  diameterStatisticProvider.getPeerStatsMap().get(hostIdentity);
		if (peerStatistics == null) {
			return "Peer not found for host identity: " + hostIdentity;
		}
		TableFormatter formatter = new TableFormatter(new String[]{"PEER: " + hostIdentity}, new int[]{RC_TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		formatter.add(peerStatistics.getResultCodeStatisticSummary());
		formatter.add(getResultCodeLegend());
		return formatter.getFormattedValues();
	}

	public String getRealmResultCodeSummay() {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{"Realm ResultCode Statistics"}, 
				new int[]{RC_TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		
		Map<RealmIdentifier, GroupedStatistics> realmStatisticsMap = diameterStatisticProvider.getRealmStatsMap();
		if(realmStatisticsMap.size() == 0){
			formatter.add("No Result Code Details Available", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		for(Map.Entry<RealmIdentifier, GroupedStatistics> realmEntry : realmStatisticsMap.entrySet()){
			RealmIdentifier realmIdentifier = realmEntry.getKey();
			formatter.addNewLine();
			formatter.add("REALM: "+ realmIdentifier.getDbpRealmMessageRouteRealm(), TableFormatter.CENTER);
			formatter.add("Application: " + 
					realmIdentifier.getDbpRealmMessageRouteApp() + 
					"(" + realmIdentifier.getDbpRealmMessageRouteType() + ")" 
					, TableFormatter.LEFT);
			formatter.add("Route-Action: " + 
					realmIdentifier.getDbpRealmMessageRouteAction()
					, TableFormatter.LEFT);
			formatter.addNewLine();
			formatter.add(realmEntry.getValue().getResultCodeStatisticSummary());
			formatter.addNewLine();
		}
		formatter.add(getResultCodeLegend());
		return formatter.getFormattedValues();
	}

	public String getRealmResultCodeSummay(String realmName) {
		
		TableFormatter formatter = new TableFormatter(new String[]{"Realm ResultCode Statistics"}, new int[]{RC_TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		boolean realmFound = false;
		for(Map.Entry<RealmIdentifier, GroupedStatistics> realmEntry : diameterStatisticProvider.getRealmStatsMap().entrySet()){
			if(realmEntry.getKey().getDbpRealmMessageRouteRealm().equalsIgnoreCase(realmName)){
				RealmIdentifier realmIdentifier = realmEntry.getKey();
				formatter.addNewLine();
				formatter.add("REALM: "+ realmIdentifier.getDbpRealmMessageRouteRealm(), TableFormatter.CENTER);
				formatter.add("Application: " + 
						realmIdentifier.getDbpRealmMessageRouteApp() + 
						"(" + realmIdentifier.getDbpRealmMessageRouteType() + ")" 
						, TableFormatter.LEFT);
				formatter.add("Route-Action: " + 
						realmIdentifier.getDbpRealmMessageRouteAction()
						, TableFormatter.LEFT);
				formatter.addNewLine();
				formatter.add(realmEntry.getValue().getResultCodeStatisticSummary());
				realmFound = true;
			}
		}
		if(realmFound == false){
			formatter.add("No Result Code Statistics Available.", TableFormatter.LEFT);
		}else{
			formatter.add(getResultCodeLegend());
		}
		return formatter.getFormattedValues();
		
	}

	public String getApplicationResultCodeSummary(String appString) {
		
		TableFormatter formatter = new TableFormatter(new String[]{}, new int[]{RC_TABLE_WIDTH}, TableFormatter.NO_BORDERS);
		boolean appFound = false;
		for(Map.Entry<ApplicationStatsIdentifier, GroupedStatistics> appEntry : diameterStatisticProvider.getApplicationMap().entrySet()){
			if(appEntry.getKey().getApplication().equalsIgnoreCase(appString)  || 
					String.valueOf(appEntry.getKey().getApplicationId()).equals(appString)){
				formatter.addNewLine();
				formatter.add(appEntry.getKey().toString(), TableFormatter.CENTER);
				formatter.add(appEntry.getValue().getResultCodeStatisticSummary());
				formatter.addNewLine();
				appFound = true;
			}
		}
		if(appFound == false){
			formatter.add("No Result Code Statistics Available.", TableFormatter.LEFT);
		}else{
			formatter.add(getResultCodeLegend());
		}
		return formatter.getFormattedValues();
		
	}

	
	
	private String getResultCodeLegend(){
		StringBuilder responseBuilder = new StringBuilder();
		
		responseBuilder.append(StringUtility.fillChar("", 57, '-'));
		responseBuilder.append("\n  Rx  : Received     ||   Tx  : Transmitted"); 
		responseBuilder.append("\n" + StringUtility.fillChar("", 57, '-') + "\n");
		return responseBuilder.toString();
	}
}
