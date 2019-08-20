package com.elitecore.aaa.diameter.util.cli;

import java.util.HashMap;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.core.common.transport.stats.DiameterNetworkStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;

public abstract class DiameterStackStatisticDetailProvider extends DetailProvider {

	private static final String STATS_SUMMARY = "-s";
	private static final String TRAFFIC_SUMMARY = "-t";
	private final static String STACK			= "stack";
	private final static String HELP			= "-help";
	private static final int TABLE_WIDTH 		= 76; 

	private DiameterStatisticsProvider diameterStatisticProvider;

	public DiameterStackStatisticDetailProvider(DiameterStatisticsProvider diameterStatisticProvider){
		this.diameterStatisticProvider = diameterStatisticProvider;
	}

	@Override
	public String execute(String[] parameters) {

		if (parameters == null || parameters.length == 0) {
			return getTrafficSummary() + getStackStatisticSummary();
		}
		
		if (TRAFFIC_SUMMARY.equalsIgnoreCase(parameters[0])) {
			if (parameters.length == 1) {
				return getTrafficSummary();
			}
			if (CSV.equalsIgnoreCase(parameters[1])) {
				return getTafficCSVSummary();
			}
		}
		
		if (STATS_SUMMARY.equalsIgnoreCase(parameters[0])) {
			if (parameters.length == 1) {
				return getStackStatisticSummary();
			}
			if (CSV.equalsIgnoreCase(parameters[1])) {
				return getStackStatisticCSVSummary();
			}
		}
		return getUsage();
	}
	
	private String getTafficCSVSummary() {
		TableFormatter formatter = new TableFormatter(
				new String[]{"Traffic Summary", ""}, 
				new int[]{30, 40}, TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		formatter.addRecord(new String[]{"Average Incoming MPS", String.valueOf(diameterStatisticProvider.getAvgIncomingMPS())} );
		formatter.addRecord(new String[]{"Average RTT", String.valueOf(diameterStatisticProvider.getAvgRoundTripTime())} );
		formatter.addRecord(new String[]{"Current MPS", String.valueOf(getCurrentMPS())} );
		formatter.add("Thread Summary");
		formatter.addNewLine();
		formatter.add(getThreadCSVSummary());
		return formatter.getFormattedValues();

	}

	private String getThreadCSVSummary() {
		TableFormatter formatter = new TableFormatter(new String[]{
				"Pool-Type", "Min", "Max", "Active-Count", "Current-Count", "Peak-Count", "Queue-Size"}, 
				new int[]{9, 5, 5, 12, 13, 10, 10} ,TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		
		addThreadSummaryDataTo(formatter);
		return formatter.getFormattedValues();
	}

	private String getThreadSummary() {
		TableFormatter formatter = new TableFormatter(new String[]{
				"Pool-Type", "Min", "Max", "Active-Count", "Current-Count", "Peak-Count", "Queue-Size"}, 
				new int[]{9, 5, 5, 12, 13, 10, 10} , 
				new int[]{TableFormatter.LEFT, TableFormatter.RIGHT, TableFormatter.RIGHT,
				TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT, TableFormatter.RIGHT},
				TableFormatter.ONLY_HEADER_LINE);
		
		addThreadSummaryDataTo(formatter);
		return formatter.getFormattedValues();
	}

	private void addThreadSummaryDataTo(TableFormatter formatter) {
		
		if(getDiameterNetworkStatisticsProvider() == null) {
			formatter.add("No Summary Available, check `services` command", TableFormatter.LEFT);
			return;
		}
		
		formatter.addRecord(new String[]{
				"Message", 
				String.valueOf(getDiameterNetworkStatisticsProvider().getMessageThreadPoolDetails().getMinSize()),
				String.valueOf(getDiameterNetworkStatisticsProvider().getMessageThreadPoolDetails().getMaxSize()),
				String.valueOf(getDiameterNetworkStatisticsProvider().getMessageThreadPoolDetails().getActiveCount()),
				String.valueOf(getDiameterNetworkStatisticsProvider().getMessageThreadPoolDetails().getPoolSize()),
				String.valueOf(getDiameterNetworkStatisticsProvider().getMessageThreadPoolDetails().getPeakPoolSize()),
				String.valueOf(getDiameterNetworkStatisticsProvider().getMessageThreadPoolDetails().getQueueSize())});
		
		formatter.addRecord(new String[]{
				"Base Pkt", 
				String.valueOf(getDiameterNetworkStatisticsProvider().getBaseThreadPoolDetails().getMinSize()),
				String.valueOf(getDiameterNetworkStatisticsProvider().getBaseThreadPoolDetails().getMaxSize()),
				String.valueOf(getDiameterNetworkStatisticsProvider().getBaseThreadPoolDetails().getActiveCount()),
				String.valueOf(getDiameterNetworkStatisticsProvider().getBaseThreadPoolDetails().getPoolSize()),
				String.valueOf(getDiameterNetworkStatisticsProvider().getBaseThreadPoolDetails().getPeakPoolSize()),
				String.valueOf(getDiameterNetworkStatisticsProvider().getBaseThreadPoolDetails().getQueueSize())});
	}

	protected abstract DiameterNetworkStatisticsProvider getDiameterNetworkStatisticsProvider();

	@Override
	public String getHelpMsg() {
		return StringUtility.fillChar(STACK, 20, ' ') + " : " + getDescription() + "\n";
	}
	
	public String getUsage() {
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\nUsage 	  : show diameter statistic stack <options>");
		responseBuilder.append("\nDescription : " + getDescription());
		responseBuilder.append("\n\n-----------------------------Possible Options:------------------------------\n");
		responseBuilder.append(StringUtility.fillChar(TRAFFIC_SUMMARY, 20, ' ') + " : Displays Diameter Traffic and Thread Summary.\n");
		responseBuilder.append(StringUtility.fillChar(TRAFFIC_SUMMARY + " " + CSV, 20, ' ') + " : Diameter Traffic and Thread Summary in CSV Format.\n");
		responseBuilder.append(StringUtility.fillChar(STATS_SUMMARY, 20, ' ') + " : Displays Diameter Stack Statistic Details.\n");
		responseBuilder.append(StringUtility.fillChar(STATS_SUMMARY + " " + CSV, 20, ' ') + " : Diameter Stack Statistics in CSV Format.\n");
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return STACK;
	}

	public String getDescription(){
		return "Displays Diameter Stack Statistics";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		throw new UnsupportedOperationException("Sub detail providers are not provisioned in Stack Detail Provider");
	}

	@Override
	public String getHotkeyHelp() {
		
		return "'"+STACK+"':{ '" + STATS_SUMMARY + "':{ '"+CSV+"':{}} , '" + TRAFFIC_SUMMARY + "':{ '"+CSV+"':{}}, '"+HELP+"':{}}";
	}

	private String getStackStatisticSummary() {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{"Detailed Statistics"}, 
				new int[]{TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);
		GroupedStatistics stackStatistics = diameterStatisticProvider.getStackStatistics();
		if(stackStatistics == null){
			formatter.add("No Statistics Available.", TableFormatter.LEFT);
			return formatter.getFormattedValues();
		}
		formatter.add(stackStatistics.toString());
		formatter.add(getLegend());
		return formatter.getFormattedValues();
	}
	
	private String getTrafficSummary() {
		TableFormatter formatter = new TableFormatter(
				new String[]{"Traffic Summary"}, 
				new int[]{TABLE_WIDTH}, TableFormatter.ONLY_HEADER_LINE);

		formatter.add(StringUtility.fillChar("Average Incoming MPS", 20, ' ') + ": " + diameterStatisticProvider.getAvgIncomingMPS(), TableFormatter.LEFT);
		formatter.add(StringUtility.fillChar("Average RTT", 20, ' ') + ": " + diameterStatisticProvider.getAvgRoundTripTime(), TableFormatter.LEFT);
		formatter.add(StringUtility.fillChar("Current MPS", 20, ' ') + ": " + getCurrentMPS(), TableFormatter.LEFT);
		formatter.add("Thread Summary", TableFormatter.CENTER);
		formatter.add(getThreadSummary());
		return formatter.getFormattedValues();
	}

	private long getCurrentMPS() {
		long currentMPM = diameterStatisticProvider.getMessagePerMinute();
		long currentTimeMillis = System.currentTimeMillis();
		long lastResetMillis = diameterStatisticProvider.getLastResetTimeInMilli();		
		long statsAge = (currentTimeMillis - lastResetMillis)/1000;
		long currentMPS = statsAge > 0 ? currentMPM/statsAge : currentMPM;
		return currentMPS;
	}

	private String getStackStatisticCSVSummary() {
		
		TableFormatter formatter = new TableFormatter(
				new String[]{"Detailed Statistics"}, 
				new int[]{TABLE_WIDTH}, TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		
		GroupedStatistics stackStatistics = diameterStatisticProvider.getStackStatistics();
		if(stackStatistics == null){
			formatter.addRecord(new String[]{"No Statistics Available."});
			return formatter.getFormattedValues();
		}
		formatter.add(stackStatistics.toCSV());
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
