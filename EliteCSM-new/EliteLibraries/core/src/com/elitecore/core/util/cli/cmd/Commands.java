package com.elitecore.core.util.cli.cmd;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.elitecore.core.util.cli.JMXException;
import com.elitecore.core.util.cli.JMXInterface;

public class Commands {
	
	private static final String DEFAULT_GROP = "";
	private static final String BLANK_SPACE = "            ";
	
	private Map<String, ICommand> serverCommands;
	private Map<String, LinkedHashMap<String, ICommand>> groupWiseCommands;
	
	private String serverName;
	private String host;
	private int port;
	
	public Commands(String ipAddress, int port) {
		this(ipAddress, port, "");
	}

	public Commands(String ipAddress, int port, String serverName) {
		this.host = ipAddress;
		this.port = port;
		this.serverName = serverName;
		
		try {
			JMXInterface.getInstance().init(host, port);
		} catch (JMXException e) {
			//e.printStackTrace();
		}
		
		serverCommands = new LinkedHashMap<String, ICommand>();
		groupWiseCommands = new LinkedHashMap<String, LinkedHashMap<String,ICommand>>();

	}
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getServerName() {
		return serverName;
	}
	
	public void addCommand(ICommand command) {
		addCommand(DEFAULT_GROP, command);
	}
	
	public synchronized void addCommand(String group, ICommand command) {

		if (group == null || group.trim().length() == 0){
			group = DEFAULT_GROP;
		}
		
		serverCommands.put(command.getCommandName(),command);
		LinkedHashMap <String, ICommand> groupCommands = groupWiseCommands.get(group);
		
		if(groupCommands == null){
			groupCommands = new LinkedHashMap<String, ICommand>();
			groupWiseCommands.put(group, groupCommands);
		}
		groupCommands.put(command.getCommandName(), command);
	}

	
	public ICommand getCommand(String command) {
		return serverCommands.get(command);
	}

	public ICommand getGroupCommand(String group, String command) {

		HashMap <String, ICommand> groupCommands = groupWiseCommands.get(group);
		
		if(groupCommands != null){
			return groupCommands.get(command);
		}

		return null;
	}
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		Iterator<String> iterator = groupWiseCommands.keySet().iterator();
		
		while(iterator.hasNext()){
			String groupName = iterator.next();
			out.println();
			if (groupName.length() > 0) {
				out.println(groupName);
				out.println();
			}
			LinkedHashMap<String, ICommand> groupCommands = groupWiseCommands.get(groupName);
			Iterator<String> iterator1 = groupCommands.keySet().iterator();
			while(iterator1.hasNext()){
				String commandName = iterator1.next();
				ICommand command = groupCommands.get(commandName);
				out.println(commandName + BLANK_SPACE.substring(commandName.length()) + command.getDescription());
			}
			out.println();
		}
		out.println();
		//out.println("  * Use ? with command to get detailed description.");
		out.close();
		return stringBuffer.toString();
		
	}
}
