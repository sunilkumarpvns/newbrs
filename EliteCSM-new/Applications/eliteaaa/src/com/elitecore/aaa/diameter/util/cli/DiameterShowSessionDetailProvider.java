package com.elitecore.aaa.diameter.util.cli;

import java.util.HashMap;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;

public class DiameterShowSessionDetailProvider extends DetailProvider {

	private static final String COUNT = "count";
	private static final String SESSION = "session";
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private SessionFactoryManager sessionFactoryManager;
	
	public DiameterShowSessionDetailProvider(SessionFactoryManager sessionFactoryManager){
		this.sessionFactoryManager = sessionFactoryManager;
		this.detailProviderMap = new HashMap<String ,DetailProvider>();
	}
	@Override
	public String execute(String[] parameters) {
		StringBuilder responseBuilder = new StringBuilder();
		
		if(parameters!=null && parameters.length>0){
			if (COUNT.equalsIgnoreCase(parameters[0])){
				responseBuilder.append("Total Diameter Session count is: " + sessionFactoryManager.getSessionCount() + "\n");
			} else if (isHelpSymbol(parameters[0])){
				responseBuilder.append(getHelpMsg());
			} else {
				responseBuilder.append("Invalid Argument\n\n" + getHelpMsg());
			}
		} else {
			responseBuilder.append("Provide valid Arguments\n" + getHelpMsg());
		}
		return responseBuilder.toString();
	} 

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage : show diameter session [<options>]\n");
		responseBuilder.append("--------------------------------Possible Options:--------------------------------\n");
		responseBuilder.append(COUNT + " 		: Display total numbers of Diameter sessions\n");
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return SESSION;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
	
	@Override
	public String getHotkeyHelp() {
		return "'"+SESSION+"':{'"+COUNT+"':{},'"+HELP+"':{}}";
	}
	
	@Override
	public String getDescription() {
		return "Displays count of diameter sessions";
}
}
