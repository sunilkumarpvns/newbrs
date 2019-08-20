package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.impl.UDPCommunicatorManagerImpl;
import com.elitecore.core.util.cli.TableFormatter;

public class ESIStatisticsCommand extends EliteBaseCommand{

	/**
	 * <ol>
	 * <li>esistats</li>
	 * <li>esistats -csv</li>
	 * <li>esistats -drverType -csv</li>
	 * <li>esistats -drverType drivername</li>
	 * </ol>
	 */
	private static final int REQUIRED_PARAMS_LENGTH = 3;
	private static final String COMMAND_NAME = "esistats";
	private String hotKeyHelpPattern;
	private static String helpString;
	private static final String[] headers = {"N","S","TR","TE","TT","DC"};
	private static final int[]    width   = {30,  1,  10,  10,  10,  5};
	private static final int[] columnAlignment = {TableFormatter.LEFT, TableFormatter.LEFT,TableFormatter.RIGHT, TableFormatter.RIGHT,TableFormatter.RIGHT,TableFormatter.RIGHT};
	private static final String[] csvHeaders = {"Name","Status","Total Requests","Total Errors","Total Timedouts","Total Dead Count"};
	private static final int[]    csvWidth   = {30,  1,  10,  10,  10,  5};
	private static final int[] csvAlignment = {TableFormatter.LEFT, TableFormatter.LEFT,TableFormatter.LEFT, TableFormatter.LEFT,TableFormatter.LEFT,TableFormatter.LEFT};
	private static final String HELP = "-help";
	private static final String HELP_SHORTHAND = "?";
	
	static{
		initHelpString();
	}
	
	private static void initHelpString(){
		DriverTypes[] driverTypes = DriverTypes.values();
		//initializing the helpString
		StringWriter writer = new StringWriter();
		PrintWriter buffer = new PrintWriter(writer);
		
		buffer.println("Usage : " + COMMAND_NAME + " <option>");
		buffer.println("Possible options");
		for(DriverTypes drvrType: driverTypes){
			buffer.append(fillChar("\t" + fillChar("-" + drvrType.name(),40) + "[<name>[,<name>]]",40));
			buffer.append("    Shows statistics of " + drvrType.name());
			buffer.println();
		}
		buffer.append(fillChar("\t" + fillChar("-" + UDPCommunicator.COMMAND_KEY,40) + "[<name>[,<name>]]",40));
		buffer.append("    Shows statistics of " + UDPCommunicator.COMMAND_KEY);
		buffer.println();
		buffer.close();

		helpString = writer.toString();
	}

	private static String getHotKeyPatternForType(String type, Collection<ESIStatistics> esiStatistics){
		if(esiStatistics != null && esiStatistics.size() > 0){
			ArrayList<String> hotKeyList = new ArrayList<String>();
			for(ESIStatistics esiStat : esiStatistics){
				if(type.equals(esiStat.getTypeName())){
					hotKeyList.add(getHotkeyPattern(esiStat.getName()));
				}
			}
			return StringUtility.getDelimitirSeparatedString(hotKeyList, ",");
		}
		return "";
	}
	
	private static String getHotkeyPattern(String drvrType) {
		return "'" + drvrType + "':{}";
	}
	
	@Override
	public String execute(String parameter) {
		if(parameter == null || parameter.trim().length() == 0){
			TableFormatter formatter = new TableFormatter(headers, width,TableFormatter.OUTER_BORDER);
			return getAllESIStatistics(formatter)+ getLegend();
		}
		
		StringTokenizer tk = new StringTokenizer(parameter);
		int paramLength = tk.countTokens();
		
		if(paramLength > REQUIRED_PARAMS_LENGTH){
			return getHelp("Too Many Parameters");
		}
		
		String typeParam = tk.nextToken().trim();
		
		if(HELP.equals(typeParam) || HELP_SHORTHAND.equals(typeParam)){
			return getHelp();
		}
		
		if(CSV.equalsIgnoreCase(typeParam)){
			TableFormatter formatter = new TableFormatter(csvHeaders, csvWidth, 
					TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
			return getAllESIStatistics(formatter);
		}
		
		if(!validateTypeParam(typeParam)){
			return getHelp("Invalid Option");
		}
		if(isNameFilter(paramLength)){
			String token = tk.nextToken().trim();
			if(CSV.equalsIgnoreCase(token)){
				TableFormatter formatter = new TableFormatter(csvHeaders, csvWidth, csvAlignment, 
						TableFormatter.CSV,TableFormatter.ROW_SEPARATOR_HYPHEN, TableFormatter.COLUMN_SEPARATOR_COMMA);
				return getESIByTypeFilter(typeParam.substring(1), formatter);
			}
			return getESIByNameAndTypeFilter(token,typeParam.substring(1));
		}else{
			TableFormatter formatter = new TableFormatter(headers, width,TableFormatter.OUTER_BORDER);
			return getESIByTypeFilter(typeParam.substring(1), formatter)+ getLegend();
		}
	}
	
	private String getAllESIStatistics(TableFormatter formatter) {
		if(formatter == null){
			return "";
		}
		Collection<ESIStatistics> esiStatistics = DriverManager.getAllDriverStatistics();
		for(ESIStatistics driverStats : esiStatistics){
			formatter.addRecord(new String[]{driverStats.getName(), driverStats.currentStatus().charAt(0)+"",driverStats.getTotalRequests()+"",driverStats.getTotalErrors()+"",driverStats.getTotalTimedouts()+"",driverStats.getDeadCount()+""},columnAlignment);
		}
		
		esiStatistics = UDPCommunicatorManagerImpl.getAllUDPCommunicatorStatistics();
		for(ESIStatistics udpStats : esiStatistics){
			formatter.addRecord(new String[]{udpStats.getName(), udpStats.currentStatus().charAt(0)+"",udpStats.getTotalRequests()+"",udpStats.getTotalErrors()+"",udpStats.getTotalTimedouts()+"",udpStats.getDeadCount()+""},columnAlignment);
		}
		
		return formatter.getFormattedValues();
	}

	private String getESIByTypeFilter(String typeParam, TableFormatter formatter) {
		Collection<ESIStatistics> esiStatistics;
		if(UDPCommunicator.COMMAND_KEY.equals(typeParam)){
			 esiStatistics = UDPCommunicatorManagerImpl.getAllUDPCommunicatorStatistics();
		}else{
			esiStatistics = DriverManager.getAllDriverStatistics();
		}
		
		String response = null;
		String defaultMessage = "----- No Statistics found for type: " + typeParam + " -------";
		if(formatter != null){
			for(ESIStatistics esiStatistic : esiStatistics){
				if(esiStatistic.getTypeName().equals(typeParam)){
					formatter.addRecord(new String[]{
							esiStatistic.getName(), 
							esiStatistic.currentStatus().charAt(0)+"",
							esiStatistic.getTotalRequests()+"",
							esiStatistic.getTotalErrors()+"",
							esiStatistic.getTotalTimedouts()+"",
							esiStatistic.getDeadCount()+"" }, columnAlignment);
				}
			}
			response = formatter.getFormattedValues();
		}
		return  response!= null ? response : defaultMessage;
	}

	private String getESIByNameAndTypeFilter(String esiName, String typeParam) {
		StringWriter writer = new StringWriter();
		PrintWriter buffer = new PrintWriter(writer);
		
		String[] esiNames = ParserUtility.splitString(esiName, ',');
		
		ESIStatistics esiStatistics;
		if(UDPCommunicator.COMMAND_KEY.equals(typeParam)){
			for(String name : esiNames){
				esiStatistics = UDPCommunicatorManagerImpl.getUDPCommunicatorStatisticsByName(name);
				if(esiStatistics == null){
					buffer.println(fillChar("", 50, '-'));
					buffer.println("Statistics not found for: " + name);
				}else{
					buffer.println(fillChar("", 50, '-'));
					buffer.println("Statistics for: " + name);
					buffer.println(String.valueOf(esiStatistics));
				}
			}
		}else{
			for(String name : esiNames){
				esiStatistics = DriverManager.getDriverStatisticsByName(name);
				if(esiStatistics == null){
					buffer.println(" Statistics not found for: " + name);
				}else{
					buffer.println(" Statistics for: " + name);
					buffer.println(String.valueOf(esiStatistics));
				}
			}
		}
		
		buffer.close();
		return writer.toString();
	}
	
	private String getLegend(){
		StringWriter writer = new StringWriter();
		PrintWriter buffer = new PrintWriter(writer);
		buffer.println();
		buffer.println("N  - "  + fillChar("Name", 20)            + " S  - " + fillChar("Status", 20));
		buffer.println("A  - "  + fillChar("Alive", 20)           + " D  - " + fillChar("Dead", 20));
		buffer.println("F  - "  + fillChar("Fail", 20)            + " TR - "  + fillChar("Total Requests", 20));
		buffer.println("TE - " + fillChar("Total Errors", 20) 	  + " TT - "  + fillChar("Total Timedouts", 20));
		buffer.println("DC - " + fillChar("Total Dead Count", 20));
		buffer.close();
		return writer.toString();
	}

	private boolean isNameFilter(int paramLength){
		return paramLength > 1;
	}

	private boolean validateTypeParam(String typeParam){
		String type = typeParam.trim();
		if(!type.startsWith("-") && type.length() > 1){
			return false;
		}
		
		type = type.substring(1);
		DriverTypes driverType = DriverTypes.getDriverTypeByName(type);
		if(driverType == null){
			if(!UDPCommunicator.COMMAND_KEY.equals(type)){
				return false;
			}
		}
		return true;
	}
	
	private String getHelp() {
		return helpString;
	}

	private String getHelp(String errorReason) {
		return CommonConstants.LINE_SEPARATOR + errorReason + CommonConstants.LINE_SEPARATOR + helpString;
	}
	
	@Override
	public String getHotkeyHelp() {
		if(hotKeyHelpPattern == null)
			initHotKeyHelp();
		return hotKeyHelpPattern;
	}
	
	private void initHotKeyHelp(){
		Collection<ESIStatistics> esiStatistics = DriverManager.getAllDriverStatistics();
		
		//initializing the hotkey string
		DriverTypes[] driverTypes = DriverTypes.values();
		String[] tmpDriverType = new String[driverTypes.length];
		int i=0;
		for(DriverTypes drvrType : driverTypes){
			String hotKeyForType = getHotKeyPatternForType(drvrType.name(), esiStatistics);
			String csvString = "'"+CSV+"':{}";
			if(hotKeyForType.length() > 0){
				csvString= ","+csvString;
			}
			 tmpDriverType[i] = "'-" + drvrType + "':{" + hotKeyForType + csvString+"}";
			i++;
		}
		esiStatistics = UDPCommunicatorManagerImpl.getAllUDPCommunicatorStatistics();
		String hotKeyForUDP = getHotKeyPatternForType(UDPCommunicator.COMMAND_KEY, esiStatistics);
		String csvString = "'"+CSV+"':{}";
		if(hotKeyForUDP.length() > 0){
			csvString= ","+csvString;
		}
		
		hotKeyHelpPattern = "{'" + COMMAND_NAME + "':{'"+HELP+"':{},'"+CSV+"':{}," + StringUtility.getCommaSeparated(tmpDriverType)
		+ "," + "'" + "-" + UDPCommunicator.COMMAND_KEY + "':{" + hotKeyForUDP +"}}}";
	}


	@Override
	public String getCommandName() {
		return COMMAND_NAME; 
	}

	@Override
	public String getDescription() {
		return "Shows the statistics of External Systems";
	}
}
