package com.elitecore.netvertex.cli;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;

public class DiameterShowStatisticDetailProvider extends DetailProvider {

	private static final String STATISTIC 		= "statistic";
	private static final String ALL				= "all";
	private static final String HELP			= "-help";
	private static final int TABLE_WIDTH 		= 76; 

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
	private HashMap<String ,DetailProvider> detailProviderMap;
	private DiameterStatisticsProvider diameterStatisticProvider;

	public DiameterShowStatisticDetailProvider(DiameterStatisticsProvider diameterStatisticProvider){
		this.diameterStatisticProvider = diameterStatisticProvider;
		detailProviderMap = new LinkedHashMap<String ,DetailProvider>();
	}

	@Override
	public String execute(String[] parameters) {

		if(parameters == null || parameters.length == 0)
			return getHelpMsg();

		if(ALL.equalsIgnoreCase(parameters[0])){
			if(parameters.length == 1){
				return getStackStatisticSummary();
			}
			return getAllUsage();
		}
		if(isHelpSymbol(parameters[0])){
			return getHelpMsg();
		}
		if(CSV.equalsIgnoreCase(parameters[0])){
			return getStackStatisticCSVSummary();
		}
		DetailProvider detailProvider = detailProviderMap.get(parameters[0]);
		if(detailProvider != null){
			parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
			return detailProvider.execute(parameters);
		}
		return getHelpMsg();
	}

	private String getAllUsage() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	  : show diameter "+STATISTIC+ " " + ALL);
		responseBuilder.append("\nDescription : Displays Diameter Stack Statistics.");
		return responseBuilder.toString();
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage 	  : show diameter statistic <options>");
		responseBuilder.append("\nDescription : " + getDescription());
		responseBuilder.append("\n\n-----------------------------Possible Options:------------------------------\n");
		responseBuilder.append(StringUtility.fillChar(ALL, 20, ' ') + " : Displays Diameter Stack Statistics.\n");
		responseBuilder.append(StringUtility.fillChar(CSV, 20, ' ') + " : Diameter Stack Statistics in CSV Format.\n");
		for(DetailProvider detailProvider : detailProviderMap.values()){
			responseBuilder.append(detailProvider.getHelpMsg());
		}
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return STATISTIC;
	}

	@Override
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
		return  "'"+STATISTIC+"':{'"+CSV+"':{}, '"+ALL+"':{'"+HELP+"':{}} " + childDetailProvidersHotKeyHelp + ", '"+HELP+"':{}}";

	}

	private String getChildDetailProvidersHotKeyHelp() {

		StringBuilder appHotKey = new StringBuilder();
		for(DetailProvider detailProvider: detailProviderMap.values()){

			String hotKey = detailProvider.getHotkeyHelp();
			if(hotKey != null && hotKey.trim().length() > 0){
				appHotKey.append(",");
				appHotKey.append(hotKey);
			}
		}
		return appHotKey.toString();
	}
	
	private String getStackStatisticCSVSummary() {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{"Diameter Stack Statistics"}, 
				new int[]{TABLE_WIDTH}, TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		
		GroupedStatistics stackStatistics = diameterStatisticProvider.getStackStatistics();
		if(stackStatistics == null){
			formatter.addRecord(new String[]{"No Statistics Available."});
			return formatter.getFormattedValues();
		}
		formatter.add("Stack Up Time : " + getStackUpTime(), TableFormatter.LEFT);
		formatter.add(stackStatistics.toCSV());
		return formatter.getFormattedValues();
	}
	
	private String getStackStatisticSummary() {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{"Diameter Stack Statistics"}, 
				new int[]{TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		GroupedStatistics stackStatistics = diameterStatisticProvider.getStackStatistics();
		if(stackStatistics == null){
			formatter.add("No Statistics Available.", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		formatter.add(
				"Stack Up Time : " + getStackUpTime(), 
				TableFormatter.LEFT);
		formatter.add(stackStatistics.toString());
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
	
	public String getStackUpTime() {
		return dateFormat.format(Parameter.getInstance().getStackUpTime());
	}
}
