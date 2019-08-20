package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.elitecore.commons.base.Maps;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;

public class DiameterGatewayStatusDetailProvider extends DetailProvider {

	private static final String COMMAND_NAME = "gs";
	private static final String DESCRIPTION = "Provides Operations for Gateway Status";
	private static final String LIST = "-list";
	private static final String INFO = "-info";
	private static final String CLOSE = "-close";
	private static final String HELP = "-help";
	private static final String FORCE_CLOSE = "-forceclose";
	private static final String START = "-start";
	private final String[] header = { "DIAMETER GATEWAY IDENTITY" , "STATUS" };
	private final int[] alignment = new int[] { TableFormatter.LEFT, TableFormatter.LEFT };

	private DiameterGatewayStatus gatewayStatus;

	public DiameterGatewayStatusDetailProvider(DiameterGatewayStatus gatewayStatus) {
		this.gatewayStatus = gatewayStatus;
	}

	@Override
	public String execute(String[] parameters) {
		if (parameters!=null && parameters.length > 0) {
			String parameter = parameters[0];
			
			if(parameters.length > 1){
				parameter += " " + parameters[1];
			}
			
			if (CSV.equalsIgnoreCase(parameter)) {
				return getAllGatewaysCSVInfo();
			}
			
			StringTokenizer tokenizer = new StringTokenizer(parameter," ");
			if (tokenizer.hasMoreTokens()) {
				parameter = tokenizer.nextToken();
			}

			if (parameter.equalsIgnoreCase(HELP) || parameter.equalsIgnoreCase("?")) {
					return getHelpMsg();
				
			} else  if (parameter.equalsIgnoreCase(LIST)) {
				if (tokenizer.hasMoreTokens()) {
					return "Invalid arguments."
							+ CommonConstants.LINE_SEPARATOR + getHelpMsg();
				}
				return getAllGatewaysInfo();
			} else if (parameter.equalsIgnoreCase(START)) {

				String commandParam = null;
				if (tokenizer.hasMoreTokens() == false) {
					return "Gateway Host Identity required.\n" + getHelpMsg();
				}
				commandParam = tokenizer.nextToken();
				commandParam = commandParam.trim();
				if (gatewayStatus.startGateway(commandParam)) {
					return "Connection to Gateway: " + commandParam
							+ " has been attempted";
				}
				return "Unable to Attempt Connection for Gateway: " + commandParam + ". Please refer logs for further detail";

			} else if (parameter.equalsIgnoreCase(CLOSE)) {

				String commandParam = null;
				if (tokenizer.hasMoreTokens() == false) {
					return "Gateway Host Identity required.\n" + getHelpMsg();
				}
				commandParam = tokenizer.nextToken();
				commandParam = commandParam.trim();
				if (gatewayStatus.closeGateway(commandParam)) {
					return "Stop event has been generated for Gateway: "
							+ commandParam;
				}
				return "Unable to generate Stop event for Gateway: "
						+ commandParam + ", Gateway not found";

			} else if (parameter.equalsIgnoreCase(FORCE_CLOSE)) {

				String commandParam = null;
				if (tokenizer.hasMoreTokens() == false) {
					return "Gateway Host Identity required.\n" + getHelpMsg();
				}
				commandParam = tokenizer.nextToken();
				commandParam = commandParam.trim();

				if (gatewayStatus.forceCloseGateway(commandParam)) {
					return "Close Connection has been generated for Gateway: "
							+ commandParam;
				}
				return "Unable to Close Connection for Gateway: " + commandParam
						+ ", Gateway not found";

			} else if (parameter.equalsIgnoreCase(INFO)) {
				String commandParam = null;
				if (tokenizer.hasMoreTokens()) {
					commandParam = tokenizer.nextToken();
					Map<String, IStateEnum> peersStateMap = gatewayStatus.getGatewaysState();
					if (peersStateMap != null) {
						IStateEnum diameterGatewayState = peersStateMap.get(commandParam);
						if (diameterGatewayState != null) {
							return getGatewayInfo(commandParam,diameterGatewayState.name());
						} else {
							return "Gateway not found";
						}
					} else {
						return "Gateway not found";
					}

				} else {
					return "Invalid arguments.\n" + getHelpMsg();
				}
			} else {
				return "Invalid arguments.\n" + getHelpMsg();
			}
		}else{			
			return getAllGatewaysInfo();
		}
	}
	
	protected final static String fillChar(String input, int length){
		return fillChar(input, length, ' ');
	}
	
	protected final static String fillChar(String input, int length, char chr){
		
		if (input == null)
			input = "";

		StringBuilder stringBuffer = new StringBuilder();
		stringBuffer.append(input);
		for(int i = input.length(); i<length; i++){
			stringBuffer.append(chr);
		}
		return stringBuffer.toString();
	}
	
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	private String getAllGatewaysCSVInfo(){
		TableFormatter formatter = new TableFormatter(header, new int[]{40, 20},
				TableFormatter.CSV, TableFormatter.COLUMN_SEPARATOR_COMMA);
		
		return getAllGatewaysInfo(formatter);
	}
	
	private String getAllGatewaysInfo(){

		TableFormatter formatter = new TableFormatter(header, 
				new int[]{40, 20}, 
				TableFormatter.ONLY_HEADER_LINE);
		
		return getAllGatewaysInfo(formatter);
	}

	private String getGatewayInfo(String commandParam, String gatewayState){
		TableFormatter formatter = new TableFormatter(header,new int[]{30, 20},TableFormatter.ONLY_HEADER_LINE);
		formatter.addRecord(new String[]{commandParam, gatewayState}, alignment);		
		return formatter.getFormattedValues();
	}
	
	private String getAllGatewaysInfo(TableFormatter tableFormatter){

		if (tableFormatter == null) {
			return "Error Occured while fetching Diameter Gateways State List";
		}
		Map<String, IStateEnum> gatewaysStateMap = gatewayStatus.getGatewaysState();
		for(Entry<String, IStateEnum> gateway: gatewaysStateMap.entrySet()){
			tableFormatter.addRecord(new String[]{gateway.getKey(), gateway.getValue().name()}, alignment);
		}
		return tableFormatter.getFormattedValues();
	}
	
	
	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() + " -diameter");
		out.println("Description : " + getDescription()+"\n");		
		out.println(fillChar("\t"+LIST,30) + " : Displays list of all the Diameter Gateways");
		out.println(fillChar("\t"+CSV,30) +  " : CSV Output for all Diameter Gateways and Its current state");
		out.println(fillChar("\t"+INFO+" [<host identity>]",30) + " : Displays state of Mentioned 'host identity' Gateway");
		out.println(fillChar("\t"+START+" [<host identity>]",30)  + " : Generates Start Event for Mentioned 'host identity' Gateway and attempts for Init Connection");
		out.println(fillChar("\t"+CLOSE+" [<host identity>]",30) +  " : Generates Stop Event for Mentioned 'host identity' Gateway and attempts for Close Connection");
		out.println(fillChar("\t"+FORCE_CLOSE+" [<host identity>]",30) + " : Forcefully Closes Mentioned 'host identity' Gateway's Connection and marks it Closed");
		return stringWriter.toString();	
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return null;
	}

	@Override
	public String getKey() {
		return "-diameter";
	}
	
	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out= new PrintWriter(writer);

		StringBuilder gatewayStringBuilder = new StringBuilder();
		Map<String, IStateEnum> gatewaysStateMap = gatewayStatus.getGatewaysState();
		if(Maps.isNullOrEmpty(gatewaysStateMap) == false) {
			for(String gateway: gatewaysStateMap.keySet()){
				gatewayStringBuilder.append("'").append(gateway).append("' :{}").append(',');
			}
		}


		String gatewayNames = gatewayStringBuilder.toString();

		out.print("'"+getKey()+"':{'"+HELP+"' :{}" +
				",'"+LIST+"'  :{}" +
				",'"+CSV+"'   :{}" +
				",'"+INFO+"'  :{"+ gatewayNames +"}" +
				",'"+START+"' :{"+ gatewayNames +"}" +
				",'"+CLOSE+"' :{"+ gatewayNames +"}" +
				",'"+FORCE_CLOSE+"' :{"+ gatewayNames +"}}");
		
		return writer.toString() ;
	}

}
