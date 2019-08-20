package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

/**
 * @Command: Version
 * 
 * @author jatin.zaveri
 *
 */
public class VersionCommand extends EliteBaseCommand{

	String serverName = "";
	String serverVersion = "";
	public VersionCommand(String serverName,String strVersion){
		this.serverName = serverName;
		this.serverVersion = strVersion;
	}
	public String execute(String parameter) {
		String responseMessage = "";
		if(parameter!=null && parameter.length() > 0){
			responseMessage = getHelp();
			return responseMessage;
		}
		else{
		return serverName + " [ " + serverVersion + " ]";
		}
	}

	public String getCommandName() {
		return "version";
	}

	public String getDescription() {
		return "Displays name and version of Server.";
	}
	private String getHelp(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() );
		out.println("Description : " + getDescription() );
		return stringWriter.toString();
	}
	@Override
	public String getHotkeyHelp() {
		return "{'version':{'-help':{}}}";
	}
}
