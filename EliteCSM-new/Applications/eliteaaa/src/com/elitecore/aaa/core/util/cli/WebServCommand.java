package com.elitecore.aaa.core.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public abstract class WebServCommand extends EliteBaseCommand {
	private final static String THREADS_QUEUE_DETAILS = "-t";
	private final static String HELP           		  = "?";
	
	@Override
	public String execute(String parameter) {
		
		String response = "--";
		
		if(parameter != null){

			StringTokenizer tokenizer = new StringTokenizer(parameter, " ");
			
			if (tokenizer.hasMoreTokens())
				parameter= tokenizer.nextToken();
					
			if(parameter.trim().length() > 0 ){				
				if(parameter.trim().equals(THREADS_QUEUE_DETAILS)){
					response = getServiceThreadSummary();
				}else if(parameter.trim().equals(HELP)){
					response = getHelp().toString();
				}else{
					response = getErrorMsg().toString();
				}				
			}else{ // if parameter's length is  = 0;
				StringWriter stringWriter = new StringWriter();
				stringWriter.append("Required parameter missing.");
				stringWriter.append("\n");
				stringWriter.append(getHelp().toString());
				response = stringWriter.toString();
			}
		}else{ // if parameter is null;
			StringWriter stringWriter = new StringWriter();
			stringWriter.append("Required parameter missing.");
			stringWriter.append("\n");
			stringWriter.append(getHelp().toString());
			response = stringWriter.toString();			
		}
		
		return response;
	}

	@Override
	public String getCommandName() {
		return "webserv";
	}

	@Override
	public String getDescription() {
		return "Displays Web service detail as per option.";
	}

	@Override
	public String getHotkeyHelp() {
		return "{'webserv':{'-t':{}}}";
	}
	private StringWriter getHelp(){

		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		String paramName[] ={THREADS_QUEUE_DETAILS};					 

		String paramDesc[] ={
				"Displays detail of Threads and Queue currently in used."};
		
		out.println("Usage : webserv <options>");
		out.println("Possible options"  );
		for(int i=0;i<paramDesc.length;i++){
			out.println("    " + fillChar(paramName[i],52)  +  paramDesc[i]   );
		}
		
		out.close();		
		return stringWriter;	
	
	}
	private String getErrorMsg(){		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Invalid Parameter or Parameter format is not correct.");
		stringBuilder.append("\n");
		stringBuilder.append("Try 'webserv ?'.");
		
		return stringBuilder.toString();
		
	}
	public abstract String getServiceThreadSummary();

}
