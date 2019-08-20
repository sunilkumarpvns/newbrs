package com.elitecore.aaa.radius.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public class ClientsCommand extends EliteBaseCommand {
	private AAAServerContext serverContext = null;
	public ClientsCommand(AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}

	@Override
	public String execute(String parameter) {
		if(parameter == null || parameter.length() <= 0){
			return getAllClientsDetail();
		}else if(HELP_OPTION.equalsIgnoreCase(parameter.trim()) || "?".equalsIgnoreCase(parameter.trim())){
			return getUsageInfo();
		}else{
			return getClient(parameter);
		}
	}
	private String getDottedLine(){
		return "--------------------------------------------------------------------------------";			   
	}
	private String getAllClientsDetail(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
	/*	out.println(getDottedLine());
		out.println("                                 Active Clients");
		out.println(getDottedLine());*/
		RadClientConfiguration radClientConfiguration = serverContext.getServerConfiguration().getRadClientConfiguration();
		if(radClientConfiguration != null){
			out.println(radClientConfiguration.getAllClients());
		}else{
			out.println("                         Client Configuration not found.");
		}
		out.print(getDottedLine());
		return stringBuffer.toString();
	}

	private String getClient(String ipAddress){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println(getDottedLine());
		out.println("                                 Client Detail");
		out.println(getDottedLine());
		RadClientConfiguration radClientConfiguration = serverContext.getServerConfiguration().getRadClientConfiguration();
		if(radClientConfiguration != null){
			RadClientData clientData = radClientConfiguration.getClientData(ipAddress.trim());
			if(clientData != null){
				out.println(clientData.toString());
			}else{
				out.println("No such client exist!");				
				out.println("Kindly verify the IP and Retry.");
			}
			
		}else{
			out.println("                         Client Configuration not found.");
		}		
		out.print(getDottedLine());
		return stringBuffer.toString();		
	}
	@Override
	public String getCommandName() {	
		return "clients";
	}

	@Override
	public String getDescription() {
		return "Displays Clients Details.";
	}
	
	private String getUsageInfo(){
	
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() + " [<options>]");
		out.println("Description : " + getDescription() );
		out.println();
		out.println(fillChar("where options include:", 30));		
		out.println("    " + fillChar("ip-address", 30));
		out.println("    " + fillChar("", 5) +fillChar(" Displays Specific Clients Details.", 30));
		return stringWriter.toString();
	}

	@Override
	public String getHotkeyHelp() {
		return "{'clients':{'-help':{}}}";
	}
}
