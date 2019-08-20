package com.elitecore.aaa.rm.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import com.elitecore.aaa.mibs.rm.chargingservice.server.RMChargingServiceMIBListener;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public abstract class ChargingServCommand extends EliteBaseCommand {	
	
	
	private final static String SUMMARY        	      = "-s";

	private final static String THREADS_QUEUE_DETAILS = "-t";
	
	private final static String HELP           		  = "?";
	
	private RMChargingServiceMIBListener rmChargingMIBListener;
	
	public ChargingServCommand(RMChargingServiceMIBListener rmChargingMIBListner) {
		this.rmChargingMIBListener = rmChargingMIBListner;
	}
	
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
		return "chargingserv";
	}

	@Override
	public String getDescription() {
		return "Displays Charging service detail as per option.";
	}
	
	private StringWriter getHelp(){
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);

		String paramName[] ={
				THREADS_QUEUE_DETAILS,
				SUMMARY};					 

		String paramDesc[] ={
				"Displays detail of Threads and Queue currently in used.",
				"Displays Charging Service summary."
		};
		
		out.println("Usage : chargingserv <options>");
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
		stringBuilder.append("Try 'chargingserv ?'.");
		
		return stringBuilder.toString();
		
	}

	public abstract String getServiceThreadSummary();
	
	private String getServiceRequestSummary(){
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append("\n    Charging Service Request Summary");
		responseBuilder.append("\n---------------------------------------");
		responseBuilder.append("\nCharging-Request          :" + rmChargingMIBListener.getRMChargingServTotalRequests());
		responseBuilder.append("\nCharging-Response         :"+  rmChargingMIBListener.getRMChargingServTotalResponses());
		responseBuilder.append("\nDropped                   :" + rmChargingMIBListener.getRMChargingServTotalPacketsDropped());
		responseBuilder.append("\nDuplicate Request         :" + rmChargingMIBListener.getRMChargingServTotalDupRequests());
		responseBuilder.append("\nUnknown Type Request      :" + rmChargingMIBListener.getRMChargingServTotalUnknownTypes());
		responseBuilder.append("\nMalformed Request         :" + rmChargingMIBListener.getRMChargingServTotalMalformedRequests());
		responseBuilder.append("\nInvalid Request           :" + rmChargingMIBListener.getRMChargingServTotalInvalidRequests());
		responseBuilder.append("\nBad Authenticator Request :" + rmChargingMIBListener.getRMChargingServTotalBadAuthenticators());

		responseBuilder.append("\nAccess Request            :" + rmChargingMIBListener.getRmChargingServTotalAccessRequest());        
		responseBuilder.append("\nAccess Accept             :" + rmChargingMIBListener.getRmChargingServTotalAccessAccept());         
		responseBuilder.append("\nAccess Reject             :" + rmChargingMIBListener.getRMChargingServTotalAccessRejects());        
		responseBuilder.append("\nAccouting Request         :" + rmChargingMIBListener.getRmChargingServTotalAcctRequest());            
		responseBuilder.append("\nAccouting Response        :" + rmChargingMIBListener.getRmChargingServTotalAcctResponse());          
		responseBuilder.append("\nAccouting Start           :" + rmChargingMIBListener.getRmChargingServTotalAcctStartRequest());  
		responseBuilder.append("\nAccouting Stop            :" + rmChargingMIBListener.getRmChargingServTotalAcctStopRequest());    
		responseBuilder.append("\nAccouting Update          :" + rmChargingMIBListener.getRmChargingServTotalAcctUpdateRequest());
		
		return responseBuilder.toString();
	}

	@Override
	public String getHotkeyHelp() {
		return "{'chargingserv':{'-s':{},'-t':{},'?':{}}}";
	}

}
