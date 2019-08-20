package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
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
		detailProviderMap = new HashMap<String ,DetailProvider>();
		this.sessionFactoryManager = sessionFactoryManager;
	}
	
	@Override
	public String execute(String[] parameters) {
		
		if(parameters == null || parameters.length == 0){
			return "Provide valid arguments" + getHelpMsg();
		}
		
		if(isHelpSymbol(parameters[0])){
			return getHelpMsg();
		}
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		
		if (ALL.equalsIgnoreCase(parameters[0])){
			int count = sessionFactoryManager.removeAllSessions();
			out.println("Total number of flushed Diameter Sessions are: " + count);
			return stringWriter.toString();
		} else if (parameters.length > 1 && TIME.equalsIgnoreCase(parameters[0])) {
			try {
				int staleTime = Integer.parseInt(parameters[1]);
				if(staleTime < 0){
					return "Invalid time. Please give time in positive Number of Minutes";
				}
				int count = sessionFactoryManager.removeIdleSession(staleTime * 60000l);
				return "Total number of flushed sessions are: " + count;
			} catch (NumberFormatException e){
				return "Invalid time. Please give time in Number of Minutes";
			}
		} else if (parameters.length > 1 && ID.equalsIgnoreCase(parameters[0])){
			if (sessionFactoryManager.hasSession(parameters[1])) {
				sessionFactoryManager.release(parameters[1]);
				return "Session for Session-ID: " + parameters[1] + " succesfully removed";
			} else {
				return "Session with Session-ID: " + parameters[1] + " does not exist";
			}
		}
		
		return "Invalid argument" + getHelpMsg();
	}

	@Override
	public String getHelpMsg() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : clear diameter session [<options>]");
		out.println("--------------------------------Possible Options:--------------------------------");
		out.println(" " + TIME +"  <time-in-minutes>        : Flushes Diameter Sessions idle for given time");
		out.println(" " + ALL  +"                           : Flushes All Diameter Sessions");
		out.println(" " + ID   +"  <session-id>             : Flushes specific Session of given Session-ID");
		out.close();
		return stringWriter.toString();
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
		return "'"+SESSION+"':{'"+ALL+"':{},'"+TIME+"':{},'"+ID+"':{}}";
	}
}
