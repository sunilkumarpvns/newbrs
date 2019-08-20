package com.elitecore.aaa.diameter.util.cli;

import java.util.HashMap;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;

public class DiameterClearSessionDetailProvider extends DetailProvider{

	private static final String ALL = "all";
	private static final String TIME = "time";
	private static final String ID = "id";
	private static final String SESSION = "session";
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private SessionFactoryManager sessionFactoryManager;
	
	public DiameterClearSessionDetailProvider(SessionFactoryManager sessionFactoryManager) {
		this.sessionFactoryManager = sessionFactoryManager;
		detailProviderMap = new HashMap<String ,DetailProvider>();
	}
	
	@Override
	public String execute(String[] parameters) {
		String response = "";
		if(parameters!=null && parameters.length>0){
			if (ALL.equalsIgnoreCase(parameters[0])){
				int count = sessionFactoryManager.removeAllSessions();
				response = "Total number of flushed Diameter Sessions are: " + count + "\n";
			} else if (TIME.equalsIgnoreCase(parameters[0]) && parameters.length > 1) {
				try {
					int staleTime = Integer.parseInt(parameters[1]);
					int count = sessionFactoryManager.removeIdleSession(staleTime * 60000);
					response = "Total number of flushed sessions are: " + count + "\n";
				} catch (NumberFormatException e){
					response = "Invalid time. Please give time in Number of Minutes.";
				}
			} else if (ID.equalsIgnoreCase(parameters[0]) && parameters.length > 1){
				if (sessionFactoryManager.hasSession(parameters[1])) {
					sessionFactoryManager.release(parameters[1]);
					response = "Session for Session-ID: " + parameters[1] + " succesfully removed\n";
				} else {
					response = "Session with Session-ID: " + parameters[1] + " does not exist\n";
				}
			} else if (isHelpSymbol(parameters[0])){
				response = getHelpMsg();
			}else {
				response = "Invalid Argument\n\n" + getHelpMsg();
			}
		} else {
			response = "Provide valid Arguments\n" + getHelpMsg();
		}
		return response;
	}

	@Override
	public String getHelpMsg() {
		StringBuilder responseBuilder=new StringBuilder();
		responseBuilder.append("\nUsage : clear diameter session [<options>]\n");
		responseBuilder.append("--------------------------------Possible Options:--------------------------------\n");
		responseBuilder.append(TIME + " <time-in-minutes>	 	  : Flushes Diameter Sessions idle for given time\n");
		responseBuilder.append(ALL + "							  : Flushes All Diameter Sessions\n");
		responseBuilder.append(ID + " <session-id>				  : Flushes specific Session of given Session-ID\n");
		return responseBuilder.toString();
	}

	@Override
	public String getKey() {
		return SESSION;
	}

	@Override
	public String getDescription() {
		return "Flushes Diameter Sessions";
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}

	@Override
	public String getHotkeyHelp() {
		return "'"+SESSION+"':{'"+ALL+"':{},'"+TIME+"':{},'"+ID+"':{},'"+HELP+"':{}}";
	}
}
