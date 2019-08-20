package com.elitecore.aaa.core.util.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public class SetCommand extends EliteBaseCommand{
	private static List<ConfigurationSetter> configSetters;
	private static List<ConfigurationSetter> serviceConfigSetters;
	private static final String SET = "set";
	private static final String INPUT = "input";
	private static final String OUTPUT = "output";
	private static final String FILENAME = "<file_name>";
	private static final String SERVER = "server";
	private static final String SERVICE = "service";
	
	public SetCommand(AAAServerContext serverContext){	
		configSetters = new ArrayList<ConfigurationSetter>();
		serviceConfigSetters = new ArrayList<ConfigurationSetter>();
	}
	
	@Override
	public String execute(String parameter) {
		if(parameter == null || parameter.trim().length() <= 0 ){
			return getHelpMsg();
		}
		StringTokenizer stk = new StringTokenizer(parameter);
		String[] parameters = new String[stk.countTokens()];
		for(int i = 0 ; i < parameters.length ; i++){
			parameters[i] = stk.nextToken();
		}
		if(parameter != null && parameter.length() > 0){

			if(HELP.equalsIgnoreCase(parameters[0])){
				return getHelpMsg();
			}else if(SERVER.equalsIgnoreCase(parameters[0])){
				int size = configSetters.size();
				if(parameters.length > 1){
					if(HELP.equalsIgnoreCase(parameters[1]) || HELP_OPTION.equalsIgnoreCase(parameters[1])){
						return getConfigSetterHelp(configSetters);
					}
					for(int i = 0 ; i < size ; i++){
						if(configSetters.get(i).isEligible(parameters)){
							return configSetters.get(i).execute(parameters);
						}
					}
				}
			}else if(SERVICE.equalsIgnoreCase(parameters[0])){
				int size = serviceConfigSetters.size();
				if(parameters.length > 1){
					
					if(HELP.equalsIgnoreCase(parameters[1])  || HELP_OPTION.equalsIgnoreCase(parameters[1])){
						return getConfigSetterHelp(serviceConfigSetters);
					}
					
					for(int i = 0 ; i < size ; i++){

						if(serviceConfigSetters.get(i).isEligible(parameters)){
							return serviceConfigSetters.get(i).execute(parameters);
						}
					}
				}
			}
		}
		
		return "Invalid Option";
	}
	
	private String getConfigSetterHelp(List<ConfigurationSetter> configSetter){
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\n-----------------------------------------------------------------\n");
		for (int i = 0; i < configSetter.size(); i++) {
			responseBuilder.append(configSetter.get(i).getHelpMsg());
			responseBuilder.append("\n-----------------------------------------------------------------\n");
		}
		return responseBuilder.toString();
	}

	@Override
	public String getCommandName() {
		return "set";
	}

	@Override
	public String getDescription() {
		return "Set given Parameter.";
	}

	@Override
	public String getHotkeyHelp() {
		String str = "{'" + SET + "':{'" + SERVER + "':{ '" + HELP + "':{}, ";

		int size = configSetters.size();
		for( int i = 0 ; i < size ; i++){
			if(0 < i && i < size ){
				str = str + ",";
			}
			str = str + configSetters.get(i).getHotkeyHelp();
		}
		str = str + "},'" + SERVICE + "':{ '" + HELP + "':{}, ";
		size = serviceConfigSetters.size();
		for( int i = 0 ; i < size ; i++){
			if(0 < i && i < size ){
				str = str + ",";
			}			
			str = str + serviceConfigSetters.get(i).getHotkeyHelp();
		}
		str = str + "},'" + INPUT + "':{} , '" + OUTPUT + "':{} , '" + HELP + "':{} }}";
		return str;		
	}

	public static void registerConfigurationSetter(ConfigurationSetter configSetter){
		if(configSetter.getConfigurationSetterType() == ConfigurationSetter.SERVER_TYPE){
			configSetters.add(configSetter);	
		}else if(configSetter.getConfigurationSetterType() == ConfigurationSetter.SERVICE_TYPE){
			serviceConfigSetters.add(configSetter);
		}
	}
	
	
	
	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		String newline = System.getProperty("line.separator");
		responseBuilder.append(newline);
		responseBuilder.append("Usage : " + SET + " [<options>]").append(newline);
		responseBuilder.append("Description: " + getDescription()).append(newline);
		responseBuilder.append("-------------------------------------------------Possible Options:----------------------------------------------").append(newline).append(newline);
		responseBuilder.append("server  [<options>] 			: Change configuration of Server level (Temporary - discard on server shutdown)").append(newline);
		responseBuilder.append("service [<options>] 			: Change configuration of Service level (Temporary - discard on server shutdown)").append(newline);
		responseBuilder.append(INPUT + "  " + FILENAME + "  			: File for batch input of commands").append(newline);
		responseBuilder.append(OUTPUT + " " + FILENAME + "  			: Output will be rolled in specified file.").append(newline);
		responseBuilder.append("------------------------------------------------------------------------------------").append(newline);
		return responseBuilder.toString();
	}
	
	public interface ConfigurationSetter {
		public int SERVER_TYPE = 1;
		public int SERVICE_TYPE = 2;
		public String INVALID_OPTION = "Invalid option provied";
		public String execute(String... parameters);
		public boolean isEligible(String... parameters);
		public String getHelpMsg();
		public String getHotkeyHelp();
		public int getConfigurationSetterType();
	}
}
