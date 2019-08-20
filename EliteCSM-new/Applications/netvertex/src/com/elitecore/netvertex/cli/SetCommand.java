package com.elitecore.netvertex.cli;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SetCommand extends EliteBaseCommand{
	private static List<ConfigurationSetter> configSetters;
	private static List<ConfigurationSetter> serviceConfigSetters;
	private static final String SET = "set";
	private static final String INPUT = "input";
	private static final String OUTPUT = "output";
	private static final String FILENAME = "<file_name>";
	private static final String SERVER = "server";
	private static final String SERVICE = "service";
	
	static{
		configSetters = new ArrayList<ConfigurationSetter>();
		serviceConfigSetters = new ArrayList<ConfigurationSetter>();
	}
	
	@Override
	public String execute(String parameter) {
		if(parameter == null || parameter.trim().length() <= 0 ){
			return "Invalid Option";
		}
		
		StringTokenizer stk = new StringTokenizer(parameter);
		String[] parameters = new String[stk.countTokens()];
		for(int i = 0 ; i < parameters.length ; i++){
			parameters[i] = stk.nextToken();
		}
		
		if(HELP.equalsIgnoreCase(parameters[0]) || HELP_OPTION.equalsIgnoreCase(parameters[0])){
			return getHelpMsg();
		}
		
		if(SERVER.equalsIgnoreCase(parameters[0])){
			
			if(HELP.equalsIgnoreCase(parameters[0]) || HELP_OPTION.equalsIgnoreCase(parameters[0])){
				return getConfigSetterHelp(configSetters);
			}
			
			int size = configSetters.size();
			for(int i = 0 ; i < size ; i++){
				if(configSetters.get(i).isEligible(parameters)){
					return configSetters.get(i).execute(parameters);
				}
			}
		}
		
		
		if(SERVER.equalsIgnoreCase(parameters[0])){
			
			if(HELP.equalsIgnoreCase(parameters[0]) || HELP_OPTION.equalsIgnoreCase(parameters[0])){
				return getConfigSetterHelp(serviceConfigSetters);
			}
			
			int size = serviceConfigSetters.size();
			for(int i = 0 ; i < size ; i++){
				if(serviceConfigSetters.get(i).isEligible(parameters)){
					return serviceConfigSetters.get(i).execute(parameters);
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
		return SET;
	}

	@Override
	public String getDescription() {
		return "Change configuration of NetVertex Server ( Temporary - discard on server shutdown)";
	}

	@Override
	public String getHotkeyHelp() {
		StringBuilder str = new StringBuilder().append("{'").append(SET).append("':{'").append(SERVER).append("':{ '").append(HELP).append("':{}, ");

		int size = configSetters.size();
		for( int i = 0 ; i < size ; i++){
			if(0 < i && i < size ){
				str = str.append(",");
			}
			str.append(configSetters.get(i).getHotkeyHelp());
		}
		str = str.append("},'").append(SERVICE).append("':{ '").append(HELP).append("':{}, ");
		size = serviceConfigSetters.size();
		for( int i = 0 ; i < size ; i++){
			if(0 < i && i < size ){
				str = str.append(",");
			}			
			str = str.append(serviceConfigSetters.get(i).getHotkeyHelp());
		}
		str.append("},'").append(INPUT).append("':{} , '").append(OUTPUT).append("':{} , '").append(HELP).append("':{} }}");
		return str.toString();
	}

	public static void registerConfigurationSetter(ConfigurationSetter configSetter){
		if(configSetter.getConfigurationSetterType() == ConfigurationSetter.SERVER_TYPE){
			configSetters.add(configSetter);	
		}else if(configSetter.getConfigurationSetterType() == ConfigurationSetter.SERVICE_TYPE){
			serviceConfigSetters.add(configSetter);
		}
	}
	
	@Override
	public String getHelpMsg(){
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
		public String execute(String... parameters);
		public boolean isEligible(String... parameters);
		public String getHelpMsg();
		public String getHotkeyHelp();
		public int getConfigurationSetterType();
	}
}
