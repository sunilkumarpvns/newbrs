package com.elitecore.aaa.rm.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.SessionMgrCommand;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerData;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.util.cli.TableFormatter;

public class RMSessionMgrCommand extends SessionMgrCommand{

	public RMSessionMgrCommand(AAAServerContext serverContext) {
		super(serverContext);
	}
	
	@Override
	protected TableFormatter getTableFormatter(int format, String columnSeparator) {
		String[] header = {"ID", "NAME", "TYPE", "STATUS"};
		int [] width = {4, 30, 5, 7};
		int [] alignment = {TableFormatter.LEFT, TableFormatter.LEFT, 
				TableFormatter.LEFT, TableFormatter.LEFT};
		return new TableFormatter(header, width, alignment, format, columnSeparator);
	}
	
	@Override
	protected String getListOptionHelp() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println("\nUsage : sessionmgr  -list ["+CSV+"]");
		out.println(StringUtility.fillChar("-", 80, '-'));
		out.println("Description : \n");
		out.println("	Display the List of Session Manager with respective Type and Status Information");
		out.println("    Displays CSV formatted output with "+CSV+" option");
		out.flush();
		out.close();
		return stringWriter.toString();
	}
	
	@Override
	protected String getSessionManagerList( TableFormatter formatter, 
			Map<String, ConcurrencySessionManager> sessionMgrMap,
			Map<String, SessionManagerData> allSessionMngrConf) {
		if(formatter == null)
			return "";
		Set<String> allSessMgrSet = null;
		Set<String> activeSet;
		for (ConcurrencySessionManager value : sessionMgrMap.values()) {
			formatter.addRecord(new String[]{
					String.valueOf(value.getSmInstanceId()), 
					value.getSmInstanceName(), 
					value.getSMType(), 
					getStatus(value.isAlive())});

		}
		// key set for active session managers
		activeSet = sessionMgrMap.keySet();
		allSessMgrSet = new HashSet<String>(allSessionMngrConf.keySet());
		// key set for not initialized session managers
		allSessMgrSet.removeAll(activeSet);
		for (String key : allSessMgrSet) {
			SessionManagerData config = allSessionMngrConf.get(key);
			formatter.addRecord(new String[]{
					String.valueOf(config.getInstanceId()), 
					config.getInstanceName(), 
					config.getType(), 
					"NI"});
		}

		formatter.add("L-LOCAL", TableFormatter.LEFT);
		formatter.add("R-REMOTE", TableFormatter.LEFT);
		formatter.add("A-ALIVE", TableFormatter.LEFT);
		formatter.add("D-DEAD", TableFormatter.LEFT);
		formatter.add("NI-NOT INITIALIZED", TableFormatter.LEFT);
		return formatter.getFormattedValues();
	}
}
