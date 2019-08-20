package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.core.util.cli.cmd.DetailProvider;

public class RadiusGatewayStatusDetailProvider extends DetailProvider{
 
	private static final String COMMAND_NAME = "gs";
	private static final String DESCRIPTION = "Provides Information on Radius Gateway Status";
	private static final String LIST = "-list";
	private static final String HELP = "-help";
 	private final String[] header = { "RADIUS GATEWAY IDENTITY" , "STATUS" };
	private final int[] alignment = new int[] { TableFormatter.LEFT, TableFormatter.LEFT };

	private RadiusGatewayStatus gatewayStatus;

	public RadiusGatewayStatusDetailProvider(RadiusGatewayStatus gatewayStatus){ 
		this.gatewayStatus = gatewayStatus;
	}

	@Override
	public String execute(String[] parameters) {
		if (parameters.length > 0) {
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

			if (parameter.equalsIgnoreCase(LIST)) {
				if (tokenizer.hasMoreTokens()) {
					return "Invalid arguments."
							+ CommonConstants.LINE_SEPARATOR + getHelpMsg();
				}
				return getAllGatewaysInfo();
			
			}else if (parameter.equalsIgnoreCase(HELP) || parameter.equalsIgnoreCase("?")) {
				return getHelpMsg();
			}else {
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

	private String getAllGatewaysInfo(TableFormatter tableFormatter){

		if (tableFormatter == null) {
			return "Error Occured while fetching Radius Gateways State List";
		}
		Map<String, Boolean> gatewaysStateMap = gatewayStatus.getGatewaysState();
		for(Entry<String, Boolean> gateway: gatewaysStateMap.entrySet()){
			tableFormatter.addRecord(new String[]{gateway.getKey(), gateway.getValue()==true ? "Active" : "Dead"}, alignment);
 		}
		return tableFormatter.getFormattedValues();
	}
	
	
	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() + " -radius");
		out.println("Description : " + getDescription());
		out.println();
		out.println(fillChar("\t"+LIST,10) + " : Displays list of all the Radius Gateways");
		out.println(fillChar("\t"+CSV,10) + " : CSV Output for all Radius Gateway and Its current state");
		return stringWriter.toString();	

	}


	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return null;
	}

	@Override
	public String getKey() {
		return "-radius";
	}
	
	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out= new PrintWriter(writer);
		out.print("'"+getKey()+"':{'"+HELP+"' :{},'"+LIST+"' :{},'"+CSV+"' :{}}");
		
		return writer.toString() ;
	}

}
