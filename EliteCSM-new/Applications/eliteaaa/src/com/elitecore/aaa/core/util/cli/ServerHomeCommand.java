package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

/**
 * @Command: ServerHome Command
 * 
 * @author jatin.zaveri
 *
 */
public class ServerHomeCommand extends EliteBaseCommand{
	String serverHome = "";
	public ServerHomeCommand(String home){
		this.serverHome = home;
	}
	public String execute(String parameter){
		String responseMessage = "";
		if(parameter!=null && parameter.length() > 0){
			responseMessage = getHelp();
			return responseMessage;
		}
		else{
		return serverHome;
		}
	}

	public String getCommandName(){
		return "home";
	}
	
	public String getDescription() {
		return "Displays home folder of server instance.";
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
		return "{'home':{'-help':{}}}";
	}
}
