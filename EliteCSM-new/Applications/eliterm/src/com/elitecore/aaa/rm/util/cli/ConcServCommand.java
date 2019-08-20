package com.elitecore.aaa.rm.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import com.elitecore.aaa.mibs.rm.concurrentloginservice.server.RMConcServiceMIBListener;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public abstract class ConcServCommand extends EliteBaseCommand {	
	
	
	private final static String SUMMARY        	      = "-s";

	private final static String THREADS_QUEUE_DETAILS = "-t";
	
	private final static String HELP           		  = "?";
	
	@Override
	public String execute(String parameter) {
		// TODO Auto-generated method stub
		
		String response = "--";
		
		if(parameter != null){

			StringTokenizer tokenizer = new StringTokenizer(parameter, " ");
			String commandParameters=null;	
			
			if (tokenizer.hasMoreTokens())
				parameter= tokenizer.nextToken();
			
			if (tokenizer.hasMoreTokens())
				commandParameters= tokenizer.nextToken();
			
			if(parameter.trim().length() > 0 ){				
				if(parameter.trim().equals(SUMMARY)){
					response = getServiceRequestSummary();					
				}else if(parameter.trim().equals(THREADS_QUEUE_DETAILS)){
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
		return "concserv";
	}

	@Override
	public String getDescription() {
		return "Displays Concurrent Login service detail as per option.";
	}
	
	private StringWriter getHelp(){
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		String paramName[] ={
				THREADS_QUEUE_DETAILS,
				SUMMARY};					 

		String paramDesc[] ={
				"Displays detail of Threads and Queue currently in used.",
				"Displays Concurrent Login Service summary."
		};
		
		out.println("Usage : concserv <options>");
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
		stringBuilder.append("Try 'concserv ?'.");
		
		return stringBuilder.toString();
		
	}

	public abstract String getServiceThreadSummary();
	
	private String getServiceRequestSummary(){
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\n    Concurrent Login Service Request Summary");
		responseBuilder.append("\n---------------------------------------");
		responseBuilder.append("\nConcurrent-Login-Request  :" + RMConcServiceMIBListener.getRMConcServTotalRequests());
		responseBuilder.append("\nConcurrent-Login-Response :"+ RMConcServiceMIBListener.getRMConcServTotalResponses());
		responseBuilder.append("\nDropped                   :" + RMConcServiceMIBListener.getRMConcServTotalPacketsDropped());
		responseBuilder.append("\nDuplicate Request         :" + RMConcServiceMIBListener.getRMConcServTotalDupRequests());
		responseBuilder.append("\nUnknown Type Request      :" + RMConcServiceMIBListener.getRMConcServTotalUnknownTypes());
		responseBuilder.append("\nMalformed Request         :" + RMConcServiceMIBListener.getRMConcServTotalMalformedRequests());
		responseBuilder.append("\nInvalid Request           :" + RMConcServiceMIBListener.getRMConcServTotalInvalidRequests());
		responseBuilder.append("\nBad Authenticator Request :" + RMConcServiceMIBListener.getRMConcServTotalBadAuthenticators());

		return responseBuilder.toString();
	}

	@Override
	public String getHotkeyHelp() {
		return "{'concserv':{'-s':{},'-t':{},'?':{}}}";
	}

}
