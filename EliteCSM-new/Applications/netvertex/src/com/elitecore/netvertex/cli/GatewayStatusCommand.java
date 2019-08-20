package com.elitecore.netvertex.cli;


import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class GatewayStatusCommand extends EliteBaseCommand {
	
	private static final String COMMAND_NAME = "gs";
	private static final String DESCRIPTION = "Provides Operations for Gateway Status";
	private static final String HELP = "-help";
 	private static HashMap<String ,DetailProvider> detailProviderMap = new HashMap<String, DetailProvider>();

	public static void registerDetailProvider(DetailProvider detailprovider) throws RegistrationFailedException{
		
		if(detailprovider.getKey() == null){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : key is not specified.");
		}
		
		if(detailProviderMap.containsKey(detailprovider.getKey())){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : Show Command already contains detail provider with Key : " + detailprovider.getKey());
		}
		
		detailProviderMap.put(detailprovider.getKey() , detailprovider);	
	}
	
	@Override
	public String execute(String parameter) { 
		StringTokenizer stk = new StringTokenizer(parameter);
		if(stk.countTokens() > 0){
			String[] parameters = new String[stk.countTokens()];
			for(int i = 0 ; i < parameters.length ; i++){
				parameters[i] = stk.nextToken();
			}
			
			if("?".equalsIgnoreCase(parameters[0]) || parameters[0].equalsIgnoreCase(HELP)){
				return getHelp();
			} 
			
			if(detailProviderMap.containsKey(parameters[0])){
				return detailProviderMap.get(parameters[0]).execute(Arrays.copyOfRange(parameters,1,parameters.length));				
			}else{
				return "Invalid Option\n" +getHelpMsg();
			}
		}
		return getHelp();
	}

	
	private String getHelp(){
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);		
		out.println("Usage : " + getCommandName());
		out.println("Description : " + getDescription());
		out.println();
		for(Map.Entry<String, DetailProvider> provider : detailProviderMap.entrySet()){
			out.println(" " +provider.getKey() + " : " +provider.getValue());
		}		
		return stringWriter.toString();	
	}
	
	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getHotkeyHelp() {
		StringWriter writer = new StringWriter();
		PrintWriter out= new PrintWriter(writer);
		out.print("{'"+COMMAND_NAME+"':{'"+HELP+"' :{}");
		for(DetailProvider provider : detailProviderMap.values()){
				out.print("," + provider.getHotkeyHelp());
		}
		out.print("}}");
		
		return writer.toString() ;
	}
 
}
