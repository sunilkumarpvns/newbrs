package com.elitecore.netvertex.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;

public class DiameterShowSessionDetailProvider extends DetailProvider {

	private static final String COUNT = "count";
	private static final String SESSION = "session";
	
	private HashMap<String ,DetailProvider> detailProviderMap;
	private SessionFactoryManager sessionFactoryManager;
	
	public DiameterShowSessionDetailProvider(SessionFactoryManager sessionFactoryManager) {
		this.sessionFactoryManager = sessionFactoryManager;
		this.detailProviderMap = new HashMap<String ,DetailProvider>();
	}
	
	@Override
	public String execute(String[] parameters) {
		if(parameters == null || parameters.length == 0){
			return "Provide valid arguments" + getHelpMsg();
		}
		
		if(isHelpSymbol(parameters[0])){
			return getHelpMsg();
		}

		if (COUNT.equalsIgnoreCase(parameters[0])){
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println("Total Diameter Session count is: " + sessionFactoryManager.getSessionCount());
			out.close();
			return stringWriter.toString();
		}
		
		return "Invalid argument" + getHelpMsg();
	
	} 

	@Override
	public String getHelpMsg() {
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : show diameter session [<options>]");
		out.println("--------------------------------Possible Options:--------------------------------");
		out.println(" " + COUNT + "              : Display total numbers of Diameter and Routing sessions");
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
		return "'"+SESSION+"':{'"+COUNT+"':{}}";
	}
}
