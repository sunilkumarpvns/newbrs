package com.elitecore.netvertex.cli;

import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.diameterapi.core.common.session.SessionsFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DiameterSessionCommand extends EliteBaseCommand {

	private static final String COUNT = "count";
	private static final String FLUSH = "flush";
	private static final String ALL = "all";
	private static final String TIME = "time";
	private static final String ID = "id";
	private static final String DIAMETERSESS = "diametersess";

	private SessionsFactory sessionFactory;
	public DiameterSessionCommand(SessionsFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public String execute(String parameter) {
		String[] parameters = parameter.split(" ");
		String response = "";
		
		if (parameters == null || parameters.length == 0){
			return getHelpMsg();
		}
		
		if (HELP.equals(parameters[0]) || "?".equals(parameters[0])){
			return getHelpMsg();
		}
		

		if (COUNT.equalsIgnoreCase(parameters[0])){
			return "Total Diameter Session count is: " + sessionFactory.getSessionCount();
		} else if (  parameters.length > 1 && FLUSH.equalsIgnoreCase(parameters[0])){
			if (ALL.equalsIgnoreCase(parameters[1])){
				StringWriter stringWriter = new StringWriter();
				PrintWriter out = new PrintWriter(stringWriter);
				int count = sessionFactory.removeAllSessions();
				out.println("Total number of flushed Diameter Sessions are: " + count);
				return stringWriter.toString();
			} else if (parameters.length > 2 && TIME.equalsIgnoreCase(parameters[1])) {
				try {
					int staleTime = Integer.parseInt(parameters[2]);
					if(staleTime < 0){
						return "Invalid time. Please give time in positive number of minutes";
					}
					int count = sessionFactory.removeIdleSession(staleTime * 60000l);
					return "Total number of flushed sessions are: " + count;
				} catch (NumberFormatException e){
					return "Invalid time. Please give time in number of minutes";
				}
			} else if (parameters.length > 2 && ID.equalsIgnoreCase(parameters[1])){
				if (sessionFactory.hasSession(parameters[2])){
					sessionFactory.getOrCreateSession(parameters[2]).release();
					return "Session for Session-ID: " + parameters[2] + " succesfully removed\n";
				} else {
					return "Session with Session-ID: " + parameters[2] + " does not exist\n";
				}
			} else {
				return "Invalid argument" + getHelpMsg();
			}
			
		}
	
		response = "Invalid argument" + getHelpMsg();
	
		
		return response;
	}

	@Override
	public String getCommandName() {
		return DIAMETERSESS;
	}

	@Override
	public String getDescription() {
		return "Displays Diameter Session count and flushes Diameter Sessions";
	}

	@Override
	public String getHotkeyHelp() {
		return "{'"+DIAMETERSESS+"':{'"+COUNT+"':{},'"+FLUSH+"':{'"+ALL+"':{},'"+TIME+"':{},'"+ID+"':{}}}}";
	}

	@Override
	public String getHelpMsg() {
		String [] paramName ={COUNT,	FLUSH +" "+TIME + " <time-in-minutes>", FLUSH + " " + ALL, FLUSH + " " + ID +" <Session-ID>"};


		String [] paramDesc ={"Display count of Diameter Session",
				"Flushes Diameter Sessions idle for given time in minutes",
				"Flushes All Diameter Sessions",
				"Flushes specific Session of given Session-ID",

		};
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : diametersess <options>");
		out.println("Possible options"  );
		out.println();
		out.println(StringUtility.fillChar("", 96, '-'));
		out.println();
		for(int i=0;i<paramDesc.length;i++){
			out.println("    " + fillChar(paramName[i],35)  +  paramDesc[i]   );
		}

		out.close();		
		return stringWriter.toString();
	}
}
